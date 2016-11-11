package com.github.bzumhagen.sct;

import com.github.zafarkhaja.semver.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class GitChangelogTest {

    private static final ChangelogConfiguration DEFAULT_CONFIG = new ChangelogConfiguration();
    private static final String DEFAULT_DESCRIPTION = "This is a new change";
    private static final String ADDED = "added";
    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String DEFAULT_REFERENCE = "TEST-123";

    @Test
    public void fullChangelog() throws IOException, GitAPIException {
        File gitDir = Files.createTempDirectory("changelogTestRepo").toFile();
        Git gitRepo = Git.init().setDirectory(gitDir).call();
        gitRepo.commit()
                .setAllowEmpty(true)
                .setMessage(
                    String.format(
                        "%s\n\nThis is the body of the change\n\nversion: %s\ntag: %s\nresolves: %s",
                        DEFAULT_DESCRIPTION,
                        DEFAULT_VERSION,
                        ADDED,
                        DEFAULT_REFERENCE
                    )
                ).call();

        GitChangelog changelog = new GitChangelog(DEFAULT_CONFIG, gitDir);
        ChangelogChange expectedChange =
                new ChangelogChange(DEFAULT_DESCRIPTION, Version.valueOf(DEFAULT_VERSION), ADDED, DEFAULT_REFERENCE);

        assertTrue(changelog.getChanges().get(0).equals(expectedChange));
    }
}
