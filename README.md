# helmify

Generate a Helm Chart for an existing application.

## Currently supporting

### Chart Flavors
- "Helm Create" - a basic Helm Chart like you would get from `helm create`
- "Bitnami" - a Helm Chart with Bitnami's common chart structure

### Application Types
- Web Applications

### Languages

#### Java

<table>
  <tr>
    <td colspan="4">Supported JVM Frameworks</td>
  </tr>
  <tr>
    <td></td>
    <td>Maven</td>
    <td>Gradle (groovy)</td>
    <td>Gradle (kts)</td>
  </tr>
  <tr>
    <td>Spring Boot</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td>Quarkus</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
</table>

<table>
  <tr>
    <td colspan="4">Supported Services</td>
  </tr>
  <tr>
    <td></td>
    <td>Spring Boot</td>
    <td>Quarkus</td>
  </tr>
  <tr>
    <td>Postgresql</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>MySQL</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>MariaDB</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>MongoDB</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Elasticsearch</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Cassandra</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Couchbase</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Neo4j</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>RabbitMQ</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Kafka</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Redis</td>
    <td>Yes</td>
    <td>WIP</td>
  </tr>
  <tr>
    <td>Probes</td>
    <td>Spring Actuator</td>
    <td>Smallrye Health</td>
  </tr>
</table>

#### Go
TODO

#### Python
TODO

#### Node.js
TODO


## What does it do

Helmify aims to support developers in getting their applications running on Kubernetes as easily as possible.

Since Helm is the de-facto way for packaging applications [citation needed] to run on Kubernetes, Helmify is looking to
support developers in running their applications - possibly with dependencies like databases etc - on K8s as early
as possible. 

To achieve this goal, Helmify will provide a developer with:
- A Config Map to set up the application with all required information for successful connectivity to external resources
- A Secret to store credentials for external resources
- A Deployment with initContainers to wait for external resources to be ready
- A Service exposing ports for http and healthcheck access

Additionally, the Helmify Web UI will offer some options for customization:
- Choose the Helm Chart flavor (Helm Create, Bitnami)
- Specify Docker Image Name / Pull Secret

## How to use

Helmify can be used in several ways: 

- Use the Web UI at https://helmify.me
- Use the CLI, available from the [releases page](https://github.com/helmify/helmify/releases)
- Set up https://helmify.me/spring as Spring Initializr URL in your IDE
- Set up https://helmify.me/quarkus as Quarkus Initializr URL in your IDE


## Stack

- Java 21
- [Spring Boot 3.x](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html)
- [htmx](https://htmx.org/docs/)
- [Chota](https://jenil.github.io/chota/#docs)

## Build

```shell
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=helmify/helmify
```
