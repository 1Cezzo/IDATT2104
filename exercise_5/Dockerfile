# Use the node image as base
FROM node:latest

# Install Python and other necessary dependencies
RUN apt-get update && apt-get install -y python3 python3-pip

# Set the working directory
WORKDIR /usr/src/app

# Copy package.json and package-lock.json to container
COPY package*.json ./

# Install Node.js dependencies
RUN npm install

# Copy all other project files to container
COPY . .

# Expose port 3000
EXPOSE 3000

# Command to run the server
CMD ["node", "server.js"]
