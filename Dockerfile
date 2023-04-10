FROM openjdk:11-jdk-slim AS builder
RUN DEBIAN_FRONTEND=noninteractive apt-get update \
    && apt-get install --no-install-recommends -y maven
WORKDIR /app
COPY pom.xml pom.xml
RUN mvn package "-DskipTests=true"; exit 0
COPY . .
RUN rm -rf ./src/test
RUN mvn package "-DskipTests=true"
RUN mv ./target/app.jar /app/app.jar

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/app.jar .
ENTRYPOINT ["java","-jar","app.jar"]