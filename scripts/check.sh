#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
java_bin="${JAVA_HOME:+$JAVA_HOME/bin/}java"
javac_bin="${JAVA_HOME:+$JAVA_HOME/bin/}javac"
build_dir=$(mktemp -d /tmp/notebook-cesde-checks-XXXXXX)
trap 'rm -rf "$build_dir"' EXIT
find src/main/java src/test/java -name '*.java' -print > "$build_dir/sources.txt"
"$javac_bin" --release 25 -d "$build_dir/classes" @"$build_dir/sources.txt"
for check in PlaceChecks PaymentMethodChecks PaymentChecks UserIdentityChecks AdminIntegrationChecks CliChecks; do
    "$java_bin" -ea -cp "$build_dir/classes" "app.$check"
done
