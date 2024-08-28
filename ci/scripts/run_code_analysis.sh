#!/usr/bin/env bash

set -e

# Source message handler functions
source ci/scripts/handle_messages.sh

# Configuration
CHECKSTYLE_JAR="lib/checkstyle.jar"
CHECKSTYLE_CONFIG="ci/scripts/checkstyle.xml"
SOURCE_DIR="src/main/java"

info "CHECKSTYLE_JAR: $CHECKSTYLE_JAR"
info "CHECKSTYLE_CONFIG: $CHECKSTYLE_CONFIG"
info "SOURCE_DIR: $SOURCE_DIR"

# Function to get all Java files
get_java_files() {
    find "$SOURCE_DIR" -type f -name "*.java"
}

# Function to check formatting with google-java-format
check_google_java_format() {
    info "Checking formatting with google-java-format"
    files=$(get_java_files)
    if [ -z "$files" ]; then
        info "No Java files to check"
        return
    fi
    echo "Processing files: $files"
    for file in $files; do
        if ! java -jar lib/google-java-format-1.23.0-all-deps.jar --dry-run --set-exit-if-changed "$file"; then
            error "google-java-format did not pass for file: $file"
            exit 2
        fi
    done
    info "google-java-format passed!"
}

# Function to check for unused imports with Checkstyle
check_checkstyle_unused_imports() {
    info "Checking for unused imports with Checkstyle"
    files=$(get_java_files)
    if [ -z "$files" ]; then
        info "No Java files to check"
        return
    fi
    for file in $files; do
        if ! java -cp "$CHECKSTYLE_JAR:lib/commons-cli-1.4.jar" com.puppycrawl.tools.checkstyle.Main -c "$CHECKSTYLE_CONFIG" "$file"; then
            error "Checkstyle did not pass for file: $file"
            exit 2
        fi
    done
    info "Checkstyle passed for unused imports!"
}

# Function to run PMD
run_pmd_analysis() {
    info "Running PMD analysis"
    # Asegúrate de que la ruta sea la correcta, relativa a tu ubicación actual
    if ! ./pmd-bin-6.42.0/bin/run.sh pmd -d "$SOURCE_DIR" -R ci/scripts/pmd.xml -f text; then
        error "PMD did not pass"
        exit 2
    fi
    info "PMD analysis completed!"
}


# Main script
check_google_java_format
check_checkstyle_unused_imports
run_pmd_analysis
