---
name: spring-cloud-gcp-release
description: Instructions for performing an automated, sequential release of Spring Cloud GCP.
---

# Spring Cloud GCP Release Workflow

This skill guides the agent through the sequential release process for Spring Cloud GCP. Select the appropriate workflow section based on the branch being released.

## Prerequisites
1.  The GitHub CLI (`gh`) must be installed and authenticated (`gh auth status`).
2.  Your git working directory must be clean.

## Release Guidelines & Safety
*   **Merge Approvals**: You do **not** need to request explicit user approval to merge automated bot PRs (such as the Release PR or the SNAPSHOT bump PR created by `release-please`). You may approve and merge them directly once CI conditions are satisfied.
*   **SNAPSHOT PR Merges**: For post-release SNAPSHOT bump PRs, you can merge them as soon as all **required** status checks pass. You do **not** need to wait for optional checks to complete or pass. If the repository policy blocks the merge due to pending optional checks, use administrator privileges (`--admin` flag) to complete the merge.
*   **Code Change Approvals**: You **must** get explicit user approval before pushing any code fixes or opening any new PRs that you authored (such as the Spring Initializr PR or README update PR).

---

# Unified Release Workflow

Use this workflow to release any branch (e.g. `main` or a maintenance branch like `7.x`).

### Step 1: Initialize State
Create or read a `.release_status.json` file in the root of the repository to track progress.
```json
{
  "branch": "<BRANCH>",
  "version": "",
  "step": 1,
  "completed_steps": []
}
```

> [!IMPORTANT]
> **No Cherry-Picking (Maintenance Branches)**: If the target branch is a maintenance branch (e.g. `7.x`), do NOT perform any code fixes, backports, or cherry-picking of commits. All required fixes must be cherry-picked and merged to the target maintenance branch by the user before starting the release process.

### Step 2: Merge Renovate/Dependabot Dependency Upgrade PRs
Before updating `libraries-bom` or creating the release, check for and merge open dependency upgrade PRs from Renovate or Dependabot created since the last release tag:
1.  Get the date of the latest release tag on the active branch:
    ```bash
    LAST_RELEASE_DATE=$(git log -1 --format=%aI $(git describe --tags --abbrev=0))
    ```
2.  Query open PRs created by Renovate or Dependabot since `LAST_RELEASE_DATE`:
    *   **If releasing `main`**: Exclude `libraries-bom` (which is handled separately in Step 3):
        ```bash
        gh pr list --base main --json number,title,author,createdAt --jq ".[] | select((.author.login == \"renovate\" or .author.login == \"dependabot\" or .author.isBot == true) and (.title | contains(\"libraries-bom\") | not) and .createdAt > \"$LAST_RELEASE_DATE\")"
        ```
    *   **If releasing a maintenance branch (e.g., `7.x`)**: Include all dependency upgrade PRs (including `libraries-bom`):
        ```bash
        gh pr list --base <BRANCH> --json number,title,author,createdAt --jq ".[] | select((.author.login == \"renovate\" or .author.login == \"dependabot\" or .author.isBot == true) and .createdAt > \"$LAST_RELEASE_DATE\")"
        ```
3.  For each found dependency upgrade PR, approve and squash-merge it:
    ```bash
    gh pr review <PR_NUMBER> --approve
    gh pr merge <PR_NUMBER> --squash
    ```
