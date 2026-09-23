#!/usr/bin/env bash
set -euo pipefail

CLUSTER_NAME="${1:-codeconnect-local}"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "=========================================================="
echo "📦 Packaging Spring Boot microservices and Next.js frontend..."
echo "=========================================================="
mvn -f "${PROJECT_ROOT}/services/gateway-service/pom.xml" clean package -DskipTests
mvn -f "${PROJECT_ROOT}/services/user-service/pom.xml" clean package -DskipTests
npm --prefix "${PROJECT_ROOT}/frontend" run build

echo "=========================================================="
echo "🐳 Building Docker images..."
echo "=========================================================="
docker build -t codeconnect/gateway-service:latest -f "${PROJECT_ROOT}/services/gateway-service/Dockerfile" "${PROJECT_ROOT}/services/gateway-service"
docker build -t codeconnect/user-service:latest -f "${PROJECT_ROOT}/services/user-service/Dockerfile" "${PROJECT_ROOT}/services/user-service"
docker build -t codeconnect/frontend:latest -f "${PROJECT_ROOT}/frontend/Dockerfile" "${PROJECT_ROOT}/frontend"

echo "=========================================================="
echo "🚚 Importing images into k3d cluster '${CLUSTER_NAME}'..."
echo "=========================================================="
k3d image import \
  codeconnect/gateway-service:latest \
  codeconnect/user-service:latest \
  codeconnect/frontend:latest \
  -c "${CLUSTER_NAME}"

echo "✅ All images built and imported successfully into '${CLUSTER_NAME}'."
