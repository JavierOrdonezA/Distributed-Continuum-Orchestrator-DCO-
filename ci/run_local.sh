#!/usr/bin/env bash
## Parse arguments
set -e

POSITIONAL=()
# Set path to CI Scripts as environmental variable
export CI_SCRIPTS_PATH="ci/scripts"
export CI_FOLDER="ci/scripts"
# Change all shell scripts in ci/ folder to have permission to execute
chmod u+x ${CI_FOLDER}/*.sh
# Set format and tests run to False initially
run_format=false
run_tests=false

function print_usage {
cat <<- EOF
    usage: run_local [-f | -t | -i | -h] -- Runs code quality and formatting checks

    -f,--run-format: Forces autoformatting in-place on code.

    -t,--test,--tests: Runs format and code quality checks, along with unit tests

    -i,--install: Install required dependencies and tools.

    -h,--help: Get help on how to use the local code quality and test script

EOF
}

while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    --run-format|-f)
        run_format=true
        shift
    ;;
    --tests|--test|-t)
        run_tests=true
        shift
    ;;
    --install|-i)
        echo "Installing requirements"
        # Install Java dependencies, assuming you're using a package manager like SDKMAN! or Homebrew
        echo "Installing Checkstyle, PMD, and Google Java Format"
        brew install checkstyle pmd google-java-format
        echo "Succeeded"
        exit 0
    ;;
    --help|-h)
        print_usage
        exit 0
    ;;
    *)
        echo "Unrecognized argument $key. Try ${CI_SCRIPTS_PATH}/run_local.sh --help"
        exit 2
    ;;
esac
done

if $run_format; then
    ${CI_SCRIPTS_PATH}/apply_formatting.sh
fi
${CI_SCRIPTS_PATH}/validate_formatting.sh
${CI_SCRIPTS_PATH}/run_code_analysis.sh

if $run_tests; then
    echo "Running tests"
    ./gradlew test
fi

echo "Local checks passed :) Might still deviate from GitHub Actions checks :/"
