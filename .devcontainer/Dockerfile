FROM mcr.microsoft.com/vscode/devcontainers/java:17

COPY --chown=vscode:vscode ../backend /workspace/backend
COPY --chown=vscode:vscode ../frontend /workspace/frontend

USER vscode
WORKDIR /workspace/backend

RUN ./mvnw clean install
