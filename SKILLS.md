# Spring Cloud GCP AI Agent Skills: Backporting

This document defines the standard operating procedures and conventions for AI agents performing backporting tasks in the `spring-cloud-gcp` repository.

---

## Skill: Backporting Pull Requests

### 1. Objective
Backport pull requests that have been merged into the `main` branch across to older active release branches (e.g., `6.x`, `7.x`).

### 2. Prerequisites
- Access to the `git` command line tool.
- Access to the GitHub CLI (`gh`), authenticated with sufficient repository scopes to create branches and pull requests.

### 3. Branching & Naming Conventions
- **Target Release Branches:** Active support branches such as `6.x` and `7.x`.
- **Local/Head Branch Name:** `<target-branch>-cherry-pick-<short-commit-hash>`
  - *Example:* `6.x-cherry-pick-366c6fe` (where `366c6fe` is the 7-character short hash of the commit from `main`).

### 4. Pull Request Metadata Conventions
To ensure consistency and traceability across releases, backport pull requests MUST adhere to the following formatting rules:
- **PR Base:** The target release branch (e.g., `6.x`).
- **PR Title:** Must EXACTLY match the original PR title from `main`, including the original PR number suffix.
  - *Example:* `fix: NullPointerException in spring-cloud-gcp-data-spanner (#4383)`
- **PR Body:** Must contain exactly the string `backport ` followed by the full GitHub URL of the original pull request.
  - *Example:* `backport https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/4383`

---

### 5. Step-by-Step Execution Workflow

When instructed by a user to backport one or more PRs, follow this exact sequence for each PR and target branch combination:

#### Step 1: Identify Commit Hash
Find the commit hash of the merged PR on the `main` branch.
```bash
git log --oneline origin/main
```

#### Step 2: Fetch Upstream State
Ensure local tracking branches are up to date.
```bash
git fetch origin
```

#### Step 3: Create Tracking Branch
Checkout a new branch from the target release branch.
```bash
git checkout -b <target-branch>-cherry-pick-<short-hash> origin/<target-branch>
```

#### Step 4: Cherry-Pick Commit
Cherry-pick the commit from `main`.
```bash
git cherry-pick <full-commit-hash>
```
*Note:* If merge conflicts arise, resolve them according to the target branch's baseline dependencies and continue the cherry-pick.

#### Step 5: Push Branch to Upstream
Push the newly created branch to the remote repository.
```bash
git push origin <target-branch>-cherry-pick-<short-hash>
```

#### Step 6: Create Pull Request
Create the pull request via GitHub CLI (`gh`), specifying the target base branch, exact title, and body.
```bash
gh pr create \
  --base <target-branch> \
  --title "<original-pr-title> (#<original-pr-number>)" \
  --body "backport https://github.com/GoogleCloudPlatform/spring-cloud-gcp/pull/<original-pr-number>"
```

---

### 6. Important Operating Principles
1. **Atomic Backports:** Always backport each pull request individually. Do not combine multiple PRs into a single cherry-pick branch or backport PR unless explicitly instructed by the user.
2. **Author Attribution:** Git cherry-pick preserves the original commit authorship automatically. Do not alter the commit author during the process.
3. **Verification:** Verify that the PR was successfully created and verify the CI checks once opened.
