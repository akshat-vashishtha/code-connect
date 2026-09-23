#!/usr/bin/env bash
set -euo pipefail

CLUSTER_NAME="${1:-codeconnect-local}"

echo "=========================================================="
echo "🚀 Creating k3d cluster: ${CLUSTER_NAME}"
echo "   - Host port 80 -> Ingress port 80 (Traefik)"
echo "   - Host port 443 -> Ingress port 443"
echo "=========================================================="

if k3d cluster list | grep -q "^${CLUSTER_NAME}\b"; then
  echo "⚠️ Cluster '${CLUSTER_NAME}' already exists. Skipping creation."
else
  k3d cluster create "${CLUSTER_NAME}" \
    --api-port 6443 \
    -p "80:80@loadbalancer" \
    -p "443:443@loadbalancer" \
    --agents 1 \
    --wait
  echo "✅ k3d cluster '${CLUSTER_NAME}' created successfully."
fi

# Ensure kubectl context points to our local cluster
kubectl config use-context "k3d-${CLUSTER_NAME}"

echo ""
echo "Cluster nodes:"
kubectl get nodes -o wide
