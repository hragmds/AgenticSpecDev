<!--
Sync Impact Report:
- Version change: [NEW] → 1.0.0 (Initial ratification)
- New principles: I. Specification-Driven Development, II. Test-Driven Development, III. MVP Scope, IV. Simplicity First, V. AI Compliance
- Added sections: Development Workflow, AI Agent Requirements
- Templates requiring updates: ✅ All templates aligned with SDD/TDD requirements
- Follow-up TODOs: None - all placeholders filled
-->

# Banking App Constitution

## Core Principles

### I. Specification-Driven Development (SDD)
The specification defines ALL behavior. Code, plans, and tasks MUST follow the spec exactly.
No implementation without specification. No deviation from specified behavior.
Every feature begins with a complete specification that defines all expected outcomes.

**Rationale**: SDD ensures consistency, prevents scope creep, and maintains traceability
between requirements and implementation.

### II. Test-Driven Development (TDD) 
TDD is MANDATORY. Every change follows red → green → refactor. No skipping tests.
The core cycle involves writing a failing test, writing the minimum code to pass that test,
and then refactoring the code to improve its design.

**Rationale**: TDD ensures code quality, prevents regressions, and drives good design
through test-first thinking.

### III. MVP Scope
No security work, no performance optimization. Only build what is needed for functionality.
Focus on core banking features that deliver user value. Defer non-functional requirements
until the MVP proves viability.

**Rationale**: MVP approach reduces risk, accelerates time-to-market, and validates
assumptions before investing in optimizations.

### IV. Simplicity First
Technical choices MUST maximize simplicity and testability. Choose the simplest solution
that meets the requirements. Complexity must be explicitly justified and documented.
Default to proven, straightforward approaches over novel or complex solutions.

**Rationale**: Simple code is easier to understand, maintain, test, and debug.
Simplicity reduces the cognitive load for all team members.

### V. AI Compliance
AI agents MUST follow these rules. Non-compliant outputs MUST be corrected.
All AI-generated code, plans, and specifications are subject to constitution compliance
review before acceptance.

**Rationale**: Ensures consistent quality and adherence to project standards
regardless of implementation method.

## Development Workflow

All development follows the SDD + TDD cycle:
1. Write complete specification (behavior, acceptance criteria, edge cases)
2. Create failing tests based on specification
3. Write minimal code to pass tests
4. Refactor for simplicity while maintaining test passage
5. Verify implementation matches specification exactly

Code reviews MUST verify:
- Specification compliance
- Complete test coverage
- TDD cycle adherence
- Simplicity maximization

## AI Agent Requirements

AI agents working on this project MUST:
- Follow SDD: Reference specifications before any code changes
- Follow TDD: Write tests before implementation code
- Respect MVP scope: Reject requests for premature optimization or security features
- Prioritize simplicity: Choose simple solutions over complex ones
- Self-correct: Fix any outputs that violate constitution principles

## Governance

This constitution supersedes all other development practices and guidelines.
All pull requests, code reviews, and architectural decisions MUST verify compliance
with these principles. Any complexity introduced MUST be explicitly justified
in terms of meeting specification requirements.

Amendments require:
1. Documentation of proposed changes
2. Impact analysis on existing codebase
3. Team approval
4. Version increment following semantic versioning

**Version**: 1.0.0 | **Ratified**: 2025-11-19 | **Last Amended**: 2025-11-19
