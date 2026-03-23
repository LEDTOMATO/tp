package meditrack.ui.screen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import meditrack.commons.core.Constants;
import meditrack.logic.Logic;
import meditrack.logic.commands.GenerateResupplyReportCommand;
import meditrack.logic.commands.GenerateResupplyReportCommand.ReportEntry;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.logic.parser.CommandType;
import meditrack.logic.parser.Parser;
import meditrack.logic.parser.exceptions.ParseException;
import meditrack.model.ModelManager;

/**
 * Resupply Report screen for the Logistics Officer.
 *
 * <p>Clicking "Generate Report" flags supplies that are low stock
 * (below 20 units) or expiring within 30 days. Each flagged item
 * shows the reason: Low Stock, Expiring Soon, or Both. If nothing
 * is flagged an all-clear message is shown instead.
 */
public class ResupplyReportScreen extends VBox {

    private final ModelManager model;
    private final Logic logic;

    private final TableView<ReportRow> reportTable = new TableView<>();
    private final Label statusLabel = new Label();
    private final Label errorLabel = new Label();

    public ResupplyReportScreen(ModelManager model, Logic logic) {
        this.model = model;
        this.logic = logic;
        buildUi();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        setSpacing(14);
        setPadding(new Insets(20));

        Label title = new Label("Resupply Report");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 5; -fx-cursor: hand;");
        generateBtn.setOnAction(e -> handleGenerateReport());

        HBox btnRow = new HBox(10, generateBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        TableColumn<ReportRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().name));

        TableColumn<ReportRow, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setPrefWidth(100);
        qtyCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().quantity));

        TableColumn<ReportRow, LocalDate> expiryCol = new TableColumn<>("Expiry Date");
        expiryCol.setPrefWidth(130);
        expiryCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().expiryDate));

        TableColumn<ReportRow, String> reasonCol = new TableColumn<>("Reason Flagged");
        reasonCol.setPrefWidth(150);
        reasonCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().reason));

        reportTable.getColumns().addAll(nameCol, qtyCol, expiryCol, reasonCol);

        getChildren().addAll(title, btnRow, errorLabel, statusLabel, reportTable);
        VBox.setVgrow(reportTable, Priority.ALWAYS);
    }

    /**
     * Validates input, runs the command through Logic (so data is saved), then fills the table
     * using the same merge rules as {@link GenerateResupplyReportCommand}.
     */
    private void handleGenerateReport() {
        clearMessages();

        Parser parser = new Parser(model);
        try {
            parser.validate(CommandType.GENERATE_RESUPPLY_REPORT, Map.of());
        } catch (ParseException e) {
            showError(e.getMessage());
            return;
        }

        try {
            logic.executeCommand(new GenerateResupplyReportCommand(
                    Constants.LOW_STOCK_THRESHOLD_QUANTITY,
                    Constants.EXPIRY_THRESHOLD_DAYS));
        } catch (CommandException e) {
            showError(e.getMessage());
            return;
        }

        List<ReportEntry> entries = GenerateResupplyReportCommand.collectFlaggedEntries(model,
                Constants.LOW_STOCK_THRESHOLD_QUANTITY,
                Constants.EXPIRY_THRESHOLD_DAYS);

        if (entries.isEmpty()) {
            showStatus(GenerateResupplyReportCommand.MESSAGE_ALL_CLEAR);
            reportTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<ReportRow> rows = new ArrayList<>();
        for (ReportEntry e : entries) {
            rows.add(new ReportRow(
                    e.getSupply().getName(),
                    e.getSupply().getQuantity(),
                    e.getSupply().getExpiryDate(),
                    e.getReason()));
        }
        reportTable.setItems(FXCollections.observableArrayList(rows));
    }

    private void showStatus(String msg) {
        statusLabel.setText(msg);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearMessages() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /** One row in the resupply report table. */
    public static class ReportRow {
        final String name;
        final int quantity;
        final LocalDate expiryDate;
        final String reason;

        public ReportRow(String name, int quantity, LocalDate expiryDate, String reason) {
            this.name = name;
            this.quantity = quantity;
            this.expiryDate = expiryDate;
            this.reason = reason;
        }
    }
}
