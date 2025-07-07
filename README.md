# Secure App

This project is a microservice web application with a React UI and a Spring Boot REST API.

## Running Locally

### Backend

To run the backend, navigate to the `backend` directory and run the following command:

```bash
./mvnw spring-boot:run
```

### Frontend

To run the frontend, navigate to the `frontend` directory and run the following command:

```bash
npm start
```

### Frontend Features

The React UI now includes enhanced features for managing products and orders:

*   **Navigation:** Use the top navigation bar to switch between "Products" and "Orders" sections.
*   **Product Management (CRUD):**
    *   **List Products:** View all products in a table.
    *   **Add Product:** Click the "Add Product" button to open a form for creating new products.
    *   **Edit Product:** Click the "Edit" button next to a product to modify its details.
    *   **Delete Product:** Click the "Delete" button next to a product to remove it.
*   **Order Management:**
    *   **List Orders:** View all orders, including their associated order items and product details.
    *   **Create New Order:** Click the "Create New Order" button to open a form where you can specify a customer name and add multiple products with quantities to the order.

## Testing

### Backend Unit Tests

To run the unit tests for the backend (using an in-memory H2 database), navigate to the `backend` directory and run the following command:

```bash
./mvnw test
```

### API Contract Tests (Pact)

To run the Pact verification tests for the backend, navigate to the `backend` directory and run the following command:

```bash
./mvnw test
```

This will verify that the backend API adheres to the contracts defined in the Pact files located in `backend/src/test/resources/pacts`. This includes contracts for Product, Order, and OrderItem APIs.

## CI/CD Pipeline

This project utilizes GitHub Actions for its CI/CD pipeline, automating build, testing, security scanning, and deployment processes.

### Workflow Overview

The main workflow is defined in `.github/workflows/build-and-deploy.yml` and consists of the following jobs:

*   **SAST (Static Application Security Testing):** Uses CodeQL to analyze the codebase for potential security vulnerabilities.
*   **SCA (Software Composition Analysis):** Uses Trivy to scan Docker images for known vulnerabilities in dependencies and operating system packages.
*   **Build and Push Docker Images:** Builds Docker images for both the backend and frontend applications and pushes them to Docker Hub.
*   **Generate Kustomize Manifests:** Generates environment-specific Kubernetes manifests using Kustomize.
*   **DAST (Dynamic Application Security Testing):** (Conceptual) A placeholder for running DAST scans against a deployed application.
*   **ArgoCD Integration:** (Conceptual) Explains how ArgoCD would be used for GitOps-based deployments.

### Docker Hub Credentials

To allow the pipeline to push Docker images, you need to configure the following GitHub Secrets in your repository settings (`Settings > Secrets and variables > Actions`):

*   `DOCKER_HUB_USERNAME`: Your Docker Hub username.
*   `DOCKER_HUB_TOKEN`: Your Docker Hub Personal Access Token (PAT).

### Kustomize for Environment-Specific Deployments

The project uses Kustomize to manage Kubernetes manifests for different environments (dev, stage, prod). The structure is as follows:

```
kubernetes/
├── base/
│   ├── backend.yaml
│   ├── frontend.yaml
│   └── kustomization.yaml
└── overlays/
    ├── dev/
    │   └── kustomization.yaml
    ├── stage/
    │   └── kustomization.yaml
    └── prod/
        └── kustomization.yaml
```

*   **`base/`**: Contains the common, base Kubernetes manifests for the backend and frontend.
*   **`overlays/`**: Contains environment-specific configurations. Each environment (`dev`, `stage`, `prod`) has its own `kustomization.yaml` that references the `base` and applies patches (e.g., different replica counts, environment-specific labels).

During the CI/CD pipeline, Kustomize builds the final manifests for each environment, which can then be used for deployment.

### ArgoCD GitOps Integration (Conceptual)

ArgoCD is a declarative GitOps continuous delivery tool for Kubernetes. In this setup, ArgoCD would be configured to monitor the Git repository for changes in the generated Kustomized manifests (e.g., `manifests-dev.yaml`, `manifests-stage.yaml`, `manifests-prod.yaml`).

Upon detecting new commits to these manifest files, ArgoCD would automatically pull the latest changes and apply them to the respective Kubernetes clusters, ensuring that the cluster state always matches the desired state defined in Git.

**To set up ArgoCD:**

1.  Install ArgoCD in your Kubernetes cluster.
2.  Create ArgoCD `Application` resources that point to the `kubernetes/overlays/<environment>` directories in this Git repository.
3.  Configure ArgoCD to automatically sync changes from the repository to your clusters.

### DAST Integration (Conceptual)

Dynamic Application Security Testing (DAST) involves testing the application in its running state to identify vulnerabilities. In a typical CI/CD pipeline, DAST would be performed after the application has been deployed to a staging or test environment. This pipeline includes a placeholder step for DAST.

**Example DAST Integration Flow:**

1.  Deploy the application to a staging environment (e.g., using ArgoCD).
2.  Trigger a DAST tool (e.g., OWASP ZAP, Burp Suite) to scan the deployed application's URLs.
3.  Analyze the DAST scan results and fail the pipeline if critical vulnerabilities are found.

## Running with Kubernetes

To run the application with Kubernetes, you will need to have Docker and Kubernetes installed.

### Build Docker Images

First, build the Docker images for the backend and frontend:

```bash
docker build -t secure-app-backend backend
docker build -t secure-app-frontend frontend
```

### Kubernetes Deployment

Create a `kubernetes` directory and add the following deployment files:

**backend-deployment.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: secure-app-backend
        ports:
        - containerPort: 8080
```

**frontend-deployment.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: secure-app-frontend
        ports:
        - containerPort: 3000
```

**backend-service.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: backend
spec:
  selector:
    app: backend
  ports:
  - port: 8080
    targetPort: 8080
```

**frontend-service.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: frontend
spec:
  selector:
    app: frontend
  ports:
  - port: 3000
    targetPort: 3000
  type: LoadBalancer
```

### Apply Deployments

Apply the deployments to your Kubernetes cluster:

```bash
kubectl apply -f backend-deployment.yaml
kubectl apply -f frontend-deployment.yaml
kubectl apply -f backend-service.yaml
kubectl apply -f frontend-service.yaml
```