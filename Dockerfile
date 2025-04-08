# Usa una imagen base de Maven para construir el proyecto
FROM maven:3.8.5-openjdk-8 AS build
WORKDIR /app

# Copia el código fuente al contenedor
COPY . .

# Construye el proyecto y genera el archivo JAR
RUN mvn clean package -DskipTests

# Usa una imagen base de Java para ejecutar el JAR
FROM openjdk:8-jdk-alpine
WORKDIR /app

# Copia el archivo JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]