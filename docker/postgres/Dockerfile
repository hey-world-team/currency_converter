# Use the official PostgreSQL base image from Docker Hub
FROM postgres:latest

# Set environment variables for PostgreSQL
ENV POSTGRES_USER=$POSTGRES_USER
ENV POSTGRES_PASSWORD=$POSTGRES_PASSWORD
ENV POSTGRES_DB=$POSTGRES_DB

# Copy initialization scripts to the container
#COPY init-scripts/ /docker-entrypoint-initdb.d/

# Define a volume for persisting data
VOLUME ["/var/lib/postgresql/data"]

# Expose the default PostgreSQL port
EXPOSE 5432

# Set the default command to run when the container starts
CMD ["postgres"]
