#!/bin/bash
echo "🔥 Deleting k3d cluster 'codeconnect-local' and purging all volume data..."
k3d cluster delete codeconnect-local
echo "✅ Cluster and all volume data deleted successfully."
