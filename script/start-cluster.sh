#!/bin/bash
set -e

echo "🚀 [1/7] Creating k3d cluster 'codeconnect-local'..."
k3d cluster create codeconnect-local --port "8080:80@loadbalancer" --port "8443:443@loadbalancer" || k3d cluster start codeconnect-local

echo "🔧 [2/7] Pre-loading required k3s sandbox & infrastructure images..."
# Pull on host and pipe into containerd socket to bypass SSL proxy certificate issues
for img in rancher/mirrored-pause:3.6 rancher/mirrored-library-busybox:1.37.0 mongo:7.0 redis:7.2-alpine; do
  docker pull $img || true
  docker save $img | docker exec -i k3d-codeconnect-local-server-0 ctr -a /run/k3s/containerd/containerd.sock -n k8s.io images import - 2>/dev/null || k3d image import $img -c codeconnect-local || true
done

echo "📦 [3/7] Applying Namespaces, Quotas & LimitRanges..."
kubectl apply -f k8s/namespaces/namespace.yaml
kubectl apply -f k8s/namespaces/
kubectl config set-context --current --namespace=codeconnect-dev

echo "🔑 [4/7] Applying Secrets & ConfigMaps..."
kubectl apply -f k8s/secrets/
kubectl apply -f k8s/configmaps/

echo "🗄️ [5/7] Deploying Stateful Infrastructure (MongoDB & Redis)..."
kubectl apply -R -f k8s/infrastructure/

echo "🔨 [6/7] Building Microservice JARs & Docker Images..."
echo "  ↳ Building user-service JAR & Docker image..."
mvn clean package -DskipTests -f services/user-service/pom.xml
docker build -t codeconnect/user-service:latest ./services/user-service

echo "  ↳ Building gateway-service JAR & Docker image..."
mvn clean package -DskipTests -f services/gateway-service/pom.xml
docker build -t codeconnect/gateway-service:latest ./services/gateway-service

echo "  ↳ Loading microservice images into k3d cluster..."
for img in codeconnect/user-service:latest codeconnect/gateway-service:latest; do
  docker save $img | docker exec -i k3d-codeconnect-local-server-0 ctr -a /run/k3s/containerd/containerd.sock -n k8s.io images import - 2>/dev/null || k3d image import $img -c codeconnect-local
done

echo "⚡ [7/7] Deploying Application Microservices & Ingress..."
kubectl apply -f k8s/services/
kubectl apply -f k8s/deployments/
kubectl apply -f k8s/autoscaling/
kubectl apply -f k8s/networking/
kubectl rollout restart deployment/gateway-service deployment/user-service -n codeconnect-dev 2>/dev/null || true

echo "✅ Cluster setup complete! Current status in codeconnect-dev:"
kubectl get pods,pvc -n codeconnect-dev
