# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies để build nhanh hơn
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# ---------- Run stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app


# Copy đúng file jar của bạn (loại trừ *.original)
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
