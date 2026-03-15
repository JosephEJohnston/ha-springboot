# ==========================================
# 第一阶段：编译环境 (拥抱 GraalVM JDK 25)
# 使用官方的 GraalVM 社区版 JDK 25 镜像
# ==========================================
FROM ghcr.io/graalvm/native-image-community:25 AS build
WORKDIR /app

# 强烈建议：GraalVM 基础镜像通常不带全局的 mvn 命令
# 把你 Spring Boot 项目自带的 Maven Wrapper 拷进去用，保证版本一致性
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# 完美保留你的挂载缓存神技，加速依赖下载
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline

# 拷贝源码并打包
COPY src ./src

# 执行 JDK 25 下的 AOT 原生编译 (-Pnative 激活原生配置)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean native:compile -Pnative -DskipTests


# ==========================================
# 第二阶段：运行环境 (极简至上，丢弃庞大的 JDK 25)
# ==========================================
FROM debian:bookworm-slim
WORKDIR /app

# Native Image 产物只依赖最基础的 C 运行库 (glibc 和 zlib)
RUN apt-get update && apt-get install -y curl libc6 libz1 && rm -rf /var/lib/apt/lists/*

# 从 build 阶段拷贝打好的【二进制可执行文件】，而不是 app.jar
# (假设你的 pom.xml 中 artifactId 是 ha-springboot，生成的文件同名)
COPY --from=build /app/target/ha-springboot /app/ha-springboot

# 预创建目录
RUN mkdir -p /app/logs /tmp/jfr_repo

EXPOSE 8080 9090

# 像启动 C/C++ 程序一样直接启动，享受几十毫秒的启动速度！
ENTRYPOINT ["/app/ha-springboot"]
