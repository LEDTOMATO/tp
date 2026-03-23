package meditrack.ui.screen;

import java.time.LocalDate;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import meditrack.logic.Logic;
import meditrack.model.ModelManager;
import meditrack.model.Supply;
import meditrack.ui.modal.AddSupplyModal;
import meditrack.ui.modal.DeleteSupplyModal;
import meditrack.ui.modal.EditSupplyModal;

/**
 * Inventory screen for the Field Medic role.
 *
 * <p>Shows a full table of all supplies with add, edit and delete actions.
 * The table is bound to the ObservableList so it updates automatically
 * whenever the data changes.
 */
public class InventoryScreen extends VBox {

    private final ModelManager model;
    private final Logic logic;

    private final TableView<Supply> table = new TableView<>();

    public InventoryScreen(ModelManager model, Logic logic) {
        this.model = model;
        this.logic = logic;
        buildUi();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        setSpacing(10);
        setPadding(new Insets(20));

        // title
        Label title = new Label("Inventory");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // add supply button
        Button addBtn = new Button("+ Add Supply");
        addBtn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;");
        addBtn.setOnAction(e -> AddSupplyModal.show(model, logic, getScene().getWindow()));

        

        TableColumn<Supply, Number> indexCol = new TableColumn<>("#");
        indexCol.setPrefWidth(50);
        indexCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cell.getValue()) + 1));

        TableColumn<Supply, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getName()));

        TableColumn<Supply, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setPrefWidth(100);
        qtyCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getQuantity()));

        TableColumn<Supply, LocalDate> expiryCol = new TableColumn<>("Expiry Date");
        expiryCol.setPrefWidth(130);
        expiryCol.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getExpiryDate()));

        // actions column with edit + delete buttons per row
        TableColumn<Supply, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(170);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                        + "-fx-padding: 4 10; -fx-background-radius: 3; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                        + "-fx-padding: 4 10; -fx-background-radius: 3; -fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                    Supply s = getTableView().getItems().get(getIndex());
                    int oneIdx = getIndex() + 1;
                    EditSupplyModal.show(model, logic, s, oneIdx, getScene().getWindow());
                });

                deleteBtn.setOnAction(e -> {
                    Supply s = getTableView().getItems().get(getIndex());
                    int oneIdx = getIndex() + 1;
                    DeleteSupplyModal.show(model, logic, s, oneIdx, getScene().getWindow());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(indexCol, nameCol, qtyCol, expiryCol, actionsCol);
        table.setItems(model.getFilteredSupplyList());

        getChildren().addAll(title, addBtn, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }
}
