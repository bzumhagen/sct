package com.github.bzumhagen.sct;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashSet;
import java.util.Set;

final class ChangelogConfiguration {
    private Set<String> tags;

    ChangelogConfiguration() {
        Config conf = ConfigFactory.load();
        tags = new HashSet<String>(conf.getStringList("sct.tags"));
    }

    Set<String> getTags() {
        return tags;
    }
}
