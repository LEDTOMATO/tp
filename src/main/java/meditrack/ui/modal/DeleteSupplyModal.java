package meditrack.ui.modal;

import java.util.Map;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import meditrack.commons.core.Index;
import meditrack.logic.Logic;
import meditrack.logic.commands.DeleteSupplyCommand;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.logic.parser.CommandType;
import meditrack.logic.parser.Parser;
import meditrack.logic.parser.exceptions.ParseException;
import meditrack.model.ModelManager;
import meditrack.model.Supply;

/**
 * Simple yes / no confirmation dialog for deleting a supply.
 *
 * <p>If the user clicks OK the command is executed through Logic.
 * If they cancel nothing happens.
 */
public class DeleteSupplyModal {

    /**
     * Shows a confirmation alert and deletes the supply if the user agrees.
     *
     * @param logic         logic layer to execute the delete command
     * @param supply        the supply about to be deleted (used for the message)
     * @param oneBasedIndex 1-based index of the supply in the list
     * @param owner         parent window
     */
    public static void show(ModelManager model, Logic logic, Supply supply,
                            int oneBasedIndex, Window owner) {
        Parser parser = new Parser(model);
        try {
            parser.validate(CommandType.DELETE_SUPPLY,
                    Map.of("index", String.valueOf(oneBasedIndex)));
        } catch (ParseException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage());
            error.initOwner(owner);
            error.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Supply");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete \"" + supply.getName() + "\"?");
        alert.initOwner(owner);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                logic.executeCommand(new DeleteSupplyCommand(Index.fromOneBased(oneBasedIndex)));
            } catch (CommandException e) {
                Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage());
                error.initOwner(owner);
                error.showAndWait();
            }
        }
    }
}
