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

This project utilizes GitHub Actions for its CI/CD pipeline, automating build, testing, security scanning, and deployment processes across different environments.

### Workflow Overview

The main workflow is defined in `.github/workflows/build-and-deploy.yml` and consists of the following jobs:

*   **SAST (Static Application Security Testing):** Uses CodeQL to analyze the codebase for potential security vulnerabilities.
*   **SCA (Software Composition Analysis):** Uses Trivy to scan Docker images for known vulnerabilities in dependencies and operating system packages.
*   **Build and Push Docker Images:** Builds Docker images for both the backend and frontend applications and pushes them to Docker Hub, tagging them with the Git commit SHA.
*   **Deploy to Dev:** Deploys the application to the `dev` environment. This stage requires manual approval.
*   **Deploy to Stage:** Deploys the application to the `stage` environment. This stage requires manual approval and runs after successful deployment to `dev`.
*   **DAST (Dynamic Application Security Testing):** Runs an OWASP ZAP baseline scan against the deployed application in the `stage` environment to identify security vulnerabilities.
*   **Deploy to Prod:** Deploys the application to the `prod` environment. This stage requires manual approval and runs after successful DAST scans on `stage`.

### Manual Approvals

Deployment to `dev`, `stage`, and `prod` environments requires manual approval. This is configured using GitHub Environments, which provide protection rules. You will need to configure these environments in your GitHub repository settings (`Settings > Environments`).

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

*   **`base/`**: Contains the common, base Kubernetes manifests for the backend and frontend. Image names here are generic (e.g., `your_docker_username/secure-app-backend`) without specific tags.
*   **`overlays/`**: Contains environment-specific configurations. Each environment (`dev`, `stage`, `prod`) has its own `kustomization.yaml` that references the `base` and applies patches (e.g., different replica counts, environment-specific labels).

During the CI/CD pipeline, Kustomize dynamically sets the image tags (using the Git commit SHA) and builds the final manifests for each environment. These generated manifests are then committed back to the repository, ready for GitOps deployment.

### ArgoCD GitOps Integration (Conceptual)

ArgoCD is a declarative GitOps continuous delivery tool for Kubernetes. In this setup, ArgoCD would be configured to monitor the Git repository for changes in the generated Kustomized manifests (e.g., `manifests-dev.yaml`, `manifests-stage.yaml`, `manifests-prod.yaml`).

Upon detecting new commits to these manifest files, ArgoCD would automatically pull the latest changes and apply them to the respective Kubernetes clusters, ensuring that the cluster state always matches the desired state defined in Git.

**To set up ArgoCD:**

1.  Install ArgoCD in your Kubernetes cluster.
2.  Create ArgoCD `Application` resources that point to the `kubernetes/overlays/<environment>` directories in this Git repository.
3.  Configure ArgoCD to automatically sync changes from the repository to your clusters.

### DAST Integration

Dynamic Application Security Testing (DAST) involves testing the application in its running state to identify vulnerabilities. In this CI/CD pipeline, an OWASP ZAP baseline scan is performed after the application has been deployed to the `stage` environment. This ensures that security vulnerabilities are identified in a deployed environment before promotion to production.

**How it works:**

*   The `dast` job in the GitHub Actions workflow uses the `zaproxy/action` to run a ZAP scan.
*   The `target` for the scan should be the URL of your deployed staging environment (e.g., `http://stage.example.com`).
*   The `cmd` parameter specifies the ZAP command to execute. You can customize this for different scan types (e.g., full scan, API scan).
*   The DAST report (`dast_report.html`) is uploaded as a workflow artifact for review.

## Backstage Templates

This repository includes Backstage templates to streamline the creation of new services and infrastructure components. These templates can be registered with your Backstage instance to enable self-service creation.

### Registering Templates in Backstage

To make these templates available in your Backstage instance, you need to register them in your `app-config.yaml` (or equivalent configuration file) under the `scaffolder.locations` section. For example:

```yaml
scaffolder:
  locations:
    - type: url
      target: https://github.com/your-org/your-repo/blob/main/backstage-templates/spring-boot-microservice/template.yaml
      rules:
        - allow: [Template]
    - type: url
      target: https://github.com/your-org/your-repo/blob/main/backstage-templates/kubernetes-cluster-provisioner/template.yaml
      rules:
        - allow: [Template]
```

Replace `https://github.com/your-org/your-repo` with the actual URL of your GitHub repository.

### Available Templates

#### 1. Spring Boot Microservice Template

*   **Location:** `backstage-templates/spring-boot-microservice/template.yaml`
*   **Description:** This template provisions a new Spring Boot microservice project with a basic structure, including a `pom.xml` and a sample `main` application class. It's designed to get you started quickly with a new Java-based service.
*   **Usage:** When creating a new component in Backstage, select this template. You will be prompted to provide basic project information like `Component ID` and `Description`.

#### 2. Kubernetes Cluster Provisioner Template

*   **Location:** `backstage-templates/kubernetes-cluster-provisioner/template.yaml`
*   **Description:** This conceptual template facilitates the provisioning of new Kubernetes clusters. It demonstrates how you can integrate Infrastructure as Code (IaC) tools (like Terraform) into Backstage to automate cloud resource creation. The template includes placeholders for defining cluster name, cloud provider (AWS, Azure, GCP), and region.
*   **Usage:** Select this template when you need to provision a new Kubernetes cluster. You will be asked for cluster details, and upon submission, it will trigger a process (e.g., a GitHub Pull Request) to apply the IaC changes.

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