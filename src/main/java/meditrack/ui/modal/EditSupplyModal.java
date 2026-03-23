package meditrack.ui.modal;

import java.time.LocalDate;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import meditrack.commons.core.Index;
import meditrack.logic.Logic;
import meditrack.logic.commands.EditSupplyCommand;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.logic.parser.CommandType;
import meditrack.logic.parser.Parser;
import meditrack.logic.parser.exceptions.ParseException;
import meditrack.model.ModelManager;
import meditrack.model.Supply;

/**
 * Pop-up dialog for editing an existing supply item.
 *
 * <p>Pre-fills the form with the current values of the selected supply.
 * Same validation and command flow as AddSupplyModal.
 */
public class EditSupplyModal {

    /**
     * Shows the Edit Supply dialog for the given supply row.
     *
     * @param model         the model (for parser validation)
     * @param logic         logic layer to execute through
     * @param current       the supply being edited (pre-fills the fields)
     * @param oneBasedIndex 1-based row index of the supply in the table
     * @param owner         parent window
     */
    public static void show(ModelManager model, Logic logic, Supply current,
                            int oneBasedIndex, Window owner) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Supply");
        dialog.initOwner(owner);

        ButtonType confirmType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // pre-fill with current values
        TextField nameField = new TextField(current.getName());
        TextField qtyField = new TextField(String.valueOf(current.getQuantity()));
        DatePicker expiryPicker = new DatePicker(current.getExpiryDate());

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(qtyField, 1, 1);
        grid.add(new Label("Expiry Date:"), 0, 2);
        grid.add(expiryPicker, 1, 2);
        grid.add(errorLabel, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().lookupButton(confirmType).addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
            errorLabel.setText("");

            String name = nameField.getText();
            String qty = qtyField.getText();
            String expiry = expiryPicker.getValue() != null
                    ? expiryPicker.getValue().toString() : "";

            Parser parser = new Parser(model);
            try {
                parser.validate(CommandType.EDIT_SUPPLY, Map.of(
                        "name", name, "qty", qty, "expiry", expiry,
                        "index", String.valueOf(oneBasedIndex)));
            } catch (ParseException e) {
                errorLabel.setText(e.getMessage());
                event.consume();
                return;
            }

            try {
                Supply edited = new Supply(
                        name.trim(),
                        Integer.parseInt(qty.trim()),
                        LocalDate.parse(expiry));
                logic.executeCommand(
                        new EditSupplyCommand(Index.fromOneBased(oneBasedIndex), edited));
            } catch (CommandException e) {
                errorLabel.setText(e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }
}
