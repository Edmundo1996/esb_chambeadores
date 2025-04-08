# Usa una imagen base de Maven para construir el proyecto
FROM maven:3.8.5-openjdk-8 AS build
WORKDIR /app

# Copia el código fuente al contenedor
COPY . .

# Construye el proyecto y genera el archivo JAR
RUN mvn clean package -DskipTests

# Usa una imagen base ligera para ejecutar el JAR
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Copia el archivo JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación con límites de memoria
ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-XX:+UseG1GC", "-jar", "app.jar"]