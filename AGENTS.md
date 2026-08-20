# AGENTS.md

This file provides guidance to coding agents (Claude Code, and others that read `AGENTS.md`) when working with code in this repository.

## Overview

A Spring Boot 4 + Kotlin proof-of-concept for GraalVM Native Image compilation. The project explores running a Spring Boot application as a native executable (both glibc/Ubuntu and musl/Alpine variants) with full AOT processing.

## Build & Run Commands

The project bundles Maven at `lib/maven/apache-maven-3.9.11/`. Use the wrapper at `./lib/maven/apache-maven-3.9.11/bin/mvn` or a system `mvn`.

```bash
# Native image build (glibc, default — requires GraalVM JDK with native-image)
mvn package

# Native image build for musl/Alpine
mvn -Pmusl package

# Docker image — Alpine/musl (multi-stage, builds native inside container)
docker build -f docker/alpine-build/Dockerfile -t graal-vm-and-spring-boot:alpine .

# Docker image — Ubuntu/glibc (expects pre-built native binary in target/)
mvn package
docker build -t graal-vm-and-spring-boot:ubuntu target/
```

## Architecture

### Key design points

- **In-memory storage only** — `TaskService` uses a `ConcurrentHashMap` and `AtomicLong` ID sequence. State is lost on restart. There is no database dependency.
- **GraalVM AOT** — Spring Boot AOT (`process-aot`) runs at `prepare-package`. `RuntimeHints` registers Tomcat and Kotlin internal classes that need reflection at runtime. All new reflection-dependent code must be registered there or via `@RegisterReflectionForBinding`.
- **Two native profiles**: `glibc` (default, `--gc=G1`) for Ubuntu/standard Linux and `musl` (static, no G1 GC) for Alpine. The musl profile cannot use G1 GC (causes segfault).
- **Docker**: `docker/ubuntu-build/Dockerfile` is a run-only image that copies the pre-built binary from `target/`. `docker/alpine-build/Dockerfile` is a full multi-stage build using `container-registry.oracle.com/graalvm/native-image:25-muslib`.

### Tests

Tests are plain JUnit 5 + `kotlin-test-junit5` — no Spring context is loaded. `TaskService` is instantiated directly and reset via `service.clear()` in `@BeforeTest`. Avoid adding `@SpringBootTest` unless genuinely testing Spring wiring.
