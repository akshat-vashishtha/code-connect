#!/bin/bash
echo "⏸️ Stopping k3d cluster 'codeconnect-local'..."
k3d cluster stop codeconnect-local
echo "✅ Cluster stopped successfully."
