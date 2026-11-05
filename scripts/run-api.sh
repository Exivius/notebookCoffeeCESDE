#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
driver_jar="${MYSQL_CONNECTOR_JAR:-$HOME/.m2/repository/com/mysql/mysql-connector-j/26.7.0/mysql-connector-j-26.7.0.jar}"

if [[ ! -f "$driver_jar" ]]; then
  echo "No se encontró MySQL Connector/J. Define MYSQL_CONNECTOR_JAR con la ruta del .jar." >&2
  exit 1
fi

if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" && -x "$JAVA_HOME/bin/javac" ]]; then
  java_bin="$JAVA_HOME/bin/java"
  javac_bin="$JAVA_HOME/bin/javac"
elif command -v java >/dev/null 2>&1 && command -v javac >/dev/null 2>&1; then
  java_bin="$(command -v java)"
  javac_bin="$(command -v javac)"
else
  jdk_bin="$(find "$HOME/.jdks" -mindepth 3 -maxdepth 3 -type f -name java -print -quit 2>/dev/null || true)"
  if [[ -z "$jdk_bin" ]]; then
    echo "Instala un JDK o define JAVA_HOME antes de iniciar el backend." >&2
    exit 1
  fi
  java_bin="$jdk_bin"
  javac_bin="$(dirname "$jdk_bin")/javac"
fi

cd "$project_dir"
mkdir -p target/classes
mapfile -t sources < <(find src/main/java -name '*.java' -print)
"$javac_bin" -cp "$driver_jar" -d target/classes "${sources[@]}"
exec "$java_bin" -cp "target/classes:$driver_jar" app.ApiApplication
