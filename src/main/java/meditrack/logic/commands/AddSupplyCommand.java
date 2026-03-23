package meditrack.logic.commands;

import java.time.LocalDate;
import java.util.Objects;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.Role;
import meditrack.model.Supply;
import meditrack.model.exceptions.DuplicateSupplyException;

/**
 * Adds a new supply item to the inventory.
 * Only the Field Medic role can run this command.
 */
public class AddSupplyCommand extends Command {

    public static final String MESSAGE_SUCCESS = "New supply added: %s (Qty: %d, Expiry: %s)";
    public static final String MESSAGE_DUPLICATE = "A supply with this name already exists in the inventory.";

    private final String name;
    private final int quantity;
    private final LocalDate expiryDate;

    public AddSupplyCommand(String name, int quantity, LocalDate expiryDate) {
        this.name = Objects.requireNonNull(name);
        this.quantity = quantity;
        this.expiryDate = Objects.requireNonNull(expiryDate);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        try {
            Supply supply = new Supply(name, quantity, expiryDate);
            model.addSupply(supply);
        } catch (DuplicateSupplyException e) {
            throw new CommandException(MESSAGE_DUPLICATE);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, name, quantity, expiryDate));
    }

    @Override
    public Role getRequiredRole() {
        return Role.FIELD_MEDIC;
    }
}
