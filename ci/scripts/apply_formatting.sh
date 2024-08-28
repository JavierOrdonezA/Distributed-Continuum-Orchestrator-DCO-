#!/usr/bin/env bash

set -e

# Source message handler functions
source ci/scripts/handle_messages.sh

# Function to get all Java files
get_java_files() {
    find src/main/java -type f -name "*.java"
}

# Main script
info "Organizing imports with google-java-format"
java_files=$(get_java_files)
if [ -z "$java_files" ]; then
    info "No Java files to format"
    exit 0
fi

# Format code and organize imports using google-java-format
for file in $java_files; do
    info "Formatting file: $file"
    if ! java -jar lib/google-java-format-1.23.0-all-deps.jar --replace "$file"; then
        error "google-java-format failed for file: $file"
        exit 2
    fi
done

info "Performing code linting with Checkstyle"
# Use Checkstyle directly with java -jar
if ! java -cp "lib/checkstyle.jar:lib/commons-cli-1.4.jar" com.puppycrawl.tools.checkstyle.Main -c ci/scripts/checkstyle.xml $(get_java_files); then
    error "Checkstyle did not pass"
    exit 2
fi

info "Performing static code analysis with PMD"
# Use the correct path to the PMD executable
if ! pmd-bin-6.42.0/bin/run.sh pmd -d src/main/java/ -R ci/scripts/pmd.xml -f text; then
    error "PMD did not pass"
    exit 2
fi

info "Formatting completed :)"
