# ============================================================
# ВариантУм Worker — асинхронный consumer Kafka-событий
# ============================================================

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /workspace/target/variantum-worker.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
