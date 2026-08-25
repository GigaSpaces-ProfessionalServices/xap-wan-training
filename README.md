# xap-wan-training

Training material for GigaSpaces XAP's WAN Gateway feature, covering five scenarios: one-directional and bidirectional replication, bootstrapping, replication filtering, and conflict resolution. Each solution lab below is an independently self-contained Maven reactor with its own Docker Compose setup - see a lab's own README.md for its specific goals, topology, and run instructions.

## Prerequisites

- Docker and Docker Compose v2
- Maven
- The `gigaspaces/smart-cache-enterprise:17.3.0` image

## Labs

- `lab01-active_passive` - WAN Gateway active-passive topology: one-directional replication, US to EMEA.
- `lab02-active_active` - WAN Gateway active-active topology: bidirectional replication between US and EMEA.
- `lab03-wan_bootstrap-solution` - WAN Gateway bootstrapping: pulling one site's existing data into another after the fact.
- `lab04-wan_gateway_filter-solution` - WAN Gateway replication filter: selectively discarding replicated writes based on a field value.
- `lab05-wan_gateway_conflict-solution` - WAN Gateway conflict resolution: reconciling records both sites modified independently.

The `xap-wan-training-lab{2,3,4,5}-exercise` folders are the unsolved counterparts to labs 2-5, not yet updated to match the solutions' current Docker-based setup.
