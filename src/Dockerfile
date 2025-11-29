# Usa una imagen base de Java
FROM eclipse-temurin:17-jre-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el JAR de tu aplicación
COPY target/*.jar app.jar

# Expone el puerto (ajusta según tu aplicación)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]