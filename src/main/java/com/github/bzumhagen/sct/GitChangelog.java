package com.github.bzumhagen.sct;

import com.github.zafarkhaja.semver.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GitChangelog implements Changelog {
    private ChangelogConfiguration config;
    private List<ChangelogChange> changes;
    private File gitDir;

    GitChangelog(ChangelogConfiguration config, File gitDir) {
        this.config = config;
        this.gitDir = gitDir;
        this.changes = buildChanges(gitDir);
    }

    GitChangelog(ChangelogConfiguration config, File gitDir, Version startVersion, Version endVersion) {
        this.config = config;
        this.gitDir = gitDir;
        this.changes = buildChanges(gitDir, startVersion, endVersion);
    }

    private List<ChangelogChange> buildChanges(File gitDir) {
        return buildChanges(gitDir, null, null);
    }

    private List<ChangelogChange> buildChanges(File gitDir, Version startVersion, Version endVersion) {
        List<ChangelogChange> builtChanges = new ArrayList<>();
        try {
            Git git = Git.open(gitDir);
            for(RevCommit commit : git.log().call()) {
                buildChange(commit).map(builtChanges::add);
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return builtChanges;
    }

    private Optional<ChangelogChange> buildChange(RevCommit commit) {
        String commitMessage = commit.getFullMessage();
        String description = commit.getShortMessage();
        Optional<Version> version = getVersionFromMessage(commitMessage);
        Optional<String> tag = getMatchFor(config.getTagPattern(), commitMessage);
        Optional<String> reference = getMatchFor(config.getReferencePattern(), commitMessage);
        if(tag.isPresent() && version.isPresent()) {
            ChangelogChange change = new ChangelogChange(description, version.get(), tag.get(), reference.orElse(""));
            return Optional.of(change);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Version> getVersionFromMessage(String commitMessage) {
        Optional<String> version = getMatchFor(config.getVersionPattern(), commitMessage);
        return version.map(Version::valueOf);
    }

    private Optional<String> getMatchFor(Pattern regex, String data) {
        Matcher matcher = regex.matcher(data);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ChangelogChange> getChanges() {
        return this.changes;
    }

    @Override
    public void toMarkdown(Path pathToWriteFile) {
        //TODO
    }
}
