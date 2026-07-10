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

# Workflow A: Releasing the 'main' Branch

Use this workflow when the user requests a release for the `main` branch (e.g., "Perform a release for the main branch").

### Step 1: Initialize State
Create or read a `.release_status.json` file in the root of the repository to track progress.
```json
{
  "branch": "main",
  "version": "",
  "step": 1,
  "completed_steps": []
}
```

### Step 2: Merge gapic-generator-java-bom PR
1.  Search for open PRs with `gapic-generator-java-bom` in the title:
    ```bash
    gh pr list --base main --search "gapic-generator-java-bom in:title is:open" --json number
    ```
2.  If found, approve workflow runs, approve the PR, and merge it:
    ```bash
    gh pr review <PR_NUMBER> --approve
    gh pr merge <PR_NUMBER> --squash
    ```
3.  **Fallback (If PR is not found)**:
    *   View the Renovate Dependency Dashboard (Issue #1705):
        ```bash
        gh issue view 1705 --json body --jq .body
        ```
    *   Find the checkbox containing `gapic-generator-java-bom` (e.g. `- [ ] ... gapic-generator-java-bom ...`).
    *   If it is unchecked (`- [ ]`), edit the issue to check it (`- [x]`) using `gh issue edit 1705 --body-file <temp_file_with_checked_body>`. This triggers Renovate to create the PR.
    *   Poll for the PR again (up to 30 minutes). If still not found, report failure to the user.

### Step 3: Merge libraries-bom PR (with Autoconfigs)
1.  Search for open PRs with `libraries-bom` in the title:
    ```bash
    gh pr list --base main --search "libraries-bom in:title is:open" --json number
    ```
2.  **Fallback (If PR is not found)**:
    *   Check the Renovate Dependency Dashboard (Issue #1705):
        ```bash
        gh issue view 1705 --json body --jq .body
        ```
    *   Find the checkbox containing `libraries-bom`.
    *   If unchecked, check it by editing the issue body (using `gh issue edit`).
    *   Poll for the PR again (up to 30 minutes). If still not found, report failure.
3.  Once the PR is found, check if it needs a rebase:
    *   If the rebase box (`- [ ] <!-- rebase-check -->`) is in the PR description, edit the PR description to check it (`[x]`) to trigger Renovate rebase.
4.  Trigger the "Generate Spring Auto-Configurations" workflow:
    ```bash
    gh workflow run "Generate Spring Auto-Configurations" --ref main -f branch_name=<PR_BRANCH> -f forked_repo=<FORKED_REPO>
    ```
5.  Wait for `cloud-java-bot` to commit the changes (poll PR commits).
6.  Verify the bot commit:
    *   Ensure all modifications are strictly within `spring-cloud-previews/`.
    *   Ensure `spring-cloud-previews/README.md` is updated in the commit.
7.  Approve and merge the `libraries-bom` PR.

### Step 4: Merge Release PR
1.  Wait for `release-please` to create the Release PR:
    ```bash
    gh pr list --base main --search "release in:title is:open author:app/release-please" --json number
    ```
2.  Detect the release version from the PR title (e.g. `8.0.4`). Update `.release_status.json` with this version.
3.  Approve and merge the Release PR.

### Step 5: Merge Post-Release SNAPSHOT PR
1.  Wait for `release-please` to create the SNAPSHOT bump PR:
    ```bash
    gh pr list --base main --search "SNAPSHOT in:title is:open author:app/release-please" --json number
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
    gh repo fork spring-io/start.spring.io --clone=true --default-branch-only
    ```
2.  Clone it (default directory `temp-start.spring.io`).
3.  Sync with upstream:
    ```bash
    gh repo sync <USERNAME>/start.spring.io --source spring-io/start.spring.io
    ```
4.  Create a branch `update-gcp-<VERSION>` from `main`.
5.  Locate `start-site/src/main/resources/application.yml`.
6.  Update the `spring-cloud-gcp` BOM version (and optionally the compatibility range upper bound if releasing compatibility with a new Boot version).
    *   *Tip*: Use the `update_initializr_yaml.go` script located in the skill's `scripts/` folder:
        ```bash
        go run /workspace/spring-cloud-gcp/.agents/skills/spring-cloud-gcp-release/scripts/update_initializr_yaml.go <file_path> <version> [<boot_max>]
        ```
7.  Commit the changes.
8.  **SAFETY GATE**: Generate the git diff (`git diff HEAD~1`) and include it in your prompt to the user when asking for approval:
    *"I have prepared the changes for spring-io/start.spring.io. Here is the diff:*
    *```diff*
    *<INSERT_DIFF_HERE>*
    *```*
    *Do you approve pushing this change to your fork? (Reply 'Yes, proceed')"*
9.  After approval, push to your fork:
    ```bash
    git push -u origin update-gcp-<VERSION>
    ```
10. Create a PR targeting `spring-io/start.spring.io`'s `main` branch:
    ```bash
    gh pr create --repo spring-io/start.spring.io --title "Upgrade to Spring Cloud GCP <VERSION>" --body "Automated PR to update Spring Cloud GCP."
    ```

### Step 8: Update README.adoc
1.  Update the version strings in `README.adoc` with the newly released version.
2.  Create a local branch `docs-update-readme-<TIMESTAMP>`.
3.  Commit the change.
4.  **SAFETY GATE**: Generate the git diff (`git diff HEAD~1`) and include it in your prompt to the user when asking for approval:
    *"I have prepared the README update. Here is the diff:*
    *```diff*
    *<INSERT_DIFF_HERE>*
    *```*
    *Do you approve pushing this change to origin? (Reply 'Yes, proceed')"*
5.  Push to `origin` (your fork of `spring-cloud-gcp`).
6.  Create a PR targeting `main`.

### Step 9: Final Report
Send a message to the user summarizing the release, including:
*   A summary statement (e.g. "Release of Spring Cloud GCP <VERSION> is complete").
*   Links to all merged PRs:
    *   `gapic-generator-java-bom` PR
    *   `libraries-bom` PR
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

# Workflow B: Releasing Maintenance Branches (e.g. 6.x, 7.x)

Use this workflow when the user requests a release for a maintenance branch (e.g., "Perform a release for the 7.x branch").

> [!IMPORTANT]
> **No Cherry-Picking**: The release agent should NOT perform any code fixes, backports, or cherry-picking of commits during maintenance releases. All required fixes (such as database isolation backports) must be cherry-picked and merged to the target maintenance branch by the user before starting the release process.

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

### Step 2: Merge libraries-bom PR
1.  Search for open PRs with `libraries-bom` in the title:
    ```bash
    gh pr list --base <BRANCH> --search "libraries-bom in:title is:open" --json number
    ```
2.  **Fallback (If PR is not found)**:
    *   Check the Renovate Dependency Dashboard (Issue #1705).
    *   Find and tick the `libraries-bom` checkbox by editing the issue body.
    *   Poll for the PR again (up to 30 minutes). If still not found, report failure.
3.  Once the PR is found, approve and merge it:
    ```bash
    gh pr review <PR_NUMBER> --approve
    gh pr merge <PR_NUMBER> --squash
    ```

### Step 3: Merge Release PR
1.  Wait for `release-please` to create the Release PR:
    ```bash
    gh pr list --base <BRANCH> --search "release in:title is:open author:app/release-please" --json number
    ```
2.  Detect the release version from the PR title (e.g. `7.4.9`). Update `.release_status.json` with this version.
3.  Approve and merge the Release PR.

### Step 4: Merge Post-Release SNAPSHOT PR
1.  Wait for `release-please` to create the SNAPSHOT bump PR:
    ```bash
    gh pr list --base <BRANCH> --search "SNAPSHOT in:title is:open author:app/release-please" --json number
    ```
2.  Approve and merge it.

### Step 5: Verify Publication
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

### Step 6: Update README.adoc
Update the version reference for the released maintenance version in `README.adoc` on the `main` branch.
1.  Checkout the `main` branch and pull latest changes:
    ```bash
    git checkout main
    git pull upstream main
    ```
2.  Create a local branch `docs-update-readme-<VERSION>`.
3.  Update the version string of the released maintenance branch in `README.adoc` (e.g. update `7.4.8` to `7.4.10` in the links list).
4.  Commit the change.
5.  **SAFETY GATE**: Generate the git diff (`git diff HEAD~1`) and ask for approval:
    *"I have prepared the README update. Here is the diff:*
    *```diff*
    *<INSERT_DIFF_HERE>*
    *```*
    *Do you approve pushing this change to origin? (Reply 'Yes, proceed')"*
6.  Push to `origin` (your fork of `spring-cloud-gcp`).
7.  Create a PR targeting `main` branch of `GoogleCloudPlatform/spring-cloud-gcp`.

### Step 7: Final Report
Send a message to the user summarizing the release, including:
*   A summary statement (e.g. "Release of Spring Cloud GCP <VERSION> is complete").
*   Links to all merged PRs:
    *   `libraries-bom` PR
    *   Release PR
    *   Post-release SNAPSHOT PR
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


