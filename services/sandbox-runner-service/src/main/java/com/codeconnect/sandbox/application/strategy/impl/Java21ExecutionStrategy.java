package com.codeconnect.sandbox.application.strategy.impl;

import com.codeconnect.sandbox.application.strategy.ExecutionStrategy;
import com.codeconnect.sandbox.domain.enums.ExecutionStatus;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.model.ExecutionResult;
import com.codeconnect.sandbox.domain.model.TestResultDetail;
import com.codeconnect.sandbox.infrastructure.config.properties.SandboxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class Java21ExecutionStrategy implements ExecutionStrategy {

    private final SandboxProperties properties;
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public\\s+(?:final\\s+)?class\\s+(\\w+)");

    @Override
    public String getSupportedLanguage() {
        return "JAVA";
    }

    @Override
    public ExecutionResult execute(CodeExecutionRequestedPayload submission) {
        long startTime = System.currentTimeMillis();
        String code = submission.code();

        if (code == null || code.isBlank()) {
            return new ExecutionResult(
                ExecutionStatus.COMPILATION_ERROR,
                0, 0,
                Collections.emptyList(),
                "",
                "Source code cannot be empty",
                0
            );
        }

        String className = extractClassName(code);
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("sandbox_java_");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return new ExecutionResult(
                    ExecutionStatus.ERROR,
                    0, 0,
                    Collections.emptyList(),
                    "",
                    "JavaCompiler not available in runtime environment",
                    System.currentTimeMillis() - startTime
                );
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaSourceFromString fileObject = new JavaSourceFromString(className, code);

            List<String> compileOptions = List.of(
                "-d", tempDir.toAbsolutePath().toString(),
                "-proc:none",
                "--release", "21"
            );

            JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                null,
                diagnostics,
                compileOptions,
                null,
                List.of(fileObject)
            );

            boolean compileSuccess = task.call();
            if (!compileSuccess) {
                StringBuilder errorBuilder = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diag : diagnostics.getDiagnostics()) {
                    errorBuilder.append(String.format("Line %d: %s\n", diag.getLineNumber(), diag.getMessage(null)));
                }
                long duration = System.currentTimeMillis() - startTime;
                return new ExecutionResult(
                    ExecutionStatus.COMPILATION_ERROR,
                    0, 0,
                    Collections.emptyList(),
                    "",
                    errorBuilder.toString().trim(),
                    duration
                );
            }

            // Execute compiled class in bounded isolated executor
            return runClassWithTimeout(tempDir, className, startTime);

        } catch (Exception ex) {
            log.error("Execution error for submissionId={}: {}", submission.submissionId(), ex.getMessage(), ex);
            long duration = System.currentTimeMillis() - startTime;
            return new ExecutionResult(
                ExecutionStatus.ERROR,
                0, 0,
                Collections.emptyList(),
                "",
                "Internal execution failure: " + ex.getMessage(),
                duration
            );
        } finally {
            if (tempDir != null) {
                cleanupTempDir(tempDir.toFile());
            }
        }
    }

    private ExecutionResult runClassWithTimeout(Path classDir, String className, long startTime) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();

        long timeoutMs = properties.timeoutMs() > 0 ? properties.timeoutMs() : 3000;

        Callable<String> executionTask = () -> {
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{classDir.toUri().toURL()})) {
                System.setOut(new PrintStream(stdoutStream, true));
                System.setErr(new PrintStream(stderrStream, true));

                Class<?> cls = Class.forName(className, true, classLoader);
                Method mainMethod = cls.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[]{});
                return stdoutStream.toString();
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        };

        Future<String> future = executor.submit(executionTask);
        try {
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
            long duration = System.currentTimeMillis() - startTime;

            TestResultDetail detail = new TestResultDetail(
                "Execution Test Suite",
                true,
                "main()",
                "Execution succeeded",
                stdoutStream.toString().trim(),
                "",
                duration
            );

            return new ExecutionResult(
                ExecutionStatus.PASSED,
                1, 1,
                List.of(detail),
                stdoutStream.toString().trim(),
                stderrStream.toString().trim(),
                duration
            );

        } catch (TimeoutException ex) {
            future.cancel(true);
            long duration = System.currentTimeMillis() - startTime;
            return new ExecutionResult(
                ExecutionStatus.TIMED_OUT,
                1, 0,
                Collections.emptyList(),
                stdoutStream.toString().trim(),
                "TimeLimitExceeded: Execution timed out after " + timeoutMs + "ms",
                duration
            );
        } catch (ExecutionException ex) {
            long duration = System.currentTimeMillis() - startTime;
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if (cause instanceof java.lang.reflect.InvocationTargetException ite && ite.getTargetException() != null) {
                cause = ite.getTargetException();
            }
            if (cause instanceof OutOfMemoryError) {
                return new ExecutionResult(
                    ExecutionStatus.MEMORY_EXCEEDED,
                    1, 0,
                    Collections.emptyList(),
                    stdoutStream.toString().trim(),
                    "MemoryLimitExceeded: JVM exceeded maximum memory threshold",
                    duration
                );
            }
            return new ExecutionResult(
                ExecutionStatus.RUNTIME_ERROR,
                1, 0,
                Collections.emptyList(),
                stdoutStream.toString().trim(),
                "Runtime error: " + cause.getMessage(),
                duration
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            long duration = System.currentTimeMillis() - startTime;
            return new ExecutionResult(
                ExecutionStatus.ERROR,
                0, 0,
                Collections.emptyList(),
                stdoutStream.toString().trim(),
                "Execution interrupted",
                duration
            );
        } finally {
            executor.shutdownNow();
        }
    }

    private String extractClassName(String sourceCode) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Solution";
    }

    private void cleanupTempDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    cleanupTempDir(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    private static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
