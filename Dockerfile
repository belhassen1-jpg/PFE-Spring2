FROM openjdk:11-jdk-slim
EXPOSE 8090
ADD http://192.168.1.100:8081/repository/maven-releases/com/example/Pidev/1.0.0/Pidev-1.0.0.jar Pidev-1.0.0.jar
ENTRYPOINT ["java","-jar","/Pidev-1.0.0.jar"]