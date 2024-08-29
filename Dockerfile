# Usar una imagen base de OpenJDK para Java
FROM openjdk:17-jdk-slim

# Argumentos de construcción
ARG INSTALL_LOC
ARG HOST_PORT

# Configuración de variables de entorno
ENV INSTALL_LOC=$INSTALL_LOC \
    HOST_PORT=$HOST_PORT

# Crear el directorio de instalación en el contenedor
RUN mkdir -p $INSTALL_LOC

# Copiar el código fuente y archivos de configuración necesarios al contenedor
COPY ./src /$INSTALL_LOC/src
COPY ./gradlew /$INSTALL_LOC/
COPY ./gradle /$INSTALL_LOC/gradle
COPY ./build.gradle /$INSTALL_LOC/
COPY ./settings.gradle /$INSTALL_LOC/

# Configurar permisos y preparar la construcción
WORKDIR $INSTALL_LOC
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon

# Exponer el puerto configurado
EXPOSE $HOST_PORT

# Crear el directorio para logs u otros archivos necesarios
RUN mkdir -p /$INSTALL_LOC/logs

# Configurar el punto de entrada del contenedor
COPY ./ci/scripts/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Comando de entrada
ENTRYPOINT [ "/entrypoint.sh" ]

# Comando por defecto para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/your-app.jar"]
