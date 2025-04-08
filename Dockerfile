FROM openjdk:8-jdk-alpine
#Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

COPY target/*.jar app.jar


ENTRYPOINT [ "java","-jar", "app.jar" ]