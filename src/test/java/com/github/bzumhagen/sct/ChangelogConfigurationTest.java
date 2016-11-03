package com.github.bzumhagen.sct;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangelogConfigurationTest {

    @Test
    public void defaultTags() {
        ChangelogConfiguration defaultConfig = new ChangelogConfiguration();
        Set<String> expectedTags = new HashSet<String>();
        expectedTags.add("added");
        expectedTags.add("removed");
        expectedTags.add("changed");
        expectedTags.add("deprecated");

        assertEquals(defaultConfig.getTags(), expectedTags);
    }
}
