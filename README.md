# Argos
The observer.

## Badges
![Gradle Build](https://github.com/rashidi/argos/workflows/Gradle%20Build/badge.svg) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rashidi_argos&metric=alert_status)](https://sonarcloud.io/dashboard?id=rashidi_argos) 
[![codecov](https://codecov.io/gh/rashidi/argos/branch/master/graph/badge.svg)](https://codecov.io/gh/rashidi/argos) 
[![BCH compliance](https://bettercodehub.com/edge/badge/rashidi/argos?branch=master)](https://bettercodehub.com/)

## Requirements
  - Java SDK 11 or later
  - Azure Active Directory Client ID
  - Docker (optional - in order to run MongoDB via Docker)
  - MongoDB (optional - recommended to use Docker)
  - Gradle 6.4 or later (optional - recommended to use provided Gradle Wrapper)

## Properties
Application properties can be found in [application.yml](src/main/resources/application.yml).

| Properties | Description | Scope | Default Value |
|------------|-------------|-------|---------------|
| `argos.desk.max-duration`| Total duration a Desk can be occupied (can be assigned in minutes (M), hours (H), or days (D)) | Desk | `9H` |
| `argos.desk.scheduler-delay`| Delay period between each scheduler execution in miliseconds | Desk | `120000`
| `azure.activedirectory.session-stateless` | Enable stateless session | Security | `true`
| `azure.activedirectory.client-id` | Azure Client ID to be used by the application | Security | |
| `spring.data.rest.base-path` | Path for API enabled by Spring Data REST | API | `/api`

## Build
Application can be built with local Gradle installation or [Gradle Wrapper](gradlew) (recommended).

`./gradlew clean build`
