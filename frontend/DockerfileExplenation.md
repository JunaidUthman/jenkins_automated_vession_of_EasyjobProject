# Angular Dockerfile: Deep Dive Documentation
This Dockerfile uses a multi-stage build to optimize the final production image. It separates the heavy environment needed to compile Angular code from the lightweight environment needed to serve it.

# # Stage 1: The Build Stage (Node.js)
The purpose of this stage is to transform TypeScript code and SCSS into optimized, minified static files (HTML, JS, CSS).

FROM node:18-alpine AS builder

Starts the build process using Node.js 18.

The alpine tag ensures we use a lightweight Linux distribution to keep the build process fast.

The AS builder alias allows us to reference this stage later in the process.

WORKDIR /app

Creates and switches to the /app directory inside the container. All subsequent commands will run from this location.

COPY package.json package-lock.json ./

Copies only the dependency files first.

Reason: This allows Docker to cache the npm install step. If you change your code but don't change your dependencies, Docker skips the slow download process.

RUN npm install

Executes inside the container to download all libraries listed in package.json into the node_modules folder.

COPY . .

Copies the rest of your application source code into the container's /app folder.

RUN npm run build --prod

Runs the Angular CLI production build. This creates a dist/ folder containing the highly optimized version of your app.

# # Stage 2: The Production Stage (Nginx)
The purpose of this stage is to take the compiled files and serve them using a high-performance web server.

FROM nginx:alpine

Starts a brand-new, fresh environment.

We throw away the Node.js image, the node_modules (often 500MB+), and the source code. We only keep the Nginx server.

COPY --from=builder /app/dist/your-project-name /usr/share/nginx/html

This is the "Bridge" command.

It reaches back into the builder stage, finds the compiled files in /app/dist/your-project-name, and copies them into Nginx's default "public" folder.

Note: /usr/share/nginx/html is the standard location where Nginx looks for files to serve to users.

EXPOSE 80

Documents that the container will listen for traffic on port 80 (the standard HTTP port).

CMD ["nginx", "-g", "daemon off;"]

The final command that starts Nginx.

daemon off; is required because Docker containers need the main process to stay in the foreground; otherwise, the container would stop immediately after starting.