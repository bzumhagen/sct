package com.github.bzumhagen.sct;

import java.nio.file.Path;
import java.util.List;

public interface Changelog {
    public List<ChangelogChange> getChanges();
    public void toMarkdown(Path pathToWriteFile);
}
