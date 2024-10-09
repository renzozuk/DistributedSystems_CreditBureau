package br.ufrn.imd.model.entities;

public class VersionedKey implements Comparable<VersionedKey> {
    private String key;
    private long version;

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
    public String toString() {
        return String.format("%d_%s", version, key);
    }
}
