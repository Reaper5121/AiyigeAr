# Aiyige Project Agent Rules

These rules apply to AI agents working in this repository. Any agent that reads
this file must follow them for planning, implementation, review, and handoff.

## Project priorities

1. Preserve the real customer flow: mobile app -> automatic quotation -> customer confirmation -> customer-selected J&T or Lalamove -> payment -> final order review and submission -> staff records delivery progress.
2. Keep the Android customer app, Node admin backend, admin web UI, local state, and Supabase schema consistent.
3. Prefer small, testable changes with clear ownership over broad rewrites.
4. Never claim a plugin, database migration, sync, or external connection is active without verifying it.

## Required coding rules

- Do not write spaghetti code anywhere in this project.
- Keep responsibilities separated: UI, state, API, persistence, and integrations must not be mixed unnecessarily.
- Reuse existing utilities and domain functions before adding duplicates.
- Avoid hidden global state, magic strings, duplicated business rules, and unreachable legacy branches.
- Preserve backward compatibility deliberately; document compatibility adapters and remove them only through a planned migration.
- Validate user input at boundaries and handle network/database failures explicitly.
- Never commit secrets, service-role keys, tokens, passwords, or private vault paths.

## SOLID principles

- Single Responsibility: each module, function, and component should have one reason to change.
- Open/Closed: extend behavior through clear interfaces or adapters instead of repeatedly editing unrelated branches.
- Liskov Substitution: replacements must preserve the contracts expected by callers.
- Interface Segregation: keep APIs and helpers focused; do not force callers to depend on unrelated operations.
- Dependency Inversion: business logic depends on small abstractions for storage, network, and device services.

When a change violates a principle, record the tradeoff in `docs/decisions/`.

## Requested workflow integrations

The user requested GSD, CodeRabbit, Ponytail Minimalism, and Ralph Loop. Their
availability must be verified before use. Until connected, use these local
policies as the fallback:

- GSD: define the goal, scope, acceptance checks, and handoff before coding.
- CodeRabbit: review the diff for correctness, security, maintainability, and tests.
- Ponytail Minimalism: remove unnecessary UI, dependencies, abstractions, and duplication.
- Ralph Loop: repeat inspect -> change -> validate until the acceptance checks pass.

These are project policies, not claims that external plugins are installed.

## Documentation and Obsidian

- Keep project documentation in `docs/`.
- Update the relevant documentation whenever architecture, workflows, database,
  AR behavior, APIs, or operational setup changes.
- Run `scripts/watch-obsidian.ps1` to mirror documentation into the configured
  Obsidian vault. Set `OBSIDIAN_VAULT_PATH` first.
- Do not copy secrets, build outputs, browser profiles, generated APKs, or
  temporary files into the vault.

## Verification required before handoff

- Run JavaScript syntax checks for changed JS files.
- Run the Android compile/build when Android code changes.
- Validate database SQL against the actual Supabase project when credentials and
  access are available.
- State clearly what was verified locally and what still requires a live device,
  Supabase project, browser, or external integration.
