package com.codeconnect.user.infrastructure.config;

import com.codeconnect.user.infrastructure.config.properties.MongoPoolProperties;
import com.mongodb.MongoCompressor;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
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
public class MongoPoolOptimizationConfig {

    private final MongoPoolProperties props;

    public MongoPoolOptimizationConfig(MongoPoolProperties props) {
        this.props = props;
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsCustomizer() {
        return builder -> {
            // 1. Connection Pooling
            builder.applyToConnectionPoolSettings(pool -> pool
                .minSize(props.minPoolSize())
                .maxSize(props.maxPoolSize())
                .maxWaitTime(props.maxWaitTimeMs(), TimeUnit.MILLISECONDS)
                .maxConnectionIdleTime(props.maxIdleTimeMs(), TimeUnit.MILLISECONDS)
                .maxConnectionLifeTime(props.maxLifeTimeMs(), TimeUnit.MILLISECONDS)
                .maintenanceFrequency(props.maintenanceFrequencyMs(), TimeUnit.MILLISECONDS)
            );

            // 2. Socket & Cluster Selection Timeouts
            builder.applyToSocketSettings(socket -> socket
                .connectTimeout((int) props.connectTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout((int) props.readTimeoutMs(), TimeUnit.MILLISECONDS)
            );

            builder.applyToClusterSettings(cluster -> cluster
                .serverSelectionTimeout(props.serverSelectionTimeoutMs(), TimeUnit.MILLISECONDS)
            );

            // 3. Wire Compressors (Snappy & Zstandard) with defensive classpath checks
            List<MongoCompressor> compressorList = new ArrayList<>();
            if (props.compressors() != null) {
                if (props.compressors().contains("snappy") && isClassPresent("org.xerial.snappy.SnappyInputStream")) {
                    compressorList.add(MongoCompressor.createSnappyCompressor());
                }
                if (props.compressors().contains("zstd") && isClassPresent("com.github.luben.zstd.ZstdInputStream")) {
                    compressorList.add(MongoCompressor.createZstdCompressor());
                }
            }
            if (!compressorList.isEmpty()) {
                builder.compressorList(compressorList);
            }

            // 4. Durability & Read Preference
            if (props.writeConcern() != null && !props.writeConcern().isBlank()) {
                WriteConcern wc = switch (props.writeConcern().toLowerCase()) {
                    case "majority" -> WriteConcern.MAJORITY;
                    case "w1", "acknowledged" -> WriteConcern.W1;
                    case "w2" -> WriteConcern.W2;
                    case "w3" -> WriteConcern.W3;
                    case "unacknowledged" -> WriteConcern.UNACKNOWLEDGED;
                    default -> WriteConcern.valueOf(props.writeConcern());
                };
                builder.writeConcern(wc.withWTimeout(props.maxWaitTimeMs(), TimeUnit.MILLISECONDS));
            }

            if (props.readPreference() != null && !props.readPreference().isBlank()) {
                ReadPreference rp = ReadPreference.valueOf(props.readPreference());
                if (rp != null) {
                    builder.readPreference(rp);
                }
            }
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
