FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copia o Maven Wrapper e o pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Dá permissão de execução
RUN chmod +x mvnw

# Copia o código fonte
COPY src ./src

# Executa o build
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]