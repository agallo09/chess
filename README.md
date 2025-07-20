# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Video reference for endpoints with JaVa spark library:
https://www.youtube.com/watch?v=qGVvvD7AxMU

## Server design 
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5T9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKEwMycUwtu189Q+qpelD1NA+BAIBMU+5tumqWogVXot3sgY87nae0d+7GWoKDgcTXS7SNxHekeL9vjyfB9EKHx6jHAC-xeeYUcOlfC9eb896j17wxJyowp6l7Foniaj9lgv5wp2fqlq8hpKks9T7CC156u0EB1mgcHLJcCaUJ2KYYPU4ROE4WYTDBnwwPBwLLEh8QoWhGH7Fc6AcC2mBeL4ATQOwjIxCKcARtIcAKDAAAyEBZIUeHME61D+s0bRdL0BjqPkaBZoqcxrL8-wcFckGCmB-ojBpKBafofw7NCjzgTJVBIjACDieKGKORJBJEmAxK7s6X5UoeY4MkyM4mQuNJHoKq5ihKboynKZbvEqmAqsGGrMtMN7QEgABeKAsQ+y62fZfYDkO+62f6aU0ZlOUcFGMZxoU+nfvAyCpjA6YAIwkTmqh5vM8FFiW9Q+OlerVblUJNnl-mPoVLpbu62ilb5MgzfSMAnnIKBvvEV43ne+UCk+9QvgG+1LZ+naGaWYlucBCCgdZOHlaWWbLCZezfDRdH1osSx6dUz3Jq1+EwIRxGjO9ZH9WMX03j96F-TATHNqx7F+P4XgoOgMRxIkWM47dvhYFJgqQfUDTSBGIkRu0EbdD0ymqKpwzfah6DYfCP5PVBbNoVZsLPb6Pn1K5xMueJxPuWonnLYKh2BWAO17ch7NoKFvJjhFwpRa+53yLK8p8xzyXqpKo3xON01hVrzWfr2-aDpdduA6WlUZVA2W5XVKCxqpnO4SDYBpk4XWQ2MPV9QWYyDdAw0W1b-1Tfea1HXNPZne+F0+fLqf1BwKDcGeN4q7Rasa0uR2VKuGQzBANCZ7e2fdt+UkEURWYoyxrFsd4GMopu-jYOKGoiWiMAAOJKhopMvRTE+0wz9hKqz8NqwH9zXS8xtoALf5kyLDloi5aLSyScsHjb-KK8rO8V+Fx2643sU1uvpupQnns1SnV8FcL3YOxKs7Oe5sqpf29tGX2DUN7AxKMHdqoc3oR35FHAaxY44Kk-l7XYyNk6HQgofHaH4c6X01tfI+OQp45gxPfW21cdbik3CaBAk8lQenwS7ABok0QAB4qEoB5OUZarcg7twhiMZYy8cwFgaOMKRKAACS0gCwdXCMEQIIJNjxF1CgN0nJPogmSKANUejYJ-RBPIgAckqP6FwYCdC7j3XuHF-AcAAOxuCcCgJwMQIzBDgPxAAbPAKchh+GNlnv-GoFNWgdCXivC2CMsxWKVADWSXMHiCygpIpU1jPhIz3jZKJ9lNrogxKUlAZ9ZbOz8r-ccMBGRKxLnfH+ZDlz0NFIw5+O44qQDQklVUH8wHYNaZXAhXDipOxISA92Y1wG1UgX7eMTVYFtU6kgyO+Y0FDUwcMmqSdmKjIfunE6+tgAX1qW0+pFT+EYhSXMWhj4OmnX4ew1O4y7LzVec3T5X5N483qHAUJGRVAgUwNdD5RkclzCUSotRgRcErMqG3MGHdw7yNhfUVR6jcGHJ7ujAIlhC6OU2LjJACQwBEoHBAUlAApCA4pWFzBiEYkAaoihBwPukmJzJFI9HkavVWaEszYAQMAIlUA4AQEclANYGLpBpMTNzLJ2815oTlUqJRWkxUSqldAQpQsuy-PqAAKwZWgDEZrxRVNJDU1adSb7NLVegR57ShSdIlEQnpr9+nv1AR7EZHCTkwEmRfGZWCao+yWY1V2gc4EhzDhI5BuYtmFnQaWEaeyJq4ubEG4p80vXyAufaq5itbnytdVXd1T9vkGzivKgZKUmWGEsRAZgIpfC5veZw41IbHZhqif6AAQiGKpUboFIpavGhB4jlibP6mmnZehNwokJDLA5Xa6kfPsrW85fqpUysrGaGs4YY3dkhaWQMx6wxoXHf7SdKL0yZnDvO6OsdSwmmPTAWs9Yc0sTtQrBU2AtDolubuyt2tajMmA1tZtxDz3NS3jAel1r7qPSyRel4iqgbItEai8Rjj8V9wCF4cVZKKWkflIgYMsBgDYFFYQPIBQImcpAZTamtN6a9GMBvZVf56gYipjTOmpIIXBpANwPANDi2AYkzR6THCOkwBRHXBuzCjA-OHBUFF4NO7JxbEAA