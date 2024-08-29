# Usar una imagen base de OpenJDK para Java
FROM openjdk:11-jdk AS builder

# Argumentos de construcción
ARG INSTALL_LOC=/app
ARG HOST_PORT=8080
ARG STATE_MACHINE_TYPE

# Configuración de variables de entorno
ENV INSTALL_LOC=$INSTALL_LOC \
    HOST_PORT=$HOST_PORT \
    STATE_MACHINE_TYPE=$STATE_MACHINE_TYPE

# Crear el directorio de instalación en el contenedor
RUN mkdir -p $INSTALL_LOC

# Copiar el código fuente y archivos de configuración necesarios al contenedor
COPY ./src $INSTALL_LOC/src
COPY ./gradlew $INSTALL_LOC/
COPY ./gradle $INSTALL_LOC/gradle
COPY ./build.gradle $INSTALL_LOC/
COPY ./settings.gradle $INSTALL_LOC/
COPY ./ci/scripts/checkstyle.xml $INSTALL_LOC/ci/scripts/checkstyle.xml
COPY ./lib/checkstyle.jar $INSTALL_LOC/lib/checkstyle.jar
COPY ./lib/commons-cli-1.4.jar $INSTALL_LOC/lib/commons-cli-1.4.jar
COPY ./pmd-bin-6.42.0 $INSTALL_LOC/pmd-bin-6.42.0

# Configurar permisos y preparar la construcción
WORKDIR $INSTALL_LOC
RUN chmod +x gradlew

# Construir el proyecto con opciones de depuración para diagnostico de errores
RUN ./gradlew build --no-daemon --info

# Segunda etapa: Crear la imagen limpia para ejecución
FROM openjdk:11-jdk

# Argumentos de construcción
ARG INSTALL_LOC=/app
ARG HOST_PORT=8080
ARG STATE_MACHINE_TYPE

# Configuración de variables de entorno
ENV INSTALL_LOC=$INSTALL_LOC \
    HOST_PORT=$HOST_PORT \
    STATE_MACHINE_TYPE=$STATE_MACHINE_TYPE

# Crear el directorio de instalación en el contenedor
RUN mkdir -p $INSTALL_LOC

# Copiar los artefactos construidos de la etapa anterior
COPY --from=builder $INSTALL_LOC/build/libs/*.jar $INSTALL_LOC/your-app.jar

# Crear el directorio para logs u otros archivos necesarios
RUN mkdir -p $INSTALL_LOC/logs

# Copiar el script de entrada
COPY ./ci/scripts/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Exponer el puerto configurado
EXPOSE $HOST_PORT

# Comando de entrada
ENTRYPOINT [ "/entrypoint.sh" ]

# Comando por defecto para ejecutar la aplicación
CMD ["java", "-jar", "$INSTALL_LOC/your-app.jar", "--stateMachineType=$STATE_MACHINE_TYPE"]
