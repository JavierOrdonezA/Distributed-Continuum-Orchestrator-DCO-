#!/bin/sh
# Configura variables de entorno o realiza tareas previas
export ENV_VAR=value

# Verifica si el archivo JAR existe antes de intentar ejecutarlo
if [ ! -f /app/your-app.jar ]; then
  echo "Error: JAR file not found at /app/your-app.jar"
  exit 1
fi

# Inicia tu aplicación
exec java -jar /app/your-app.jar "$@"
