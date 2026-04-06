# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/tech-blog-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8888

# Set environment variables for MySQL (can be overridden by docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/tech_blog_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root_password

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
