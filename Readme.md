# Task - 2
## Explore JanusGraph

## Introduction
  - JanusGraph is a scalable graph database optimized for storing and querying graphs containing hundreds of billions of vertices and edges distributed across a multi-machine cluster.

## Prerequisites
  - Java
  - Maven

## Instructions
  - Clone: `git clone url`
  - Build: `mvn clean install`
  - Run: `mvn exec:java`

## Sub-Tasks
  - ### Sub-Task 1:
    - Create a graph using JanusGraph and air-routes.
    - Measure the time taken and storage to create the graph.
    - **Storage Backend:** InMemory
      - **Operations:**
        - Create a graph using JanusGraph.
        - Use **InMemory** storage backend.
        - Load air-routes data.
        - Measure the time taken and storage.

  - ### Sub-Task 2:
    - Create a graph using JanusGraph and air-routes.
    - Measure the time taken and storage to create the graph.
    - **Storage Backend:** BerkeleyDB
      - **Operations:**
        - Create a graph using JanusGraph.
        - Use **BerkeleyDB** storage backend.
        - Load air-routes data.
        - Measure the time taken and storage.

  - ### Sub-Task 3:
    - Create a graph using JanusGraph and air-routes.
    - Measure the time taken and storage to create the graph.
    - **Storage Backend:** FoundationDB
      - **Operations:**
        - Create a graph using JanusGraph.
        - Use **FoundationDB** storage backend.
        - Load air-routes data.
        - Measure the time taken and storage.