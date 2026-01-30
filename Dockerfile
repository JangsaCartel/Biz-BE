# ---------- build stage ----------
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# 캐시 효율을 위해 먼저 복사
COPY gradle gradle
COPY gradlew gradlew
COPY build.gradle settings.gradle ./
# 프로젝트에 libs.versions.toml 있으면 포함
COPY gradle/libs.versions.toml gradle/libs.versions.toml

# 소스 복사
COPY src src

# WAR 빌드
RUN chmod +x gradlew && ./gradlew clean war -x test

# ---------- runtime stage ----------
FROM tomcat:9.0-jdk17-temurin
WORKDIR /usr/local/tomcat

# ROOT로 배포하려면 기존 ROOT 제거 후 교체
RUN rm -rf webapps/ROOT webapps/ROOT.war

# build 결과 WAR을 ROOT.war로 배치
COPY --from=build /app/build/libs/*.war webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]