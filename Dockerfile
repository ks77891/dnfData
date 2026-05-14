# 构建阶段 - 使用 Maven 镜像
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 运行阶段 - 使用 Temurin JDK 17（官方推荐替代 openjdk）
FROM eclipse-temurin:17-jdk-alpine
ARG PORT=8080
ENV PORT=${PORT}
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]
