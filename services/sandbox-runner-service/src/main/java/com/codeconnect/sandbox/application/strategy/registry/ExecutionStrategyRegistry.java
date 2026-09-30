package com.codeconnect.sandbox.application.strategy.registry;

import com.codeconnect.sandbox.application.strategy.ExecutionStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry resolving the appropriate language execution strategy dynamically in O(1) time.
 */
@Component
public class ExecutionStrategyRegistry {

    private final Map<String, ExecutionStrategy> strategyMap;

    public ExecutionStrategyRegistry(List<ExecutionStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toUnmodifiableMap(
                s -> s.getSupportedLanguage().toUpperCase(),
                Function.identity()
            ));
    }

    public ExecutionStrategy resolve(String language) {
        String key = language != null ? language.toUpperCase() : "JAVA";
        return Optional.ofNullable(strategyMap.get(key))
            .or(() -> Optional.ofNullable(strategyMap.get("JAVA")))
            .orElseThrow(() -> new IllegalArgumentException("Unsupported execution language: " + language));
    }
}