4.  Verify fallback:
    *   **If releasing `main`**: Verify that the `gapic-generator-java-bom` PR is merged. If not open/merged, tick its box in the Renovate Dependency Dashboard (Issue #1705) to trigger it.
    *   **If releasing a maintenance branch**: Verify that the `libraries-bom` PR is merged. If not open/merged, tick its box in the Renovate Dependency Dashboard (Issue #1705) to trigger it.

### Step 3: [Conditional] Merge libraries-bom PR (Main branch only)
*This step is only executed if releasing the `main` branch. For maintenance branches, proceed directly to Step 4.*
1.  Search for open PRs with `libraries-bom` in the title:
    ```bash
    gh pr list --base main --search "libraries-bom in:title is:open" --json number
    ```
2.  Trigger the "Generate Spring Auto-Configurations" workflow:
    ```bash
    gh workflow run "Generate Spring Auto-Configurations" --ref main -f branch_name=<PR_BRANCH> -f forked_repo=<FORKED_REPO>
    ```
3.  Wait for `cloud-java-bot` to commit changes, verify that modifications are strictly within `spring-cloud-previews/` and that `spring-cloud-previews/README.md` is updated.
4.  Approve and squash-merge the `libraries-bom` PR.

### Step 4: Merge Release PR
1.  Wait for `release-please` to create the Release PR on the target branch:
    ```bash
    gh pr list --base <BRANCH> --search "release in:title is:open author:app/release-please" --json number
    ```
2.  Detect the release version from the PR title (e.g. `8.0.4` or `7.4.10`). Update `.release_status.json` with this version.
3.  Approve and squash-merge the Release PR.

### Step 5: Merge Post-Release SNAPSHOT PR
1.  Wait for `release-please` to create the SNAPSHOT bump PR on the target branch:
    ```bash
    gh pr list --base <BRANCH> --search "SNAPSHOT in:title is:open author:app/release-please" --json number
    ```
2.  Approve and merge it.

### Step 6: Verify Publication
1.  **Maven Central (MANDATORY)**: Poll the Maven Central repository URL until the new version is available (timeout 6 hours):
    `https://repo1.maven.org/maven2/com/google/cloud/spring-cloud-gcp/<VERSION>/`
    Also check metadata to ensure it's indexed:
    `https://repo1.maven.org/maven2/com/google/cloud/spring-cloud-gcp/maven-metadata.xml`
    > [!NOTE]
    > **Release Pipeline Delays**: The Maven artifact release pipeline (executed by Kokoro/Louhi) can take longer than 2 hours to complete after the release tag is created. Continue polling Maven Central until the release process completes and the artifacts are indexed.
    > [!TIP]
    > **Checking Kokoro/Sponge**: The agent does not have direct access to search internal Google release dashboard runs (Louhi/Kokoro) due to corp SSO authentication. If the release remains unavailable after a long time, the user can manually retrieve the Sponge invocation ID from Louhi and provide it to the agent. The agent can then use `read_sponge_test_failure_logs` and `list_sponge_artifacts` to diagnose any internal build failures.
    *If Maven Central verification fails or times out, stop the release and report failure.*
2.  **Documentation (OPTIONAL)**: Poll the documentation URLs (timeout 2 hours). Do **not** halt the release if they are missing:
    *   Reference Docs: `https://googlecloudplatform.github.io/spring-cloud-gcp/<VERSION>/reference/html/index.html`
    *   Javadocs: `https://googleapis.dev/java/spring-cloud-gcp/<VERSION>/index.html`
    *Note the publication status (Exists / Missing) of both endpoints to include in the final report.*

### Step 7: Create Spring Initializr PR
Update Spring Initializr with the new Spring Cloud GCP version:
1.  Fork `spring-io/start.spring.io` if not already forked:
    ```bash
    gh repo fork spring-io/start.spring.io --clone=false
    ```
2.  Clean up any pre-existing clone and perform a fresh clone of your fork (using `temp-start.spring.io` as directory):
    ```bash
    rm -rf temp-start.spring.io
    gh repo clone <USERNAME>/start.spring.io temp-start.spring.io
    ```
3.  Navigate to the clone, link the upstream repository, fetch, and hard-reset your `main` branch to match the latest upstream state to prevent unrelated diffs:
    ```bash
    cd temp-start.spring.io
    git remote add upstream https://github.com/spring-io/start.spring.io.git || true
    git fetch upstream main
    git checkout main
    git reset --hard upstream/main
    ```
4.  Sync your fork with the latest upstream state:
    ```bash
    gh repo sync <USERNAME>/start.spring.io --source spring-io/start.spring.io
    ```
5.  Create a branch `update-gcp-<VERSION>` from `main`:
    ```bash
    git checkout -b update-gcp-<VERSION>
    ```
6.  Locate `start-site/src/main/resources/application.yml`.
7.  Update the `spring-cloud-gcp` BOM version for the mapping matching the compatibility range of the released branch.
    *   *Tip*: Use the `update_initializr_yaml.go` script located in the skill's `scripts/` folder:
        ```bash
        go run /workspace/spring-cloud-gcp/.agents/skills/spring-cloud-gcp-release/scripts/update_initializr_yaml.go <file_path> <version>
        ```
8.  Commit the changes with sign-off (DCO requirement):
    ```bash
    git commit -s -m "Upgrade to Spring Cloud GCP <VERSION>"
    ```
9.  **SAFETY GATE**: Generate the git diff (`git diff HEAD~1`) and include it in your prompt to the user when asking for approval:
    *"I have prepared the changes for spring-io/start.spring.io. Here is the diff:*
    *```diff*
    *<INSERT_DIFF_HERE>*
    *```*
    *Do you approve pushing this change to your fork? (Reply 'Yes, proceed')"*
10. After approval, push to your fork:
    ```bash
    git push -u origin update-gcp-<VERSION>
    ```
11. Create a PR targeting `spring-io/start.spring.io`'s `main` branch:
    ```bash
    gh pr create --repo spring-io/start.spring.io --title "Upgrade to Spring Cloud GCP <VERSION>" --body "Automated PR to update Spring Cloud GCP."
    ```

### Step 8: Update README.adoc
Update the version reference in `README.adoc`:
*   **If releasing `main` branch**:
    1.  Update the version strings in `README.adoc` with the newly released version.
    2.  Create a local branch `docs-update-readme-<TIMESTAMP>`.
    3.  Commit the change:
        ```bash
        git commit -m "docs: update README for release <VERSION>"
        ```
    4.  **SAFETY GATE**: Generate the git diff (`git diff HEAD~1`) and include it in your prompt to the user when asking for approval:
        *"I have prepared the README update. Here is the diff:*
        *```diff*
        *<INSERT_DIFF_HERE>*
        *```*
        *Do you approve pushing this change to origin? (Reply 'Yes, proceed')"*
    5.  Push to `origin` (your fork of `spring-cloud-gcp`).
    6.  Create a PR targeting `main`.
*   **If releasing a maintenance branch (e.g., `7.x`)**:
    1.  Checkout the `main` branch and pull latest changes:
        ```bash
        git checkout main
        git pull upstream main
        ```
    2.  Create a local branch `docs-update-readme-<VERSION>`.
    3.  Update the version string of the released maintenance branch in `README.adoc` (e.g. update `7.4.8` to `7.4.10` in the links list).
    4.  Commit the change:
        ```bash
        git commit -m "docs: update README for release <VERSION>"
        ```
    5.  **SAFETY GATE**: Generate the git diff (`git diff HEAD~1`) and ask for approval:
        *"I have prepared the README update. Here is the diff:*
        *```diff*
        *<INSERT_DIFF_HERE>*
        *```*
        *Do you approve pushing this change to origin? (Reply 'Yes, proceed')"*
    6.  Push to `origin` (your fork of `spring-cloud-gcp`).
    7.  Create a PR targeting `main` branch of `GoogleCloudPlatform/spring-cloud-gcp`.

### Step 9: Final Report
Send a message to the user summarizing the release, including:
*   A summary statement (e.g. "Release of Spring Cloud GCP <VERSION> is complete").
*   Links to all merged PRs:
    *   Renovate/Dependabot dependency upgrade PRs (e.g. `libraries-bom`, `gapic-generator-java-bom` PRs)
    *   Release PR
    *   Post-release SNAPSHOT PR
*   Links to new PRs created:
    *   `[ACTION REQUIRED]` Spring Initializr PR (requires review and merge)
    *   `[ACTION REQUIRED]` README update PR (requires review and merge)
*   Links to publications:
    *   Maven Central Artifact
    *   Reference Documentation (If missing, flag as `[ACTION REQUIRED] Reference Documentation is missing`)
    *   Javadocs (If missing, flag as `[ACTION REQUIRED] Javadocs are missing`)

---

# Handling CI Failures (Shared)

If a PR merge fails due to failing CI checks:

1.  **Investigate the Failure**:
    *   Retrieve the list of checks for the PR:
        ```bash
        gh pr checks <PR_NUMBER>
        ```
    *   Find the failing check and retrieve its run log using the GitHub CLI or by navigating to the URL:
        ```bash
        gh run view <RUN_ID> --log
        ```
    *   Determine the root cause (e.g., compile error, test assertion failure, transient connection error).

2.  **Determine if Flaky**:
    *   Check if the failed test is a known flaky test (e.g. search repository issues for the test name, or check memory files for flaky test patterns).
    *   Common indicators of flakiness: transient network timeout, integration test database cleanup failure, or resource exhaustions.

3.  **Flaky Test Recovery (Retry Up to 2 Times)**:
    *   If the test is deemed flaky, trigger a retry of the failed workflow run:
        ```bash
        gh run rerun <RUN_ID> --failed
        ```
    *   Wait for the checks to complete.
    *   If the check fails again, retry one more time (maximum 2 retries total).
    *   If it still fails after the second retry, proceed to step 4.

4.  **Create Code Fix (For Real Bugs or Persistent Failures)**:
    *   Pause the release pipeline.
    *   Create a local branch `fix/<issue-name>` off the branch you are releasing.
    *   Implement the fix locally.
    *   Verify the fix:
        *   Run tests locally to confirm it passes.
        *   Format all modified Java files: `mvn com.spotify.fmt:fmt-maven-plugin:format`.
    *   Commit your fix locally.
    *   **SAFETY GATE**: Output a standalone push confirmation request to the user:
        `"I am ready to push the fix to origin/fix/<issue-name> (force-push: no) with the commit message: '<commit_message>'. Do you approve this remote push? (Please reply with 'Yes, proceed')"`.
    *   After approval, push the branch and create a draft PR:
        ```bash
        git push -u origin fix/<issue-name>
        gh pr create --draft --title "Fix: <description>" --body "Automated PR to fix CI failure during release."
        ```
    *   Provide the PR link to the user for review.
    *   Once the PR is approved by the user and merged, pull the changes locally, check out the release branch, and resume the release steps.

5.  **Permissive Merge Policy (Required vs Optional Checks)**:
    *   Evaluate both required and optional status checks for the release PRs.
    *   **Merge Condition**: You may proceed to merge a PR if:
        1. All **required** status checks pass (verified by `gh pr checks <PR_NUMBER> --required`).
        2. The total count of failing **optional** (non-required) checks is **less than 10** (e.g. SonarCloud or flaky database checks).
    *   **Halt Condition**: If any required check fails, or if **10 or more** optional checks fail, halt the automated release, pause the pipeline, and alert the release manager for manual review.


