# 第一阶段：编译环境
FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /app
# 利用 Docker 缓存机制，先拷 pom.xml 下载依赖
# 拷贝 pom.xml
COPY pom.xml .
# 核心修改：使用 --mount 将 Maven 本地仓库挂载到宿主机缓存中
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline
# 拷贝源码并打包
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行环境
FROM eclipse-temurin:25-jdk
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
# 从第一阶段拷贝打好的 jar 包
COPY --from=build /app/target/*.jar app.jar
# 预创建 JFR 目录
RUN mkdir -p /app/logs /tmp/jfr_repo
# 暴露业务和监控端口
EXPOSE 8080 9090

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
