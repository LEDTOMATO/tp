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

import meditrack.logic.Logic;
import meditrack.logic.commands.AddSupplyCommand;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.logic.parser.CommandType;
import meditrack.logic.parser.Parser;
import meditrack.logic.parser.exceptions.ParseException;
import meditrack.model.ModelManager;

/**
 * Pop-up dialog for adding a new supply item.
 *
 * <p>Fields: name, quantity, expiry date. Validates through the Parser
 * before constructing and executing the command through Logic.
 */
public class AddSupplyModal {

    /**
     * Shows the Add Supply dialog and handles confirm/cancel.
     *
     * @param model the model (needed by the parser for duplicate checks)
     * @param logic the logic layer to execute the command through
     * @param owner parent window so the dialog stays on top
     */
    public static void show(ModelManager model, Logic logic, Window owner) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Supply");
        dialog.initOwner(owner);

        ButtonType confirmType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);

        // form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Supply name");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        DatePicker expiryPicker = new DatePicker();
        expiryPicker.setPromptText("Expiry date");

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

        // intercept confirm to validate before closing
        dialog.getDialogPane().lookupButton(confirmType).addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
            errorLabel.setText("");

            String name = nameField.getText();
            String qty = qtyField.getText();
            String expiry = expiryPicker.getValue() != null
                    ? expiryPicker.getValue().toString() : "";

            Parser parser = new Parser(model);
            try {
                parser.validate(CommandType.ADD_SUPPLY, Map.of(
                        "name", name, "qty", qty, "expiry", expiry));
            } catch (ParseException e) {
                errorLabel.setText(e.getMessage());
                event.consume();
                return;
            }

            try {
                logic.executeCommand(new AddSupplyCommand(
                        name.trim(),
                        Integer.parseInt(qty.trim()),
                        LocalDate.parse(expiry)));
            } catch (CommandException e) {
                errorLabel.setText(e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }
}
