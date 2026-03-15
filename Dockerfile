# ==========================================
# 第一阶段：编译环境 (GraalVM JDK 25)
# ==========================================
FROM ghcr.io/graalvm/native-image-community:25 AS build

# 消灭 locale 警告
ENV LANG=C
ENV LC_ALL=C
WORKDIR /app

# 拷贝 Maven 基础文件
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# 挂载缓存加速
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline

# 拷贝全量源码
COPY src ./src

# 【核心修正】：合并所有编译步骤，去掉多余注释导致的代码断路
# 1. generate: 生成 .java 源码
# 2. compile: 编译为 .class 字节码 (这一步能立刻验证 build-helper 是否生效)
# 3. native:compile: AOT 静态分析并生成二进制
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw jte:generate && \
    echo "--- [Check 1] 验证 JTE 源码生成 ---" && \
    ls -R target/generated-sources/jte && \
    ./mvnw compile && \
    echo "--- [Check 2] 验证字节码编译 ---" && \
    find target/classes -name "*Jte*" && \
    ./mvnw native:compile -Pnative -DskipTests


# ==========================================
# 第二阶段：运行环境 (极简 Debian)
# ==========================================
FROM debian:bookworm-slim
WORKDIR /app

# 安装 Native Image 运行必需的运行时库
RUN apt-get update && apt-get install -y curl libc6 libz1 && rm -rf /var/lib/apt/lists/*

# 从 build 阶段拷贝二进制文件
# 注意：请确保你的 artifactId 确实是 ha-springboot，否则这里会报错
COPY --from=build /app/target/ha-springboot /app/ha-springboot

RUN mkdir -p /app/logs /tmp/jfr_repo
EXPOSE 8080 9090

ENTRYPOINT ["/app/ha-springboot"]