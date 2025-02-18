# Usar una imagen base de OpenJDK 17 (ajusta según tu versión de Java)
FROM openjdk:17-jdk-slim

# Configurar el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el JAR de la aplicación al contenedor
COPY mads-todolist-1.0.0.jar app.jar

# Exponer el puerto en el que correrá la aplicación
EXPOSE 8080

# Ejecutar la aplicación (agrega opciones JVM si lo requieres)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
