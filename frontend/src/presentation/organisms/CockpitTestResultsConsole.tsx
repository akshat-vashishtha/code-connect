'use client';

import React, { useState } from 'react';
import {
  CheckCircle2,
  XCircle,
  AlertTriangle,
  Clock,
  Zap,
  ArrowRight,
  ShieldCheck,
  Terminal,
  Cpu,
} from 'lucide-react';
import { SubmissionResultResponse } from '@/types/submission';
import { TestDiffViewer } from '@/presentation/molecules/TestDiffViewer';

interface CockpitTestResultsConsoleProps {
  isRunning: boolean;
  result: SubmissionResultResponse | null;
  onNextFoothold?: () => void;
}

export const CockpitTestResultsConsole: React.FC<CockpitTestResultsConsoleProps> = ({
  isRunning,
  result,
  onNextFoothold,
}) => {
  const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState<number>(0);

  if (isRunning) {
    return (
      <div className="flex flex-col items-center justify-center h-full p-8 text-center space-y-4">
        <div className="relative">
          <div className="w-12 h-12 rounded-full border-4 border-amber-500/20 border-t-amber-500 animate-spin" />
          <Zap className="w-5 h-5 text-amber-400 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 animate-pulse" />
        </div>
        <div className="space-y-1">
          <h4 className="text-sm font-semibold text-neutral-200">Executing inside Sandbox Container...</h4>
          <p className="text-xs text-neutral-400">Compiling Java 21 bytecode & evaluating test matrix</p>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="flex flex-col items-center justify-center h-full p-8 text-center text-neutral-500 space-y-2">
        <Terminal className="w-8 h-8 text-neutral-600" />
        <p className="text-xs font-medium">Click &quot;Run Code&quot; to execute your solution against test cases.</p>
      </div>
    );
  }

  const activeTest = result.testResults?.[selectedTestCaseIndex] || result.testResults?.[0];

  return (
    <div className="flex flex-col h-full bg-neutral-950 overflow-hidden text-neutral-200">
      {/* 1. Header Banner */}
      <div className="p-4 border-b border-neutral-800 shrink-0">
        {result.allPassed ? (
          <div className="p-4 rounded-xl bg-gradient-to-r from-emerald-950/80 via-emerald-900/40 to-teal-950/80 border border-emerald-500/40 shadow-lg shadow-emerald-950/50 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-emerald-500/20 text-emerald-400">
                <CheckCircle2 className="w-6 h-6" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-emerald-200 flex items-center gap-1.5">
                  Foothold Conquered! 🚀
                </h3>
                <p className="text-xs text-emerald-400/80">
                  All {result.testResults.length} test cases passed in {result.totalDurationMs}ms
                </p>
              </div>
            </div>
            {onNextFoothold && (
              <button
                onClick={onNextFoothold}
                className="px-4 py-2 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-neutral-950 text-xs font-semibold flex items-center gap-2 transition-all shadow-md shadow-emerald-500/20 hover:scale-105"
              >
                Next Foothold <ArrowRight className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        ) : result.status === 'COMPILATION_ERROR' ? (
          <div className="p-3.5 rounded-xl bg-rose-950/40 border border-rose-500/30 flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
            <div className="space-y-1">
              <h4 className="text-xs font-semibold text-rose-300">Compilation Error</h4>
              <p className="text-xs text-rose-400/80">
                The Java 21 compiler rejected the submission. Check syntax and imports.
              </p>
            </div>
          </div>
        ) : result.status === 'TIMED_OUT' ? (
          <div className="p-3.5 rounded-xl bg-amber-950/40 border border-amber-500/30 flex items-start gap-3">
            <Clock className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
            <div className="space-y-1">
              <h4 className="text-xs font-semibold text-amber-300">Time Limit Exceeded (3000ms)</h4>
              <p className="text-xs text-amber-400/80">
                Execution exceeded CPU execution threshold. Look for infinite loops or heavy operations.
              </p>
            </div>
          </div>
        ) : result.status === 'MEMORY_EXCEEDED' ? (
          <div className="p-3.5 rounded-xl bg-purple-950/40 border border-purple-500/30 flex items-start gap-3">
            <Cpu className="w-5 h-5 text-purple-400 shrink-0 mt-0.5" />
            <div className="space-y-1">
              <h4 className="text-xs font-semibold text-purple-300">Memory Limit Exceeded (128MB)</h4>
              <p className="text-xs text-purple-400/80">
                Sandbox memory ceiling exceeded. Avoid unbounded arrays or collections.
              </p>
            </div>
          </div>
        ) : (
          <div className="p-3.5 rounded-xl bg-rose-950/30 border border-rose-500/20 flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <XCircle className="w-5 h-5 text-rose-400" />
              <div>
                <h4 className="text-xs font-semibold text-rose-300">Tests Failed</h4>
                <p className="text-xs text-neutral-400">
                  {result.testResults.filter((t) => t.passed).length}/{result.testResults.length} test cases passed
                </p>
              </div>
            </div>
            <span className="text-xs text-neutral-400 font-mono">{result.totalDurationMs}ms</span>
          </div>
        )}
      </div>

      {/* 2. Compiler Output (If any) */}
      {result.compilerOutput && (
        <div className="p-4 border-b border-neutral-800 bg-neutral-900/40">
          <span className="text-xs font-semibold text-rose-400 mb-1.5 block">Compiler Output:</span>
          <pre className="p-3 rounded-lg bg-black/60 border border-neutral-800 font-mono text-xs text-rose-300 overflow-x-auto whitespace-pre-wrap max-h-36">
            {result.compilerOutput}
          </pre>
        </div>
      )}

      {/* 3. Test Cases Navigation & Details */}
      {result.testResults && result.testResults.length > 0 && (
        <div className="flex-1 flex flex-col overflow-hidden">
          {/* Test Tabs */}
          <div className="flex items-center gap-2 px-4 py-2.5 border-b border-neutral-800 bg-neutral-900/30 overflow-x-auto shrink-0">
            {result.testResults.map((tc, idx) => (
              <button
                key={tc.testCaseId || idx}
                onClick={() => setSelectedTestCaseIndex(idx)}
                className={`px-3 py-1.5 rounded-lg text-xs font-medium flex items-center gap-2 transition-all whitespace-nowrap ${
                  selectedTestCaseIndex === idx
                    ? 'bg-neutral-800 text-neutral-100 border border-neutral-700 shadow-sm'
                    : 'text-neutral-400 hover:text-neutral-200 hover:bg-neutral-900'
                }`}
              >
                {tc.passed ? (
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                ) : (
                  <XCircle className="w-3.5 h-3.5 text-rose-400" />
                )}
                <span>
                  Test {idx + 1}
                  {tc.hidden && ' (Hidden)'}
                </span>
                {tc.hidden && <ShieldCheck className="w-3 h-3 text-neutral-500" />}
              </button>
            ))}
          </div>

          {/* Test Case Detail Pane */}
          {activeTest && (
            <div className="flex-1 p-4 overflow-y-auto space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-semibold text-neutral-300">
                    Test Case {selectedTestCaseIndex + 1}
                  </span>
                  <span
                    className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full border ${
                      activeTest.passed
                        ? 'bg-emerald-950/40 text-emerald-400 border-emerald-500/30'
                        : 'bg-rose-950/40 text-rose-400 border-rose-500/30'
                    }`}
                  >
                    {activeTest.passed ? 'PASSED' : 'FAILED'}
                  </span>
                  {activeTest.hidden && (
                    <span className="text-[10px] font-medium px-2 py-0.5 rounded-full bg-neutral-800 text-neutral-400 border border-neutral-700">
                      Hidden Boundary Test
                    </span>
                  )}
                </div>
                <span className="text-xs text-neutral-500 font-mono">
                  {activeTest.executionTimeMs}ms
                </span>
              </div>

              {/* Input section if visible */}
              {!activeTest.hidden && activeTest.input && (
                <div className="space-y-1">
                  <span className="text-xs text-neutral-400 font-medium">Input:</span>
                  <pre className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800 font-mono text-xs text-neutral-200 overflow-x-auto">
                    {activeTest.input}
                  </pre>
                </div>
              )}

              {/* Diff Viewer */}
              <TestDiffViewer
                expected={activeTest.expectedOutput}
                actual={activeTest.actualOutput}
                passed={activeTest.passed}
                hidden={activeTest.hidden}
              />

              {/* Error Message if failing */}
              {activeTest.errorMessage && !activeTest.hidden && (
                <div className="p-3 rounded-lg bg-rose-950/30 border border-rose-500/20 text-xs text-rose-300 font-mono whitespace-pre-wrap">
                  {activeTest.errorMessage}
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
