package br.ufrn.imd.model.entities;

import java.util.Objects;

public class VersionedKey implements Comparable<VersionedKey> {
    private final String key;
    private final long version;

    public VersionedKey(String key, long version) {
        this.key = key;
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public int compareTo(VersionedKey o) {
        int keyCompare = this.key.compareTo(o.key);
        if (keyCompare != 0) {
            return keyCompare;
        }
        return Long.compare(this.version, o.version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionedKey that)) return false;
        return version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version);
    }

    @Override
    public String toString() {
        return String.format("%d_%s", version, key);
    }
}
