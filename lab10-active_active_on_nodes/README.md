# lab10-active_active_on_nodes - WAN Gateway on Real Nodes, via Blueprint

Getting WAN Gateway's ports and hostnames to agree across two
independently-administered machines - not the active-active topology itself,
already covered in `lab02-active_active` - is where most teams actually get
stuck. This lab brings back a bare-metal/physical-host deployment (the one
environment the Docker-based labs in this set skip) and removes that class of
mistake by generating both nodes' scripts from a single shared configuration
file instead of hand-editing two.

## Lab Goals

- Deploy the active-active WAN Gateway topology onto two real, separate hosts (not
  containers) - e.g. two AWS EC2 instances.
- Use a GigaSpaces **blueprint** to turn every configuration decision that used to
  live scattered across hand-edited bash scripts (hosts, ports, gateway/space
  names, install paths) into one `values.yaml`, filled in once.
- Generate the deploy scripts for both nodes from that single file, upload them,
  and run them - with no per-node editing and no chance of the two nodes silently
  disagreeing with each other.

## Why no Docker here

This lab exists specifically to cover the case the Docker-based labs don't: a real
multi-host deployment. Docker would validate a different deployment path than the
one this lab is teaching; using two real hosts (or EC2 instances standing in for
them) tests the actual thing being shipped - scripts meant to be uploaded to a
server and run there. See [`build-notes.md`](build-notes.md) for implementation
rationale and a live-testing investigation, neither needed to run the lab.

## Requirements to run the lab

Two real servers that can reach each other over the network are all this
takes - physical or virtual, any OS or cloud provider. This lab was tested
against two AWS EC2 instances; the notes below reflect that experience, but
nothing here requires EC2 specifically.

1. **Same network, both machines** (same VPC/subnet if using cloud instances).
   Keeps this close to the "two hosts with connectivity" setup that
   `wangateway_examples-main/twohost_example` (the original physical-host
   reference for this lab) assumes, without needing cross-region routing.
2. **Firewall rules**: let the two machines reach each other freely - XAP's
   GSCs/GSA/gateway agents use a range of dynamic LRMI ports beyond just the
   ones this lab pins explicitly, so allowing full node-to-node traffic is
   simpler than enumerating a fixed port list. Keep both machines closed to
   the public internet other than the access you need to reach them (e.g.
   SSH).

## What's actually in this lab directory

There is **no pre-built Maven project here** - only a blueprint,
[`blueprint/wan_gateway_nodes`](blueprint/wan_gateway_nodes), whose `templates/`
directory is the *template* for the Maven project (and the deploy scripts).
`gs.sh blueprint generate` is what turns it into an actual, buildable project, with
every `{{...}}` placeholder replaced by the corresponding value from
`values.yaml`. Until you generate it, `wan-space`/`wan-gateway`/`wan-feeder`
aren't real modules you can `cd` into and build - they're template content.
Module names follow standard Maven lowercase-hyphenated convention (unlike the
CamelCase `BillBuddy*` artifacts elsewhere in this training set).

- `wan-space` - the space PU template. No model classes: deployed once per node
  with `-p` overrides for the site-specific space name and gateway targets, same
  approach as `lab02-active_active`'s `BillBuddySpace`.
- `wan-gateway` - the WAN Gateway PU template (delegator and sink), deployed once
  per node with `-p` overrides for the site-specific gateway name, lookup hosts,
  and ports.
- `wan-feeder` - a single-class standalone client template. Unlike the other labs,
  there's no shared model module: it defines its own type on the fly with a
  `SpaceTypeDescriptor` and writes schema-free `SpaceDocument`s, so there's
  nothing to keep in sync between the feeder and the space/gateway PUs.
- `bin/` - the deploy scripts template (start-grid, deploy, stop, clean, feed,
  verify, for both sites), the part that's actually generated fresh
  per-environment from `values.yaml`.
- `wan-distribution` - no source of its own; depends on the other three modules
  purely to force build order, then uses `maven-assembly-plugin` to package
  `bin/` plus the three built jars (flattened into `lib/`) into one
  `wan-gateway-node.tar.gz`. That's the one file that actually needs to reach
  either node.

Once generated, `wan-space`/`wan-gateway`/`wan-feeder` build to plain jars - the
space and gateway ones need no `maven-assembly-plugin` bundling of their own
(unlike labs that depend on a shared model module), since there's no model
classpath to bundle in. `wan-distribution` is the only module that uses the
assembly plugin, and it's assembling the *other* modules' output, not its own.

## 1. Provide the shared configuration

Two ways to do this - pick whichever fits your workflow:

- **Edit the file once**: fill in
  [`blueprint/wan_gateway_nodes/values.yaml`](blueprint/wan_gateway_nodes/values.yaml)
  directly. Review that file for the current list of configuration variables.
- **Answer prompts interactively instead**: skip editing the file and just run
  `gs.sh blueprint generate` as shown in step 3. Since no values are passed on
  the command line, it walks through each configuration variable one at a
  time, showing the shipped default from `values.yaml` and letting you accept
  it or type an override. Nothing gets saved back to the file, so this suits a
  one-off run better than a repeatable one.

Either way, this is the only place any of this gets typed in.

## 2. Install GigaSpaces on both servers

Download the GigaSpaces XAP distribution matching `gigaspaces.version` and
unzip it on each server, at the exact path set in `gigaspaces.home`. This is a
normal install on a real machine, no different from setting up GigaSpaces on
any server you manage yourself - it just needs to happen before you ship the
generated project there, since the deploy/start-grid scripts assume `GS_HOME`
already exists at that path.

You'll need this installation path for `gigaspaces.home` to complete the next
step, blueprint generation.

## 3. Generate the project

```bash
cp -r blueprint/wan_gateway_nodes $GS_HOME/config/blueprints/
$GS_HOME/bin/gs.sh blueprint generate wan_gateway_nodes ~/wan-gateway-node
```

The `target` argument accepts a full path, so the generated project lands
exactly at `~/wan-gateway-node`. This generates a complete, real Maven project
plus a fully pre-configured `bin/`, with every value from step 1 already
substituted in.

From here, follow the generated project's own `README.md` for the rest
(build+package, ship the one resulting archive to both servers, run, feed,
verify, tear down) - it's written specifically for the values you just filled
in.

## Tear down

On each node (see the generated `bin/`):

```bash
./stop.sh
./clean.sh
```
