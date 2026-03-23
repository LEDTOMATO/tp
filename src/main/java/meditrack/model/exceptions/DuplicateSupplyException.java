package meditrack.model.exceptions;

/**
 * Thrown when trying to add a supply whose name already exists (case-insensitive).
 */
public class DuplicateSupplyException extends RuntimeException {
    public DuplicateSupplyException() {
        super("A supply with this name already exists in the inventory.");
    }
}
