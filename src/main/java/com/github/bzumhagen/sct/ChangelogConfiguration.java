package com.github.bzumhagen.sct;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

final class ChangelogConfiguration {
    private Pattern versionPattern;
    private Pattern tagPattern;
    private Pattern referencePattern;
    private Set<String> tags;

    ChangelogConfiguration() {
        Config conf = ConfigFactory.load();
        versionPattern = Pattern.compile(conf.getString("sct.patterns.version"));
        tagPattern = Pattern.compile(conf.getString("sct.patterns.tag"));
        referencePattern = Pattern.compile(conf.getString("sct.patterns.reference"));
        tags = new HashSet<>(conf.getStringList("sct.tags"));
    }

    Set<String> getTags() {
        return tags;
    }

    Pattern getVersionPattern() {
        return versionPattern;
    }

    Pattern getTagPattern() {
        return tagPattern;
    }

    Pattern getReferencePattern() {
        return referencePattern;
    }
}
