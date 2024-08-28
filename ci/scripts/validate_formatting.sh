#!/usr/bin/env bash

set -e

# Source message handler functions
source ci/scripts/handle_messages.sh

# Function to get all Java files
get_java_files() {
    find . -type f -name "*.java" -not -path "./.sandbox/*"
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

# Function to check for unused imports with checkstyle
check_checkstyle_unused_imports() {
    info "Checking for unused imports with Checkstyle"
    files=$(get_java_files)
    if [ -z "$files" ]; then
        info "No Java files to check"
        return
    fi
    for file in $files; do
        if ! checkstyle -c ci/scripts/checkstyle_unused_imports.xml "$file"; then
            error "Checkstyle did not pass for file: $file"
            exit 2
        fi
    done
    info "Checkstyle passed for unused imports!"
}

# Main script
check_google_java_format
check_checkstyle_unused_imports
