package com.github.bzumhagen.sct;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

final class ChangelogChange {
    private String description;
    private Version version;
    private String type;
    private String reference;

    ChangelogChange(String description, Version version, String type, String reference) {
        this.description = description;
        this.version = version;
        this.type = type;
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public Version getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getReference() { return reference; }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(description).
                        append(version).
                        append(type).
                        append(reference).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChangelogChange))
            return false;
        if (obj == this)
            return true;

        ChangelogChange rhs = (ChangelogChange) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(description, rhs.description).
                        append(version, rhs.version).
                        append(type, rhs.type).
                        append(reference, rhs.reference).
                        isEquals();
    }
}
