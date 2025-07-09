FROM openjdk:17-jdk
COPY build/libs/app.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]




# 1단계: 빌드 이미지
#FROM gradle:8.4-jdk17 AS builder
#WORKDIR /app
#COPY . .
RUN #./gradlew build --no-daemon

# 2단계: 실행 이미지
#FROM openjdk:17-alpine
#COPY --from=builder /app/build/libs/*.jar /app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app.jar"]