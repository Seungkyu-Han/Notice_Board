FROM openjdk:17-jdk

CMD ["./gradlew", "clean", "build", "-x", "test"]

VOLUME /writing

ARG JAR_FILE=presentation/build/libs/presentation-1.0-SNAPSHOT.jar

COPY ${JAR_FILE} writing.jar

EXPOSE 12042

ENTRYPOINT ["java", "-jar", "/writing.jar"]