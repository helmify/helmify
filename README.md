# helm-start

Generate a Helm Chart based on your app's dependency list

Because hand-crafting helm charts is painful.

## Currently supporting

- [x] Spring Boot
- [ ] Quarkus
- [ ] Micronaut
- [x] Maven
- [ ] Gradle

## What does it do

helm-start will set up a helm chart tailored to your application's needs. It will:

- create a configmap.yaml and write a spring application.properties file which contains coordinates to external
  resources:
    - spring.datasource.url
    - spring.rabbitmq.host/port
- create / update secrets.yaml to store credentials for external resources
- update deployment.yaml to mount resource credential secretKeyRefs as environment variables into your pods
    - spring datasource username / password
    - spring rabbitmq username / password
- update deployment.yaml to run an initContainer for each external resource which blocks until the resource is ready
- update deployment.yaml to mount a runtime configuration file (spring application.properties)
- update deployment.yaml to configure readiness/liveness probes
- update values.yaml with a config block for each external resource
- update values.yaml with global values like hostnames
- update values.yaml to align naming to artifactId
- update values.yaml to use user-supplied docker image repository url
 
## Stack

- Java 17
- [Spring Boot 3.x](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html)
- [htmx](https://htmx.org/docs/)
- [Chota](https://jenil.github.io/chota/#docs)

## Build

```shell
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=starthelm/start-helm
```
