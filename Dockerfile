# Base Jenkins image (includes Java + agent)
FROM jenkins/jenkins:lts

USER root

# Install required dependencies and Docker CLI
RUN apt-get update && apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    sudo \
    libatomic1 \
    git \
    && rm -rf /var/lib/apt/lists/*

# Add Docker official GPG key
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker stable repository
RUN echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
    https://download.docker.com/linux/debian $(lsb_release -cs) stable" \
    > /etc/apt/sources.list.d/docker.list

# Install Docker CLI only
RUN apt-get update && apt-get install -y docker-ce-cli && rm -rf /var/lib/apt/lists/*

# Make sure Jenkins user can use Docker if you mount the socket
RUN usermod -aG sudo jenkins

# Switch back to Jenkins user
USER jenkins
