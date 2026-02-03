# ---------------------------------------------------------------------------
# Stage 1 – build the fat JAR
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Pull in dependency metadata first so subsequent layers are cached
COPY pom.xml ./
RUN mvn dependency:resolve -q

# Copy source and build
COPY src/ src/
RUN mvn package -DskipTests -q

# ---------------------------------------------------------------------------
# Stage 2 – minimal runtime image
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/Banking-1.0-SNAPSHOT.jar app.jar

# ---------------------------------------------------------------------------
# Environment variables (all overridable at runtime via -e / --env-file)
# Defaults match application.properties where applicable.
# ---------------------------------------------------------------------------
ENV ORG_ID=your_org_id
ENV PAGE_ID=your_page_id
ENV PROFILING_SERVER=h.online-metrix.net
ENV API_KEY=your_api_key
ENV API_BASE_URL=https://h-api-sb.online-metrix.net
ENV USER4_NAME=
ENV USER4_EMAIL=
ENV USER4_PASSWORD=
ENV VALIDATION_IGNORE_EMAILS=
ENV SHOW_DEMO_ACCOUNTS=false

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
