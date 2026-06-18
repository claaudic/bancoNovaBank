# =============================================================
# Dockerfile para ms-clientes - Banco NovaBank
#
# Multi-stage build: primero construye el .jar con Maven,
# luego lo empaqueta en una imagen ligera con solo Java 21.
# =============================================================

# ---------- STAGE 1: BUILD ----------
# Usa una imagen con Maven y JDK 21 para compilar el proyecto
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Crea y se posiciona en /app dentro del contenedor
WORKDIR /app

# Copia el pom.xml primero (para cachear dependencias)
COPY pom.xml .

# Descarga dependencias sin compilar el codigo (acelera builds futuros)
RUN mvn dependency:go-offline -B

# Copia el codigo fuente
COPY src ./src

# Compila y empaqueta el .jar saltando tests (ya pasaron en CI/dev)
RUN mvn clean package -DskipTests -B


# ---------- STAGE 2: RUNTIME ----------
# Imagen liviana con solo el Java Runtime (sin Maven, sin codigo fuente)
FROM eclipse-temurin:21-jre-alpine

# Carpeta de trabajo dentro del contenedor de runtime
WORKDIR /app

# Copia solo el .jar generado en el stage 1
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto interno (informativo, Docker no lo abre solo)
EXPOSE 8080

# Por defecto activa el perfil dev (se puede sobreescribir al correr)
ENV SPRING_PROFILES_ACTIVE=dev

# Comando que arranca la app cuando el contenedor inicie
ENTRYPOINT ["java", "-jar", "app.jar"]
