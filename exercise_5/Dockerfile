# Use the official Python image as the base
FROM python:latest

# Set up a working directory
WORKDIR /usr/src/app

# Copy the requirements file to the working directory
COPY requirements.txt .

# Install any dependencies specified in the requirements file
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application code to the working directory
COPY . .

# Define the command to run the Python script
CMD ["python", "your_script.py"]
