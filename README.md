# start-helm
Generate a Helm Chart based on your app's dependency list

Because hand-crafting helm charts is painful.

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
