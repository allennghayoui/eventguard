# EventGuard

![CI](https://github.com/allennghayoui/eventguard/actions/workflows/ci.yml/badge.svg)

A SIEM (Security Information and Event Management) system built in Java
with Spring Boot, structured around Clean Architecture. Ingests syslog
events, parses them into structured fields, evaluates detection rules,
and emits alerts - all with a domain core that has no framework dependencies.

## Why I Built EventGuard
As someone interested in Cybersecurity and with a software development background,
I like trying to replicate the tools that offensive and defensive security professionals
use. I've been wanting to learn Java for a while now to add it to my arsenal of
programming languages and thought that a SIEM would make the perfect first
project to build with because it's in a rich domain (security event analysis).

EventGuard is just an MVP for now and it is definitely not a full grade SIEM meant to
be used in real security environments.

## Quick Demo
60 seconds to a working system with sample data:

```bash
git clone https://github.com/allennghayoui/eventguard.git
cd eventguard
docker compose up -d                            # start app + Postgres
chmod +x ./demo/reset.sh ./demo/run-demo.sh     # grant execute permissions to the scripts
./demo/reset.sh && ./demo/run-demo.sh           # run the demo scripts
```

You'll see 6 alerts fire for an SSH brute-force pattern in the sample data, plus structured
event details (parsed hostname, program, severity, etc...).

**Note:** Requires `docker`, `curl`, and `jq`

## Architecture
EventGuard follows the Clean Architecture's dependency rule: source code dependencies point
inward, toward higher-level policy. The domain and use case layers have no framework
dependencies and can be tested without Spring, JPS, or HTTP.

### Diagrams
- [Component Diagram 1](./docs/ComponentDiagram/ComponentDiagram1.jpg)
- [Component Diagram 2](./docs/ComponentDiagram/ComponentDiagram2.jpg)
- [Component Diagram 3](./docs/ComponentDiagram/ComponentDiagram3.jpg)
- [Domain Class Diagram](./docs/ClassDiagram.jpg)
- [Use case Diagram](./docs/UseCaseDiagram.jpg)
- [Sequence Diagram](./docs/SequenceDiagram.jpg)

## API

| Method | Path                                  | Purpose                             |
| ------ | ------------------------------------- | ----------------------------------- |
| POST   | `/api/events`                         | Ingest a raw log line               |
| GET    | `/api/events/{id}`                    | Find a log event by ID              |
| GET    | `/api/events/by-source?source=`       | Search events by source (paginated) |
| GET    | `/api/events/by-time?from=...&to=...` | Search events by time range         |
| GET    | `/api/alerts`                         | List alerts (filter with ruleName)  |

Search endpoints accept `page` and `size` query parameters (default `size=50`, max `500`).
Time-range queries are capped at 100 days. Input validation rejects malformed or oversized
payloads with structured 400 responses.

## Testing

```bash
./gradlew clean test
```

## Limitations (MVP)

- **RFC 3164 syslog only.** Detects but rejects RFC 5424. Production SIEMs
would auto-detect and support both formats.
- **Stateless rules.** `SshBruteForceRule` fires per matching event. Real
"N failures in M seconds" detection requires windowed state, which would
need a rule engine with state management.
- **Rules defined in code.** Adding a rule means writing a Java class. A
production system would store rule definitions in the database.
- **Rule-parser coupling.** Rules look for parser-specific field names like
`program`. Multi-source support would require a normalization layer
(similar to Elastic Common Schema or Splunk CIM).
- **Timestamps assumed UTC.** RFC 3164 timestamps have no zone information;
we interpret them as UTC. Real deployments often need per-source zones.
- **Alert delivery is console-only.** Email, webhook, Slack notifiers would
implement the same `AlertNotifier` port with no use case changes.
- **No authentication, rate limiting, or audit logging.**

## Future Work

- Authentication and audit logging
- Webhook/email/Slack notifiers
- RFC 5424 parser with auto-detection
- Stateful rule engine for windowed detection rules