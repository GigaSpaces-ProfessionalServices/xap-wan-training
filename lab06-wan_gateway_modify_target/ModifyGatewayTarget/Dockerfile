# Runs the already-built uber jar (target/ModifyGatewayTarget-jar-with-dependencies.jar).
# Build the jar on the host first: mvn -pl ModifyGatewayTarget -am install
# Then: docker compose build modify-target
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/ModifyGatewayTarget-jar-with-dependencies.jar app.jar

# LOCATORS:     the site's own manager, e.g. "us-manager"
# SPACE_NAME:   the space whose target list to change, e.g. "wanSpaceUS"
# GATEWAY_NAME: the target gateway name to add or remove, e.g. "EMEA"
# ACTION:       "add" or "remove"
# All read via System.getenv, no shell interpolation needed since none is
# baked into the command line.
ENTRYPOINT ["java", "-jar", "app.jar"]
