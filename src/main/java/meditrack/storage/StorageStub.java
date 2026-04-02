package meditrack.storage;

import java.io.IOException;
import java.util.Optional;

import meditrack.model.ReadOnlyMediTrack;

/**
 * Stub storage that doesn't actually save anything.
 * Used during development and testing until the real storage is wired up.
 */
public class StorageStub implements Storage {

    /** Always returns empty. */
    @Override
    public Optional<ReadOnlyMediTrack> readMediTrackData() {
        return Optional.empty();
    }

    /** Does nothing. */
    @Override
    public void saveMediTrackData(ReadOnlyMediTrack data) throws IOException {
        
    }
}
