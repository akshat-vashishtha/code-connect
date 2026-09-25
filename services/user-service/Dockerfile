# Stage 1: Spring Boot 3.3 Layer Extractor
FROM eclipse-temurin:21-jre-alpine AS extractor
WORKDIR /builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# Stage 2: Minimal, Hardened Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: Non-root user with UID 10001
RUN addgroup -g 10001 -S spring && adduser -u 10001 -S spring -G spring
USER spring:spring

# Copy extracted layers (least frequently changed first)
COPY --from=extractor --chown=spring:spring /builder/extracted/dependencies/ ./
COPY --from=extractor --chown=spring:spring /builder/extracted/spring-boot-loader/ ./
COPY --from=extractor --chown=spring:spring /builder/extracted/snapshot-dependencies/ ./
COPY --from=extractor --chown=spring:spring /builder/extracted/application/ ./

ENV JAVA_OPTS="-XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
