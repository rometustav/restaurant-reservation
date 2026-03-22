FROM maven:3.9-eclipse-temurin-25 AS backend-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM node:24-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
COPY frontend/package*.json ./frontend/
RUN cd frontend && npm install
COPY frontend/ ./frontend/
RUN apk add --no-cache openjdk25-jre

EXPOSE 8080 5173
CMD sh -c "java -jar app.jar & cd frontend && npm run dev"