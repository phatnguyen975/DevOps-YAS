#!/usr/bin/env bash
set -euo pipefail

echo "Bootstrapping cluster with operators and CRDs"

helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo add strimzi https://strimzi.io/charts/
helm repo add akhq https://akhq.io/
helm repo add elastic https://helm.elastic.co
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
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

helm upgrade --install opentelemetry-operator open-telemetry/opentelemetry-operator \
  --namespace observability-operators \
  --create-namespace

helm upgrade --install prometheus-operator-crds prometheus-community/prometheus-operator-crds \
  --namespace observability-operators \
  --create-namespace

helm upgrade --install grafana-operator oci://ghcr.io/grafana-operator/helm-charts/grafana-operator \
  --version v5.0.2 \
  --namespace observability-operators \
  --create-namespace

kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl create namespace keycloak --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n keycloak

kubectl create clusterrolebinding keycloak-operator-global-admin \
  --clusterrole=cluster-admin \
  --serviceaccount=keycloak:keycloak-operator \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl set env deployment/keycloak-operator WATCH_NAMESPACE="" -n keycloak

echo "Cluster bootstrapped successfully"
