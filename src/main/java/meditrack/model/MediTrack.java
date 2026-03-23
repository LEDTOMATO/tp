package meditrack.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import meditrack.model.exceptions.DuplicateSupplyException;

/**
 * Root data container that holds both the supply and personnel lists.
 * The public getters return unmodifiable lists so nothing outside can
 * accidentally mess with the data directly.
 */
public class MediTrack implements ReadOnlyMediTrack {

    private final ObservableList<Supply> supplies = FXCollections.observableArrayList();
    final ObservableList<Personnel> personnel = FXCollections.observableArrayList();

    /**
     * Appends a {@link Personnel} record directly to the internal list.
     * Used by {@code StorageManager} when loading data from disk.
     * No duplicate check is performed here — callers are responsible for validation.
     */
    public void addPersonnelRecord(Personnel p) {
        personnel.add(p);
    }

    /**
     * Returns the live mutable personnel list (same package only; used by {@link ModelManager}).
     */
    ObservableList<Personnel> getPersonnelObservable() {
        return personnel;
    }

    @Override
    public ObservableList<Supply> getSupplyList() {
        return FXCollections.unmodifiableObservableList(supplies);
    }

    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return FXCollections.unmodifiableObservableList(personnel);
    }

    /** Checks for duplicate supply name (case-insensitive). */
    public boolean hasSupply(Supply supply) {
        return supplies.stream().anyMatch(s -> s.equals(supply));
    }

    public void addSupply(Supply supply) {
        if (hasSupply(supply)) {
            throw new DuplicateSupplyException();
        }
        supplies.add(supply);
    }

    public void setSupply(int index, Supply editedSupply) {
        supplies.set(index, editedSupply);
    }

    public Supply removeSupply(int index) {
        return supplies.remove(index);
    }

    /** Returns the internal modifiable list — only ModelManager should call this. */
    public ObservableList<Supply> getInternalSupplyList() {
        return supplies;
    }

    /** Appends a supply without duplicate check — used by StorageManager when loading from disk. */
    public void addSupplyRecord(Supply s) {
        supplies.add(s);
    }
}