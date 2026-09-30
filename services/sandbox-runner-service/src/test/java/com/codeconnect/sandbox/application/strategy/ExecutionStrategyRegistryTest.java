package com.codeconnect.sandbox.application.strategy;

import com.codeconnect.sandbox.application.strategy.registry.ExecutionStrategyRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionStrategyRegistryTest {

    @Mock
    private ExecutionStrategy javaStrategy;

    private ExecutionStrategyRegistry registry;

    @BeforeEach
    void setUp() {
        when(javaStrategy.getSupportedLanguage()).thenReturn("JAVA");
        registry = new ExecutionStrategyRegistry(List.of(javaStrategy));
    }

    @Test
    @DisplayName("Should resolve strategy by language case-insensitively")
    void shouldResolveLanguage() {
        assertThat(registry.resolve("JAVA")).isEqualTo(javaStrategy);
        assertThat(registry.resolve("java")).isEqualTo(javaStrategy);
        assertThat(registry.resolve(null)).isEqualTo(javaStrategy);
    }
}
