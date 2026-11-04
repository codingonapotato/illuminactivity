# Illuminactivity

## What is Illuminactivity?

Illuminactivity is a desktop application for automated usage tracking to help users gain an insight into their computer usage habits

This application is designed with a "local-first" and "privacy-first" approach in mind. As a result, all components of this application are designed to run on the user's machine locally.

## High-level Architecture
```
  ┌─────────────────────────────────────────────────────────┐
  │                 Electron Frontend Shell                 │
  │              (Local Window UI / HTTP Client)            │
  └────────────────────────────┬▲───────────────────────────┘
                               ││ HTTP / REST API
                               ▼│ (Requests / Responses)
  ┌────────────────────────────┴────────────────────────────┐
  │                 Spring Boot Backend                     │
  │  ┌───────────────────────────────────────────────────┐  │
  │  │               Controller & Service                │  │
  │  │        (@Transactional Boundary Management)       │  │
  │  └─────────────────────────┬▲────────────────────────┘  │
  │                            ││ Spring Data JPA           │
  │                            ▼│ (Queries / Persistence)   │
  │  ┌─────────────────────────┴─────────────────────────┐  │
  │  │               Embedded H2 Database                │  │
  │  │                                                   │  │
  │  └───────────────────────────────────────────────────┘  │
  └─────────────────────────────────────────────────────────┘
```

## UML diagram - Spring Boot Backend Service Layer
![alt text](backend_service_uml_diagram.png)