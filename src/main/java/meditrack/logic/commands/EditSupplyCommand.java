package meditrack.logic.commands;

import java.util.Objects;

import meditrack.commons.core.Index;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.Role;
import meditrack.model.Supply;
import meditrack.model.exceptions.InvalidIndexException;

/**
 * Replaces the supply at the given index with the edited version.
 * Only the Field Medic role can run this command.
 */
public class EditSupplyCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Supply at index %d updated to: %s (Qty: %d, Expiry: %s)";
    public static final String MESSAGE_INVALID_INDEX = "The supply index provided is out of bounds.";

    private final Index targetIndex;
    private final Supply editedSupply;

    public EditSupplyCommand(Index targetIndex, Supply editedSupply) {
        this.targetIndex = Objects.requireNonNull(targetIndex);
        this.editedSupply = Objects.requireNonNull(editedSupply);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        try {
            model.editSupply(targetIndex, editedSupply);
        } catch (InvalidIndexException e) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                targetIndex.getOneBased(),
                editedSupply.getName(),
                editedSupply.getQuantity(),
                editedSupply.getExpiryDate()));
    }

    @Override
    public Role getRequiredRole() {
        return Role.FIELD_MEDIC;
    }
}
