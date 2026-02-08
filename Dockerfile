FROM jenkins/jenkins:lts

USER root

# Install Node 18 + libatomic
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs libatomic1 \
    && rm -rf /var/lib/apt/lists/*

# Make sure Java is installed (already in jenkins/jenkins:lts)
RUN java -version

USER jenkins
