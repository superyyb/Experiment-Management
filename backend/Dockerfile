# Multi-stage Spring Boot image (build skips tests; CI runs JUnit 5 + Testcontainers separately)
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# 复制构建文件
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 下载依赖（利用Docker缓存层）
RUN gradle dependencies --no-daemon || true

# 复制源代码
COPY src ./src

# 构建应用
RUN gradle build --no-daemon -x test

# 运行阶段
FROM openjdk:17-jre-slim

WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=build /app/build/libs/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]

