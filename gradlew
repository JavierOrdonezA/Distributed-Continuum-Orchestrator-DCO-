#!/usr/bin/env sh
set -eu

GRADLE_VERSION="8.10.2"
WRAPPER_DIR=".gradle/wrapper-custom"
DIST_DIR="$WRAPPER_DIR/gradle-$GRADLE_VERSION"
ZIP_FILE="$WRAPPER_DIR/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_BIN="$DIST_DIR/gradle-$GRADLE_VERSION/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$WRAPPER_DIR"
  if [ ! -f "$ZIP_FILE" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    curl -fsSL \
      "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" \
      -o "$ZIP_FILE"
  fi
  if [ ! -x "$GRADLE_BIN" ]; then
    echo "Extracting Gradle $GRADLE_VERSION..."
    unzip -q -o "$ZIP_FILE" -d "$DIST_DIR"
  fi
fi

exec "$GRADLE_BIN" "$@"
