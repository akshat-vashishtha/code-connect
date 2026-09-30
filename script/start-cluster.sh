#!/bin/bash
set -e

# ==============================================================================
# Step 0: Move into the project root directory
# ==============================================================================
SCRIPT_DIR="$(dirname "$0")"
cd "$SCRIPT_DIR/.."
PROJECT_ROOT="$(pwd)"

# ==============================================================================
# Step 1: Create or Start the k3d Kubernetes Cluster
# ==============================================================================
echo "[1/7] Starting k3d Kubernetes cluster ('codeconnect-local')..."
if k3d cluster list codeconnect-local >/dev/null 2>&1; then
  echo "  Cluster already exists. Starting cluster..."
  k3d cluster start codeconnect-local
else
  echo "  Creating new k3d cluster with HTTP (8080) and HTTPS (8443) ports..."
  k3d cluster create codeconnect-local --port "8080:80@loadbalancer" --port "8443:443@loadbalancer"
fi

# ==============================================================================
# Step 2: Pre-load Base Infrastructure Docker Images into Cluster
# ==============================================================================
echo "[2/7] Checking & pre-loading base infrastructure images..."
INFRA_IMAGES=(
  "rancher/mirrored-pause:3.6"
  "rancher/mirrored-library-busybox:1.37.0"
  "rancher/klipper-lb:v0.4.17"
  "mongo:7.0"
  "redis:7.2-alpine"
  "apache/kafka:3.7.0"
)

for image in "${INFRA_IMAGES[@]}"; do
  # Pull missing images if not already cached on host machine
  if ! docker image inspect "$image" >/dev/null 2>&1; then
    echo "  Downloading missing image: $image..."
    docker pull "$image" || true
  else
    echo "  Image $image exists locally, skipping download."
  fi

  # Import image into k3d cluster containerd engine
  docker save "$image" | docker exec -i k3d-codeconnect-local-server-0 ctr -a /run/k3s/containerd/containerd.sock -n k8s.io images import - 2>/dev/null || k3d image import "$image" -c codeconnect-local || true
done

# ==============================================================================
# Step 3: Apply Namespaces, Quotas & Set Default Context
# ==============================================================================
echo "[3/7] Applying Namespaces, Quotas & LimitRanges..."
kubectl apply -f k8s/namespaces/namespace.yaml
kubectl apply -f k8s/namespaces/
kubectl config set-context --current --namespace=codeconnect-dev

# ==============================================================================
# Step 4: Apply Secrets & ConfigMaps
# ==============================================================================
echo "[4/7] Applying Secrets & ConfigMaps..."
kubectl apply -f k8s/secrets/
kubectl apply -f k8s/configmaps/

# ==============================================================================
# Step 5: Deploy Stateful Infrastructure (MongoDB & Redis)
# ==============================================================================
echo "[5/7] Deploying Stateful Infrastructure (MongoDB & Redis)..."
kubectl apply -R -f k8s/infrastructure/

# ==============================================================================
# Step 6: Build Microservice JARs & Load Docker Images
# ==============================================================================
echo "[6/7] Building Microservice JARs & Docker Images..."
echo "  Building user-service JAR & Docker image..."
mvn clean package -DskipTests -f services/user-service/pom.xml
docker build -t codeconnect/user-service:latest ./services/user-service

echo "  Building gateway-service JAR & Docker image..."
mvn clean package -DskipTests -f services/gateway-service/pom.xml
docker build -t codeconnect/gateway-service:latest ./services/gateway-service

echo "  Building curriculum-service JAR & Docker image..."
mvn clean package -DskipTests -f services/curriculum-service/pom.xml
docker build -t codeconnect/curriculum-service:latest ./services/curriculum-service

echo "  Building submission-service JAR & Docker image..."
mvn clean package -DskipTests -f services/submission-service/pom.xml
docker build -t codeconnect/submission-service:latest ./services/submission-service

echo "  Building sandbox-runner-service JAR & Docker image..."
mvn clean package -DskipTests -f services/sandbox-runner-service/pom.xml
docker build -t codeconnect/sandbox-runner-service:latest ./services/sandbox-runner-service

echo "  Building collab-service JAR & Docker image..."
mvn clean package -DskipTests -f services/collab-service/pom.xml
docker build -t codeconnect/collab-service:latest ./services/collab-service

echo "  Loading microservice images into k3d cluster..."
SERVICE_IMAGES=(
  "codeconnect/user-service:latest"
  "codeconnect/gateway-service:latest"
  "codeconnect/curriculum-service:latest"
  "codeconnect/submission-service:latest"
  "codeconnect/sandbox-runner-service:latest"
  "codeconnect/collab-service:latest"
)
for image in "${SERVICE_IMAGES[@]}"; do
  docker save "$image" | docker exec -i k3d-codeconnect-local-server-0 ctr -a /run/k3s/containerd/containerd.sock -n k8s.io images import - 2>/dev/null || k3d image import "$image" -c codeconnect-local || true
done

# ==============================================================================
# Step 7: Deploy Application Microservices & Ingress Rules
# ==============================================================================
echo "[7/7] Deploying Application Microservices & Ingress..."
kubectl apply -f k8s/services/
kubectl apply -f k8s/deployments/
kubectl apply -f k8s/autoscaling/
kubectl apply -f k8s/networking/
kubectl rollout restart deployment/gateway-service deployment/user-service deployment/curriculum-service deployment/submission-service deployment/sandbox-runner-service deployment/collab-service -n codeconnect-dev 2>/dev/null || true

echo "  Waiting for deployments to roll out..."
kubectl rollout status deployment/user-service -n codeconnect-dev --timeout=120s || true
kubectl rollout status deployment/curriculum-service -n codeconnect-dev --timeout=120s || true
kubectl rollout status deployment/submission-service -n codeconnect-dev --timeout=120s || true
kubectl rollout status deployment/sandbox-runner-service -n codeconnect-dev --timeout=120s || true
kubectl rollout status deployment/collab-service -n codeconnect-dev --timeout=120s || true
kubectl rollout status deployment/gateway-service -n codeconnect-dev --timeout=120s || true

echo "Cluster setup complete! Current status in codeconnect-dev:"
kubectl get pods,pvc,svc -n codeconnect-dev

