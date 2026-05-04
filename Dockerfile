# Stage 1: Build the project using Maven
FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY . .
RUN mvn -f backend/pom.xml clean package -DskipTests

# Stage 2: Run the app using Tomcat (Optimized for Railway)
FROM tomcat:9.0-jdk11-openjdk-slim

# --- RAILWAY OPTIMIZATION: JVM TUNING ---
# Setting max memory to 440MB to stay safely within Railway's 512MB free tier limit
ENV JAVA_OPTS="-Xms256m -Xmx440m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built .war file directly (Railway handles unzipping perfectly)
COPY --from=build /app/backend/target/blood-bank-system.war /usr/local/tomcat/webapps/ROOT.war

# Port 8080
EXPOSE 8080

# Start the server
CMD ["catalina.sh", "run"]
