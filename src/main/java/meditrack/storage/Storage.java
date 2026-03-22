package meditrack.storage;

import java.io.IOException;
import java.util.Optional;

/**
 * API of the Storage component.
 */
public interface Storage {

    /**
     * Checks whether the local data file exists.
     * @return true if data.json does not exist, false if it does.
     */
    boolean isFirstLaunch();

    /**
     * Reads the local JSON file during startup.
     * @return Optional containing the parsed data, or empty if no file found.
     */
    Optional<ReadOnlyMediTrack> readMediTrackData();

    /**
     * Saves the current state of the application to the local JSON file.
     * @param data A read-only snapshot of the current Model data.
     * @throws IOException if the file cannot be written.
     */
    void saveMediTrackData(ReadOnlyMediTrack data) throws IOException;
}