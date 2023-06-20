# Use the official Alpine Linux base image
FROM alpine:latest

# Set environment variables for Vault
ENV VAULT_VERSION=1.8.3
ENV VAULT_ADDR=http://localhost:9200
ENV VAULT_DEV_ROOT_TOKEN_ID=root

# Download and install Vault
RUN apk add --no-cache wget
RUN wget --quiet https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_linux_amd64.zip \
    && unzip vault_${VAULT_VERSION}_linux_amd64.zip \
    && mv vault /usr/local/bin/ \
    && rm vault_${VAULT_VERSION}_linux_amd64.zip

# Expose the Vault port
EXPOSE 9200

# Set the default command to run when the container starts
CMD ["vault", "server", "-dev"]
