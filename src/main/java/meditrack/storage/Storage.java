package meditrack.storage;

import java.io.IOException;
import java.util.Optional;

import meditrack.model.ReadOnlyMediTrack;

/**
 * API of the Storage component.
 * Defines the contract for reading from and writing to the local hard drive.
 */
public interface Storage {

    /**
     * Loads model data from disk, or empty if missing or unreadable.
     */
    Optional<ReadOnlyMediTrack> readMediTrackData();

    /**
     * Writes the given snapshot to the local JSON file.
     *
     * @param data model state to persist
     * @throws IOException if the file cannot be written
     */
    void saveMediTrackData(ReadOnlyMediTrack data) throws IOException;
}