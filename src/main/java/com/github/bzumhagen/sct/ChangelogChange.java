package com.github.bzumhagen.sct;

import com.github.zafarkhaja.semver.Version;

final class ChangelogChange {
    private String description;
    private String type;
    private Version version;

    ChangelogChange(String description, String type, Version version) {
        this.description = description;
        this.type = type;
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Version getVersion() {
        return version;
    }
}
