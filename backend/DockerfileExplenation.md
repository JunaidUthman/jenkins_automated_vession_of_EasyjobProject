# Stage 1: The Build Stage (The Construction Site)
When you run docker build, Docker looks at Stage 1 and does the following:

FROM maven:3.9.9-eclipse-temurin-23 AS build Docker downloads (or pulls from cache) a large image that has Java 23 and Maven already installed. Think of this as renting a fully equipped workshop.

WORKDIR /app Inside that workshop, Docker creates a folder called /app and "steps into" it. Every command after this happens inside that folder.

COPY pom.xml . Docker reaches outside the container (to your computer) and grabs your pom.xml. It puts a copy of it into the /app folder inside the container.

RUN mvn dependency:go-offline -B This is the clever part. Docker starts the Maven "workshop" and tells it: "Look at the pom.xml and download every library (Spring, Hibernate, etc.) right now." * Why? These libraries are saved in a "layer." Next time you change your code but NOT your pom.xml, Docker will skip this long download and use the "cached" libraries.

COPY src ./src Now that the libraries are ready, Docker copies your actual Java source code from your computer into the container.

RUN mvn clean package -DskipTests Docker runs the compiler. Maven takes the src and the libraries and crushes them together into one single file: target/backend-0.0.1-SNAPSHOT.jar.

# Stage 2: The Runtime Stage (The Finished House)
Once Stage 1 finishes, Docker essentially "pauses" that environment. It then sees a second FROM and starts a completely new process.

FROM eclipse-temurin:23-jre-alpine Docker throws away the Maven workshop. It starts a brand new, tiny environment. This image doesn't have Maven, and it doesn't have the "JDK" (compiler); it only has the "JRE" (the engine needed to run code). It’s about 1/10th the size of the first image.

WORKDIR /app Again, it creates a fresh /app folder in this new, empty environment.

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar This is the most important line. Docker reaches back into the "frozen" Stage 1 workshop, finds that specific .jar file in the target folder, and copies only that file into this new Stage 2 environment.

The source code, the Maven caches, and the heavy tools are left behind and deleted.

EXPOSE 8080 This is just a note saying, "Hey, this app likes to talk on port 8080."

ENTRYPOINT ["java", "-jar", "app.jar"] This sets the "On" switch. When you finally run the container, it immediately executes the command to start your Spring Boot app.