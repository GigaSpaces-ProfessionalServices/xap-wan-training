# Runs the already-built uber jar (target/BillBuddyAccountFeeder-jar-with-dependencies.jar).
# Build the jar on the host first: mvn -pl BillBuddyAccountFeeder -am install
# Then: docker compose build us-feeder emea-feeder
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/BillBuddyAccountFeeder-jar-with-dependencies.jar app.jar

# LOCATORS:   comma-separated manager host(s), e.g. "us-manager"
# SPACE_NAME: the space to feed, e.g. "wanSpaceUS"
ENTRYPOINT ["sh", "-c", "java -Dcom.gs.jini_lus.locators=${LOCATORS} -jar app.jar ${SPACE_NAME}"]
