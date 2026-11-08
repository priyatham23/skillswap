# ==============================================================================
# SkillSwap - Multi-Stage Production Dockerfile for Render Deployment
# ==============================================================================

# Stage 1: Build stage with OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy dependency libraries and source code
COPY lib ./lib
COPY src ./src

# Create binary output directory and compile all Java sources
RUN mkdir -p bin && \
    find src -name "*.java" > sources.txt && \
    javac -cp "lib/*" -d bin @sources.txt

# Stage 2: Minimal Production Runtime with OpenJDK 21 JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create necessary upload and data directories
RUN mkdir -p web/uploads/avatars data

# Copy compiled classes and libraries
COPY --from=builder /app/bin ./bin
COPY lib ./lib

# Copy static frontend assets, initial datastore, schemas, and configurations
COPY web ./web
COPY data ./data
COPY database ./database
COPY config.properties ./

# Default Render HTTP port
ENV PORT=8080
EXPOSE 8080

# Run the SkillSwap Java backend
CMD ["sh", "-c", "java -cp \"bin:lib/*\" com.skillswap.Main"]

