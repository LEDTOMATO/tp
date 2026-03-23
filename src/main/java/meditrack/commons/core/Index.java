package meditrack.commons.core;

/**
 * Wraps an index value. Internally stored as zero-based but can be
 * created from the one-based indices shown in the UI tables.
 */
public class Index {

    private final int zeroBasedIndex;

    private Index(int zeroBasedIndex) {
        if (zeroBasedIndex < 0) {
            throw new IllegalArgumentException("Index must be non-negative.");
        }
        this.zeroBasedIndex = zeroBasedIndex;
    }

    public static Index fromZeroBased(int zeroBasedIndex) {
        return new Index(zeroBasedIndex);
    }

    public static Index fromOneBased(int oneBasedIndex) {
        return new Index(oneBasedIndex - 1);
    }

    public int getZeroBased() {
        return zeroBasedIndex;
    }

    public int getOneBased() {
        return zeroBasedIndex + 1;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Index)) {
            return false;
        }
        return zeroBasedIndex == ((Index) other).zeroBasedIndex;
    }

    @Override
    public int hashCode() {
        return zeroBasedIndex;
    }

    @Override
    public String toString() {
        return String.valueOf(getOneBased());
    }
}
