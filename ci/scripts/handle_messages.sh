#!/usr/bin/env bash

# Define colores y estilos usando ANSI escape codes
bold=$(tput bold)
red=$(tput setaf 1)
green=$(tput setaf 2)
yellow=$(tput setaf 3)
reset=$(tput sgr0)

# Función para imprimir mensajes con el color especificado
# Arguments:
#   $1: Código de color
#   $2: Tipo de mensaje (INFO, WARNING, ERROR)
#   $3: El mensaje que se imprimirá
print_message() {
    local color=$1
    local type=$2
    local message=$3
    echo -e "${bold}${color}[${type}]${reset} ${message}"
}

# Función para imprimir mensajes de información
info() {
    print_message "$green" "INFO" "$1"
}

# Función para imprimir mensajes de advertencia
warning() {
    print_message "$yellow" "WARNING" "$1"
}

# Función para imprimir mensajes de error
# Outputs el mensaje a stderr
error() {
    print_message "$red" "ERROR" "$1" >&2
}

# Función para manejar errores y salir del script
error_exit() {
    error "$1"
    exit 1
}

info "Info message test"
warning "Warning message test"
error "Error message test"