package meditrack.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import meditrack.commons.core.Index;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.exceptions.InvalidIndexException;

/**
 * Concrete implementation of {@link Model}.
 *
 * <p>Wraps a {@link MediTrack} data object and a {@link Session}, and exposes
 * supply and personnel operations for the rest of the app.
 */
public class ModelManager implements Model {

    private static final String MSG_DUPLICATE =
            "A personnel member named \"%s\" already exists.";
    private static final String MSG_OUT_OF_BOUNDS =
            "Index %d is out of bounds. The list currently has %d member(s).";
    private static final String MSG_NO_FIT =
            "Cannot generate roster: no personnel with status FIT currently exist.";

    private final MediTrack mediTrack;
    private final Session session;

    /**
     * @param mediTrack backing data store (often loaded from {@code Storage})
     */
    public ModelManager(MediTrack mediTrack) {
        this.mediTrack = mediTrack;
        this.session = Session.getInstance();
    }

    /** Creates a manager with an empty {@link MediTrack} (e.g. for tests). */
    public ModelManager() {
        this(new MediTrack());
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setRole(Role role) {
        session.setRole(role);
    }

    @Override
    public void addSupply(Supply supply) {
        mediTrack.addSupply(supply);
    }

    @Override
    public void editSupply(Index targetIndex, Supply editedSupply) {
        int zeroIndex = targetIndex.getZeroBased();
        ObservableList<Supply> internalList = mediTrack.getInternalSupplyList();
        if (zeroIndex < 0 || zeroIndex >= internalList.size()) {
            throw new InvalidIndexException();
        }
        mediTrack.setSupply(zeroIndex, editedSupply);
    }

    @Override
    public Supply deleteSupply(Index targetIndex) {
        int zeroIndex = targetIndex.getZeroBased();
        ObservableList<Supply> internalList = mediTrack.getInternalSupplyList();
        if (zeroIndex < 0 || zeroIndex >= internalList.size()) {
            throw new InvalidIndexException();
        }
        return mediTrack.removeSupply(zeroIndex);
    }

    @Override
    public ObservableList<Supply> getFilteredSupplyList() {
        return mediTrack.getSupplyList();
    }

    @Override
    public List<Supply> getExpiringSupplies(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(daysThreshold);
        return mediTrack.getInternalSupplyList().stream()
                .filter(s -> !s.getExpiryDate().isBefore(today) && !s.getExpiryDate().isAfter(cutoff))
                .sorted(Comparator.comparing(Supply::getExpiryDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Supply> getLowStockSupplies(int quantityThreshold) {
        return mediTrack.getInternalSupplyList().stream()
                .filter(s -> s.getQuantity() < quantityThreshold)
                .sorted(Comparator.comparingInt(Supply::getQuantity))
                .collect(Collectors.toList());
    }

    @Override
    public ReadOnlyMediTrack getMediTrack() {
        return mediTrack;
    }

    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return mediTrack.getPersonnelList();
    }

    @Override
    public void addPersonnel(String name, Status status) throws CommandException {
        Personnel candidate = new Personnel(name, status);
        for (Personnel existing : getInternalPersonnelList()) {
            if (existing.equals(candidate)) {
                throw new CommandException(String.format(MSG_DUPLICATE, name));
            }
        }
        getInternalPersonnelList().add(candidate);
    }

    @Override
    public Personnel deletePersonnel(int oneBasedIndex) throws CommandException {
        List<Personnel> list = getInternalPersonnelList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        return list.remove(oneBasedIndex - 1);
    }

    @Override
    public void setPersonnelStatus(int oneBasedIndex, Status newStatus) throws CommandException {
        List<Personnel> list = getInternalPersonnelList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        list.get(oneBasedIndex - 1).setStatus(newStatus);
    }

    @Override
    public List<Personnel> getFilteredPersonnelList(Status statusFilter) {
        if (statusFilter == null) {
            return Collections.unmodifiableList(new ArrayList<>(getInternalPersonnelList()));
        }
        return getInternalPersonnelList().stream()
                .filter(p -> p.getStatus() == statusFilter)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Personnel> generateRoster() throws CommandException {
        List<Personnel> fit = new ArrayList<>(getFilteredPersonnelList(Status.FIT));
        if (fit.isEmpty()) {
            throw new CommandException(MSG_NO_FIT);
        }
        Collections.shuffle(fit);
        return fit;
    }

    @Override
    public int getPersonnelCount() {
        return getInternalPersonnelList().size();
    }

    private ObservableList<Personnel> getInternalPersonnelList() {
        return mediTrack.getPersonnelObservable();
    }
}
