#!/usr/bin/env bash
set -euo pipefail

CLUSTER_NAME="${1:-codeconnect-local}"

echo "=========================================================="
echo "🗑️ Deleting k3d cluster: ${CLUSTER_NAME}"
echo "=========================================================="

k3d cluster delete "${CLUSTER_NAME}" || true
echo "✅ Cluster '${CLUSTER_NAME}' deleted."
