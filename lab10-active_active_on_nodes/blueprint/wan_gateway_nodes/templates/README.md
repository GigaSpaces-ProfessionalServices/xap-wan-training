# Generated from the wan_gateway_nodes blueprint

Everything in this directory was generated from `values.yaml` by
`gs.sh blueprint generate`. This is a complete, ready-to-build Maven project
(`wan-space`, `wan-gateway`, `wan-feeder`, `wan-distribution`) plus a `bin/`
folder of deploy scripts that already carry the hosts, ports, and gateway
names you filled in. Nothing here needs hand-editing per node, which is the
whole point.

## 1. Build and package

```bash
mvn install
```

This builds all three PU/client jars, then `wan-distribution` (a
`maven-assembly-plugin` module with no source of its own) bundles `bin/` plus
the three jars - renamed and flattened into `lib/` - into a single archive:
`wan-distribution/target/wan-gateway-node.tar.gz`. That one file is everything
either node needs; nothing else in this project has to reach the servers.

## 2. Ship the SAME archive to BOTH nodes

```bash
for host in <siteA-host> <siteB-host>; do
  scp wan-distribution/target/wan-gateway-node.tar.gz <user>@$host:~/
  ssh <user>@$host "mkdir -p ~/wan-gateway-node && tar xzf wan-gateway-node.tar.gz -C ~/wan-gateway-node"
done
```

Replace `<user>` with whatever login your servers actually use (e.g.
`ec2-user` on Amazon Linux AMIs, `ubuntu` on Ubuntu images, or your own
account).

Both nodes get an identical extracted copy (`bin/` + `lib/`), jars included -
which node acts as `siteA` vs. `siteB` is decided purely by which scripts you
run where, not by anything different in what was shipped.

## 3. Run it

On the node you're using as `siteA`:

```bash
cd wan-gateway-node/bin
./start-grid-siteA.sh
# give it a few seconds to come up, then:
./deploy-siteA.sh
```

On the node you're using as `siteB`:

```bash
cd wan-gateway-node/bin
./start-grid-siteB.sh
./deploy-siteB.sh
```

## 4. Feed data and verify replication

From either node (or anywhere that can reach the target site's manager):

```bash
cd wan-gateway-node/bin
./feed.sh siteA 100
```

Then check both sides converged:

```bash
cd wan-gateway-node/bin
./verify.sh siteA
./verify.sh siteB
```

Both should show the same 100 `WanEvent` documents, regardless of which site they
were fed into.

## Tear down

On each node:

```bash
cd wan-gateway-node/bin
./stop.sh
./clean.sh
```
