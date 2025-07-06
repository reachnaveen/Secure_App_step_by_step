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

## Testing

### Backend Unit Tests

To run the unit tests for the backend (using an in-memory H2 database), navigate to the `backend` directory and run the following command:

```bash
./mvnw test
```

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