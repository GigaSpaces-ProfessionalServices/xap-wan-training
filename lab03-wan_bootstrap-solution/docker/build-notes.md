# Build notes: lab03-wan_bootstrap-solution

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

This lab reuses `lab02-active_active`'s grid-level Docker design (flat network, one shared `PropertyPlaceholderConfigurer`-driven module per PU type deployed twice) - see [lab02-active_active/docker/build-notes.md](../../lab02-active_active/docker/build-notes.md) for that rationale. This file covers what's specific to bootstrapping.

## Why the gateway sink is a plain bean, not `<os-gateway:sink>`

`os-gateway:sink`'s `requires-bootstrap` attribute is XSD-typed as `boolean`, not a string, and XML schema validation rejects a `${requiresBootstrap}` Spring placeholder there outright - the schema check runs before Spring's own placeholder resolution. `local-gateway-name`, `host`, `discovery-port`, etc. are all string-typed, so placeholders were never a problem for those.

The fix: declare the sink as a plain Spring bean (`GatewaySinkFactoryBean`) instead of the `<os-gateway:sink>` element. A bean property's value stays an ordinary string until Spring's own type conversion runs, which happens after placeholder resolution - so a placeholder there works fine. `GatewaySource` (for the sink's target list) is used the same way in place of the `<os-gateway:sources>`/`<os-gateway:source>` elements. The delegator stays `<os-gateway:delegator>`, since none of its attributes hit this problem.

This means the gateway PU stays one shared `PropertyPlaceholderConfigurer` module (`BillBuddyGateway`) deployed twice, exactly like `lab02-active_active`'s.

## `AdminBootStrap`: a one-shot client, not a Processing Unit

`AdminBootStrap` has no `sla.xml` and isn't deployed to a GSC - it's a normal reactor module built the same way as `BillBuddyAccountFeeder` (a self-contained uber jar in a plain `eclipse-temurin:17-jre` container).

`AdminBootstrapInitiator.java` waits internally for the US gateway's sink to see EMEA, then triggers the bootstrap and exits. It reads its manager locator from the `LOCATORS` environment variable, the same convention as the feeder's `LOCATORS`/`SPACE_NAME`.

## Why `us-feeder` and `bootstrap-initiator` have no `depends_on`

A service's `depends_on` target must exist under whatever profile set is active on that invocation, not whatever's already running. `us-deployer` is profile-gated behind `"us"`, so running the feeder or bootstrap step alone (without also passing `--profile us`) would fail Compose's own validation even when `us-deployer` is already up from an earlier invocation. Both services drop `depends_on` instead - safe here since this is an inherently manual, staged workflow (the documented step order already guarantees the dependency), and `AdminBootstrapInitiator` waits internally regardless.

## Why the compose file sets an explicit project name

`docker-compose.yaml` sets `name: wan-bootstrap` at the top level. Without it, Compose derives the project name from the containing directory's basename (`docker`) - and several labs in this training set have their compose file living in a directory literally named `docker/`, which would collide if two were ever brought up concurrently without tearing one down first.
