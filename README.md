# xap-wan-training

Training material for GigaSpaces XAP's WAN Gateway feature, covering five scenarios: one-directional and bidirectional replication, bootstrapping, replication filtering, and conflict resolution. Each solution lab below is an independently self-contained Maven reactor with its own Docker Compose setup - see a lab's own README.md for its specific goals, topology, and run instructions.

## Prerequisites

- Docker and Docker Compose v2
- Maven
- The `gigaspaces/smart-cache-enterprise:17.3.0` image

## Labs


|   | Lab                                                                          | Description                                           | Features                                                             |
|---|------------------------------------------------------------------------------|-------------------------------------------------------|----------------------------------------------------------------------|
| 1 | `lab01-active_passive`                                                       | WAN Gateway active-passive topology                   | one-directional replication, Spacedeck GigaSpaces cluster comparison |
| 2 | `lab02-active_active`                                                        | WAN Gateway active-active topology                    | bidirectional replication                                            |
| 3 | `lab03-wan_bootstrap-solution`                                               | WAN Gateway bootstrapping                             | pulling one site's existing data into another after the fact         |
|   | `lab03-wan_bootstrap-exercise`                                               | | |
| 4 | `lab04-wan_gateway_filter-solution` | WAN Gateway replication filter                        | selectively discarding replicated writes based on a field value      |
|   | `lab04-wan_gateway_filter-exercise` | | |
| 5 | `lab05-wan_gateway_conflict-solution` | WAN Gateway conflict resolution                       | reconciling records both sites modified independently                |
|   | `lab05-wan_gateway_conflict-exercise` | | |
| 6 | `lab10-active_active_on_nodes`                                               | the active-active topology deployed onto two real hosts | GigaSpaces blueprint, SpaceDocument, no Docker                       |

Each `-exercise` folder mirrors its `-solution` counterpart's module structure and Docker Compose setup exactly, with a few items marked as `TODO` for the student to complete - see the `-exercise` lab's own README.md for steps.
