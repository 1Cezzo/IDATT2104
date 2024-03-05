FROM python:3.11.7

# Set working directory
WORKDIR /app

# Install Flask and docker
RUN pip install Flask

RUN pip install docker

# Copy the rest of the application code
COPY . .

# Set user
USER root

# Command to run the application
CMD ["python", "app.py"]
