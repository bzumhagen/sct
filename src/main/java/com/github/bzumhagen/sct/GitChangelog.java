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

public class GitChangelog implements Changelog {
    private List<ChangelogChange> changes;
    private File gitDir;

    GitChangelog(File gitDir) {
        this.gitDir = gitDir;
        this.changes = buildChanges(gitDir);
    }

    GitChangelog(File gitDir, Version startVersion, Version endVersion) {
        this.gitDir = gitDir;
        this.changes = buildChanges(gitDir, startVersion, endVersion);
    }

    private List<ChangelogChange> buildChanges(File gitDir) {
        return buildChanges(gitDir, null, null);
    }

    private List<ChangelogChange> buildChanges(File gitDir, Version startVersion, Version endVersion) {
        List<ChangelogChange> builtChanges = new ArrayList<>();
        RepositoryBuilder repositoryBuilder =
            new RepositoryBuilder()
                .setMustExist(true)
                .setGitDir(gitDir);
        try {
            Repository repository = repositoryBuilder.build();
            Git git = new Git(repository);
            for(RevCommit commit : git.log().call()) {
                buildChange(commit).map(builtChanges::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Optional<ChangelogChange> buildChange(RevCommit commit) {
        String commitMessage = commit.getFullMessage();
        String description = commit.getShortMessage();
        Optional<String> tag = getTagFromMessage(commitMessage);
        Optional<Version> version = getVersionFromMessage(commitMessage);
        if(tag.isPresent() && version.isPresent()) {
            ChangelogChange change = new ChangelogChange(description, tag.get(), version.get());
            return Optional.of(change);
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getTagFromMessage(String commitMessage) {
        return getMatchFor("\ntag: (.*?)", commitMessage);
    }

    private Optional<Version> getVersionFromMessage(String commitMessage) {
        Optional<String> version = getMatchFor("\nversion: (.*?)", commitMessage);
        return version.map(Version::valueOf);
    }

    private Optional<String> getMatchFor(String regex, String data) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
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
