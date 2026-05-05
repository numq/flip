# FLIP — Feature-Layered Isolated Platform

**FLIP** is an architectural pattern for Kotlin Multiplatform (KMP) applications that enforces strict module boundaries
through dependency rules rather than conventions.

<p align="center">
  <img src="./media/flip-layers.svg" alt="FLIP Layers" width="512"/>
</p>

## Overview

FLIP organizes code into five layers. Each layer has a single responsibility and a strictly defined set of dependencies.

### Layers

| Layer                     | Purpose                        | Depends on                                     |
|---------------------------|--------------------------------|------------------------------------------------|
| `:common:core`            | Common logic                   | Nothing                                        |
| `:common:presentation`    | Common presentation            | `:common:core`                                 |
| `:entrypoint`             | DI assembly + slot composition | `:common:presentation`, `:feature`, `:service` |
| `:feature:*:core`         | Feature domain                 | `:common:core`, `:service:*`                   |
| `:feature:*:presentation` | Feature presentation           | `:common:presentation`, `:feature:*:core`      |
| `:platform`               | Platform entry point           | `:entrypoint`                                  |
| `:service`                | Domain without presentation    | `:common:core`                                 |

## Dependency Rules

<p align="center">
  <img src="./media/flip-dependencies.svg" alt="FLIP Dependencies" width="768"/>
</p>

### Core Rules

1. **No horizontal dependencies.** Features cannot depend on other features. Services cannot depend on other services.
2. **Dependencies flow downward.** Platform → Entrypoint → Feature → Service → Common.
3. **A service and a feature cannot share the same name.** If `:service:foo` exists, `:feature:foo` must not exist — use
   `:feature:bar` instead. This prevents logic duplication.

### Evolution

FLIP grows with your project.

**Stage 1: Single feature**
`:common`, `:feature`, `:entrypoint`, `:platform`

**Stage 2: Multiple features**
Add more `:feature` modules. Features must not depend on each other.

**Stage 3: Shared functionality**
Extract `:service` when two or more features share the same data or logic.

You don't need `:service` from day one.

### Service Layer

A `:service` is a **domain service without presentation**. It contains:

- Domain models
- A public interface (e.g., `UserService`)
- An internal implementation (e.g., `LocalUserService`)

Services are the only communication channel between features. A feature that needs data from another domain must depend
on its service.

### Feature Layer

A `:feature` always has two submodules:

- `:core` — presentation domain (UseCases, optional presentation services)
- `:presentation` — Compose UI

A feature **must not** duplicate the domain logic of a service. If `:service:user` receives a list of users, then
`:feature:profile` focuses on the user interface state: displaying a profile based on the user's data.

### UseCase Rule

A UseCase in `:feature:*:core` depends on:

- Its own presentation service
- External services from `:service:*`

```kotlin
class UpdateProfile(
    private val profileService: ProfileService,  // internal domain
    private val userService: UserService,        // external domain
) : UseCase<UpdateProfile.Input, Unit>
```

### Slot-Based Composition

Features are composed through slots in `:entrypoint`. A slot is a `@Composable` callback passed to the navigation
feature.

```kotlin
NavigationView(
    splash = {
        SplashView(applicationScope = applicationScope)
    },
    profile = {
        ProfileView(applicationScope = applicationScope)
    }
)
```

### When to Use FLIP

#### Use FLIP when:

- Building medium to large KMP applications
- Multiple teams work on different features
- Strict isolation between features is required
- You want compile-time enforcement of architecture boundaries

#### Don't use FLIP when:

- Building a small app with 1-2 screens
- You don't need strict module isolation
- Rapid prototyping without architectural overhead

### Comparison

| Pattern             | 	Module Isolation	     | UI Pattern               | 	Navigation    |
|---------------------|------------------------|--------------------------|----------------|
| FLIP	               | Compile-time (Gradle)	 | MVI (Feature + Reducer)	 | Slot-based     |
| Clean Architecture	 | Convention	            | Any	                     | Any            |
| Decompose           | 	Runtime	              | Any	                     | Component tree |
| Voyager	            | None	                  | Screen                   | 	Stack-based   |

___

<p align="center">
  <a href="https://numq.github.io/support">
    <img src="https://api.qrserver.com/v1/create-qr-code/?size=112x112&data=https://numq.github.io/support&bgcolor=1a1b26&color=7aa2f7" 
         width="112" 
         height="112" 
         style="border-radius: 4px;" 
         alt="QR code">
  </a>
  <br>
  <a href="https://numq.github.io/support" style="text-decoration: none;">
    <code><font color="#bb9af7">numq.github.io/support</font></code>
  </a>
</p>