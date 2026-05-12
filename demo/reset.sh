#!/usr/bin/env bash

set -euo pipefail

echo "Resetting EventGuard database..."

docker exec -i eventguard-pg psql -U eventguard -d eventguard <<SQL
DELETE FROM alerts;
DELETE FROM log_event_fields;
DELETE FROM log_events;
SQL

echo "Done. Database is empty."
