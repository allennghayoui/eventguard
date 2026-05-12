#!/usr/bin/env bash

set -euo pipefail

# EventGuard demo: ingest sample syslog lines, show alerts.
# Assumes the app is running on localhost:8080.

API=http://localhost:8080
SAMPLE_FILE="$(dirname "$0")/sample-syslog.txt"

echo "=== EventGuard demo ==="
echo

# Health check
if ! curl -sf "$API/api/alerts" > /dev/null; then
	echo "ERROR: EventGuard not responding at $API"
	echo "Start it with: docker compose up"
	exit 1
fi

echo "Ingesting sample syslog lines from $SAMPLE_FILE..."
echo

LINE_COUNT=0
while IFS= read -r line; do
	[[ -z "$line" ]] && continue
	LINE_COUNT=$((LINE_COUNT + 1))
	curl -sf -X POST "$API/api/events" \
		-H "Content-Type: application/json" \
		-d "$(jq -n --arg raw "$line" --arg src "syslog" '{rawLine: $raw, source: $src}')" \
		> /dev/null
	printf "."
done < "$SAMPLE_FILE"

echo
echo
echo "Ingested $LINE_COUNT lines."
echo

# Show alerts
echo "=== Alerts raised ==="
curl -s "$API/api/alerts" | jq '.[] | {rule: .ruleName, severity: .severity, raisedAt: .raisedAt}'

echo
echo "=== Sample events from this batch ==="
curl -s "$API/api/events/by-source?source=syslog&size=5" | \
	jq '.[] | {timestamp, severity, message, fields: (.fields | {hostname, program, "syslog.facility"})}'

echo
echo "Demo complete."
