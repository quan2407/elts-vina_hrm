# -------- Build stage --------
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -DskipTests package

# -------- Run stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
ENV PORT=8080
EXPOSE 8080
COPY --from=build /app/target/*.jar app.jar
CMD ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
