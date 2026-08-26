# Build notes: lab03-wan_bootstrap-solution

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

This lab reuses `lab02-active_active`'s grid-level Docker design (flat network, one shared `PropertyPlaceholderConfigurer`-driven module per PU type deployed twice) - see [lab02-active_active/docker/build-notes.md](../../lab02-active_active/docker/build-notes.md) for that rationale. This file covers what's specific to bootstrapping.

## Why the gateway sink is a plain bean, not `<os-gateway:sink>`

`os-gateway:sink`'s `requires-bootstrap` attribute is XSD-typed as `boolean`, not `xs:string`. A `${requiresBootstrap}` Spring property placeholder there fails XML schema validation outright (`cvc-datatype-valid.1.2.1: '${requiresBootstrap}' is not a valid value for 'boolean'`) - Xerces validates the raw attribute text against the XSD type at parse time, before Spring's `PropertyPlaceholderConfigurer` ever runs. This is different from `local-gateway-name`, `host`, `discovery-port`, etc. (all `xs:string`-typed), which happily accept placeholder text.

The fix: declare the sink as a plain Spring `<bean>` (`org.openspaces.core.gateway.GatewaySinkFactoryBean`) instead of the `<os-gateway:sink>` XSD element. A bean's `<property>` value is an ordinary String until Spring's own type-conversion machinery runs, which happens after placeholder resolution - so `<property name="requiresBootstrap" value="${requiresBootstrap}" />` (setter `setRequiresBootstrap(boolean)`) works fine. `GatewaySource` (for the sink's `gatewaySources` list) is used the same way in place of `<os-gateway:sources>`/`<os-gateway:source>`. The delegator stays `<os-gateway:delegator>` - `local-gateway-name` etc. are `xs:string`-typed, so placeholders were never a problem there.

Both `GatewaySinkFactoryBean` and `GatewaySource` live in `xap-admin.jar` (not `xap-openspaces.jar`), but since this module has no Java source - only `pu.xml` - nothing needs to declare that as a Maven dependency; the deploying GSC already has the full distribution's classpath at runtime.

This means the gateway PU stays one shared `PropertyPlaceholderConfigurer` module (`BillBuddyGateway`) deployed twice, exactly like `lab02-active_active`'s `BillBuddyGateway`.

## `AdminBootStrap`: a one-shot client, not a Processing Unit

`AdminBootStrap` has no `sla.xml` and isn't deployed to a GSC - it's a normal reactor module built the same way as `BillBuddyAccountFeeder` (`xap-openspaces` and `xap-admin` both overridden to `compile` scope, `maven-assembly-plugin` with `descriptorRef: jar-with-dependencies`, plain `eclipse-temurin:17-jre` Dockerfile). `com.gigaspaces:xap-admin` (supplies `AdminFactory`/`Gateway`/`GatewaySinkSource`/`BootstrapResult`) isn't on Maven Central, but resolves from GigaSpaces' own repo at `https://maven-repository.openspaces.org`, the same repo `xap-openspaces` itself uses.

`AdminBootstrapInitiator.java` waits internally (`admin.getGateways().waitFor("US")`) for the US gateway's sink to see EMEA, then calls `bootstrapFromGatewayAndWait()`. It reads its manager locator from the `LOCATORS` environment variable, the same convention as the feeder's `LOCATORS`/`SPACE_NAME`.

## Why `us-feeder` and `bootstrap-initiator` have no `depends_on`

A service's `depends_on` target must exist under whatever profile set is active on that invocation, not whatever's already running. `us-deployer` is profile-gated behind `"us"`, so running `docker compose --profile feeder run --rm us-feeder` alone (without also passing `--profile us`) would fail Compose's own validation even when `us-deployer` is already up from an earlier `--profile us` invocation. Both services drop `depends_on` instead - safe here since this is an inherently manual, staged workflow (the documented step order already guarantees the dependency), and `AdminBootstrapInitiator` waits internally regardless.

## Why the compose file sets an explicit project name

`docker-compose.yaml` sets `name: wan-bootstrap` at the top level. Without it, Compose derives the project name from the containing directory's basename (`docker`) - and three labs in this training set have their compose file living in a directory literally named `docker/`, which would collide if two were ever brought up concurrently without tearing one down first.
