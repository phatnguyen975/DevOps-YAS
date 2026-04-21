#!/usr/bin/env bash
set -euo pipefail

echo "Bootstrapping cluster with operators and CRDs"

helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo add strimzi https://strimzi.io/charts/
helm repo add elastic https://helm.elastic.co
helm repo add jetstack https://charts.jetstack.io
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator \
  --namespace postgres-operator \
  --create-namespace

helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator \
  --version 0.45.0 \
  --namespace kafka-operator \
  --create-namespace \
  --set watchAnyNamespace=true

helm upgrade --install elastic-operator elastic/eck-operator \
  --namespace elastic-system \
  --create-namespace

helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.12.0 \
  --set installCRDs=true \
  --set prometheus.enabled=false \
  --set webhook.timeoutSeconds=4 \
  --set admissionWebhooks.certManager.create=true

helm upgrade --install reloader stakater/reloader \
  --namespace reloader \
  --create-namespace

kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml

echo "Cluster bootstrapped successfully"
