package com.codeconnect.user.infrastructure.config;

import com.codeconnect.user.infrastructure.config.properties.MongoPoolProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCompressor;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SocketSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Customizer applying production connection pool, fail-fast timeout, wire compression,
 * and durability settings to the MongoDB Java client settings builder.
 */
@Configuration
@RequiredArgsConstructor
public class MongoPoolOptimizationConfig {

    private final MongoPoolProperties props;

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsCustomizer() {
        return builder -> {
            builder.applyToConnectionPoolSettings(this::configureConnectionPool);
            builder.applyToSocketSettings(this::configureSocketSettings);
            builder.applyToClusterSettings(this::configureClusterSettings);
            applyCompressors(builder);
            applyDurabilitySettings(builder);
        };
    }

    private void configureConnectionPool(ConnectionPoolSettings.Builder pool) {
        pool.minSize(props.minPoolSize())
            .maxSize(props.maxPoolSize())
            .maxWaitTime(props.maxWaitTimeMs(), TimeUnit.MILLISECONDS)
            .maxConnectionIdleTime(props.maxIdleTimeMs(), TimeUnit.MILLISECONDS)
            .maxConnectionLifeTime(props.maxLifeTimeMs(), TimeUnit.MILLISECONDS)
            .maintenanceFrequency(props.maintenanceFrequencyMs(), TimeUnit.MILLISECONDS);
    }

    private void configureSocketSettings(SocketSettings.Builder socket) {
        socket.connectTimeout((int) props.connectTimeoutMs(), TimeUnit.MILLISECONDS)
              .readTimeout((int) props.readTimeoutMs(), TimeUnit.MILLISECONDS);
    }

    private void configureClusterSettings(ClusterSettings.Builder cluster) {
        cluster.serverSelectionTimeout(props.serverSelectionTimeoutMs(), TimeUnit.MILLISECONDS);
    }

    private void applyCompressors(MongoClientSettings.Builder builder) {
        List<MongoCompressor> compressors = resolveCompressors();
        if (!compressors.isEmpty()) {
            builder.compressorList(compressors);
        }
    }

    private List<MongoCompressor> resolveCompressors() {
        List<MongoCompressor> compressorList = new ArrayList<>();
        if (props.compressors() == null) {
            return compressorList;
        }

        if (props.compressors().contains("snappy") && isClassPresent("org.xerial.snappy.SnappyInputStream")) {
            compressorList.add(MongoCompressor.createSnappyCompressor());
        }
        if (props.compressors().contains("zstd") && isClassPresent("com.github.luben.zstd.ZstdInputStream")) {
            compressorList.add(MongoCompressor.createZstdCompressor());
        }
        return compressorList;
    }

    private void applyDurabilitySettings(MongoClientSettings.Builder builder) {
        if (props.writeConcern() != null && !props.writeConcern().isBlank()) {
            WriteConcern wc = parseWriteConcern(props.writeConcern());
            builder.writeConcern(wc.withWTimeout(props.maxWaitTimeMs(), TimeUnit.MILLISECONDS));
        }

        if (props.readPreference() != null && !props.readPreference().isBlank()) {
            ReadPreference rp = ReadPreference.valueOf(props.readPreference());
            if (rp != null) {
                builder.readPreference(rp);
            }
        }
    }

    private WriteConcern parseWriteConcern(String writeConcern) {
        return switch (writeConcern.toLowerCase()) {
            case "majority" -> WriteConcern.MAJORITY;
            case "w1", "acknowledged" -> WriteConcern.W1;
            case "w2" -> WriteConcern.W2;
            case "w3" -> WriteConcern.W3;
            case "unacknowledged" -> WriteConcern.UNACKNOWLEDGED;
            default -> WriteConcern.valueOf(writeConcern);
        };
    }

    private boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}
