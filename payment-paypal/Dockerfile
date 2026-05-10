FROM eclipse-temurin:21-jre-alpine
#COPY target/payment-paypal*.jar app.jar
COPY target/payment-paypal-*-exec.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
