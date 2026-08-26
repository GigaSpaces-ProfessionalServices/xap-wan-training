# Runs the already-built uber jar (target/AdminBootStrap-jar-with-dependencies.jar).
# Build the jar on the host first: mvn -pl AdminBootStrap -am install
# Then: docker compose build bootstrap-initiator
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/AdminBootStrap-jar-with-dependencies.jar app.jar

# LOCATORS: the US-site manager, e.g. "us-manager" (read via System.getenv,
# no shell interpolation needed since it's not baked into the command line).
ENTRYPOINT ["java", "-jar", "app.jar"]
