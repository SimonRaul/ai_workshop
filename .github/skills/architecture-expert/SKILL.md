---
name: architecture-expert
description: Project-specific architecture expert for the fullstack online shop. Use this skill whenever the user asks about this project's architecture, project structure, repository layout, modules, packages, layers, boundaries, dependencies, request or data flow, authentication flow, architectural constraints, conventions, design decisions, runtime modes, deployment units, database ownership, migrations, state ownership, where new code belongs, or whether a proposed change fits the architecture. Trigger even when the user uses related wording such as system design, code organization, component relationships, technical structure, or misspellings such as "arhitecture" and "arhitectural".
---

# Architecture Expert

Answer architecture questions using the repository's current files as the source of truth. This skill intentionally does not embed a copied architecture description: copied facts become stale when the project changes.

## Required discovery

Before answering, inspect the smallest set of current files that can establish the answer. Start with the repository root and then follow the relevant source map:

| Concern | Inspect first | Then inspect as needed |
|---|---|---|
| Topology and project boundaries | `README.md`, root directories | `docker/**`, `onlineshopapi/pom.xml`, `onlineshopui/package.json` |
| Backend layers and request flow | `onlineshopapi/src/main/java/**` | `controller/**`, `service/**`, `repository/**`, `dto/**`, `model/**`, `config/**`, `security/**` |
| Authentication and authorization | `onlineshopapi/src/main/java/**/security/**` | frontend `**/*guard*`, `**/*interceptor*`, security annotations, auth services |
| Persistence and schema ownership | `onlineshopapi/src/main/resources/**` | `db/migration/**`, JPA models, repositories, application configuration |
| Frontend boundaries and state | `onlineshopui/src/app/**` | `core/**`, `clib/**`, `features/**`, routes, providers, services |
| Runtime modes and deployment | `onlineshopui/src/environments/**`, backend configuration | `angular.json`, compose files, startup scripts, README files |
| Testing and build conventions | `onlineshopapi/pom.xml`, `onlineshopui/package.json` | nearby test files and CI configuration |

Use directory listings and targeted searches to locate files when a path does not exist. Do not assume the versions, ports, package names, feature names, database settings, or routes from a previous answer. Read configuration and implementation rather than inferring them from filenames.

## Answering workflow

1. Identify the concern: topology, layering, feature boundaries, persistence, security, runtime configuration, request flow, state ownership, testing, or code placement.
2. Read the relevant current files from the source map. Inspect implementation when the user asks whether the architecture is still followed, proposes a change, or asks about behavior.
3. Separate **observed current behavior** from **documented intent**. If repository documentation and code disagree, report the discrepancy and identify which one is authoritative for the decision.
4. Answer directly with the recommended boundary or flow, citing concrete repository paths and symbols when useful.
5. For a proposed change, name every affected layer and integration point, and explain whether it fits, conflicts with, or extends the observed architecture.

## Stable architectural principles

Apply these principles only after verifying that the current repository still uses the corresponding layers:

- Keep independently operated application and infrastructure boundaries independently buildable.
- Keep HTTP translation in controllers and API contracts in DTOs; keep business orchestration in services; keep persistence access in repositories.
- Do not expose persistence entities directly through controller contracts.
- Treat backend authentication and authorization as the security boundary. Frontend guards and role directives improve UX but cannot replace backend enforcement.
- Keep feature code close to its owning feature. Put reusable visual UI in the repository's shared UI area and cross-cutting infrastructure/contracts in its core/shared area.
- Keep mutable state close to the feature that owns it unless current code demonstrates a cross-feature requirement.
- Use the repository's migration mechanism for schema changes; do not recommend automatic production schema mutation without verifying the configured policy.
- Select runtime-specific frontend behavior through environment/configuration and shared HTTP composition, not duplicated feature-level branches.
- Preserve transaction boundaries and replaceable domain strategy abstractions when current implementation relies on them.

These are decision rules, not a substitute for inspecting the code. Do not claim a principle is implemented if the current files do not support it.

## Response guidelines

- Lead with the answer, then explain the relevant boundary, flow, or constraint.
- Prefer concrete paths, classes, functions, configuration keys, and route names over generic architecture language.
- For “where should this code go?”, recommend a location and explain the ownership rule that determines it.
- For flow questions, describe execution order from caller through security, HTTP, business, persistence, and response layers when those layers exist.
- Clearly label conclusions inferred from code versus statements found in documentation.
- Do not invent components, dependencies, constraints, or runtime behavior absent from current repository evidence.

## Avoiding stale architecture knowledge

When a user asks a follow-up based on an earlier architecture answer, re-check the relevant current files instead of repeating the earlier facts. If a commonly used source is missing, use the nearest current implementation and state the limitation. If the repository contains generated or duplicated architecture documentation, prefer executable configuration and source code for runtime facts, and use documentation to explain intended design.

## Common question mapping

| Question type | Starting points |
|---|---|
| Overall topology or deployables | root `README.md`, root directories, compose files |
| Backend packages, layers, or request flow | backend Java source tree, controllers, services, repositories, DTOs |
| Authentication or authorization | backend security package, auth code, frontend guards/interceptors |
| Domain model, orders, stock, or migrations | backend models/services, repositories, resources/db/migration |
| Frontend folders, routing, signals, or shared code | `onlineshopui/src/app`, routes, `core`, shared UI, features |
| Local, mock, or production behavior | frontend environments/configuration, backend application configuration |
| Customer or administrator workflows | routes, feature services/components, controllers, service methods |
| Constraints or code placement | relevant current source plus build/test configuration |
