#!/bin/bash
export JAVA_HOME=
mvn -DskipTests=true clean compile && mvn spring-boot:build-image && docker tag helmify:0.6.1-SNAPSHOT helmify/helmify:latest && docker push helmify/helmify:latest
