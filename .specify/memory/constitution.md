# Agentic SDD Demo Constitution

## Core Principles

### I. Specification-Driven Development (SDD)
The specification document is the single source of truth. All behavior—including code implementation, implementation plan, and task lists—MUST directly follow the specification. The spec defines what is built; code, plan, and tasks are execution artifacts that must remain aligned with it. No feature exists outside the spec.

### II. Test-Driven Development (TDD)
Every code change follows the red → green → refactor cycle without exception. Tests MUST be written first and shown to fail before implementation begins. No skipping of tests under any circumstance. TDD is non-negotiable and enforced on all changes, regardless of complexity or urgency.

### III. MVP Scope Only
The project builds only minimum viable product functionality. No security hardening beyond baseline (e.g., input validation). No performance optimization. No "nice-to-have" features. Every line of code must directly serve a documented user story or requirement. Scope creep is prohibited.

### IV. Simplicity and Testability First
All technical choices MUST maximize simplicity and testability. Prefer boring, well-understood technologies over trendy or complex solutions. Code must be easy to understand, easy to test in isolation, and easy to modify. Complexity is only justified when simplicity is genuinely impossible.

### V. AI Agent Compliance
All AI agents generating code, plans, or specifications MUST follow rules I–IV without exception. Outputs that violate these principles MUST be corrected before acceptance. AI agents are held to the same standards as human developers.

## Development Workflow

### Specification Gate
All work begins with a feature specification (spec.md). The specification MUST include:
- User scenarios with acceptance criteria
- Functional requirements with explicit MUST/SHOULD language
- Data entities and contracts if applicable
- Clear scope boundaries

No development begins until the specification is complete and approved.

### Planning Phase
The implementation plan (plan.md) MUST:
- Align with the specification
- Identify technical context and dependencies
- Define project structure
- Pass constitution check against these principles

### Task Organization
Tasks (tasks.md) MUST:
- Be organized by user story to enable independent implementation
- Include explicit test tasks that follow TDD (write tests first)
- Reference exact file paths
- Have clear dependencies and parallelization markers

### Code Review Checklist
All PRs/code reviews MUST verify:
- Specification alignment: Does this change match the spec?
- Test coverage: Are tests written first and passing?
- Simplicity: Is this the simplest solution that meets requirements?
- MVP scope: Does this add unnecessary features or optimizations?
- AI compliance: If AI-generated, does it follow all five principles?

## Scope Exclusions (MVP)

The following are explicitly OUT of scope:
- Security hardening beyond basic input validation
- Performance optimization (e.g., caching, database indexing for scale)
- Load testing or capacity planning
- Advanced monitoring/observability beyond basic logging
- Internationalization or localization
- Accessibility enhancements beyond semantic HTML/ARIA basics
- User interface polish or theming

These can be added post-MVP if needed.

## Governance

This constitution is the highest-level governance document. All other practices, conventions, and team standards are subordinate to these five principles.

**Amendment Process**:
- Proposed amendments must be documented with rationale
- Amendments require explicit agreement from all stakeholders
- Version numbers follow semantic versioning (MAJOR.MINOR.PATCH)
- All existing specifications and plans must be re-checked after amendments

**Compliance Verification**:
- Use constitution check gates in plan.md before Phase 0 research
- All PRs must include a line stating "Constitution compliant: [yes/no]"
- Non-compliant outputs must be corrected or rejected

**Version**: 1.0.0 | **Ratified**: 2025-11-19 | **Last Amended**: 2025-11-19
