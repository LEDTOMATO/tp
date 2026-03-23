package meditrack.ui.screen;

import java.time.LocalDate;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import meditrack.model.ModelManager;
import meditrack.model.Supply;

/**
 * Read-only view of the full inventory for the Logistics Officer.
 *
 * <p>No action buttons — the logistics officer can only view supply levels
 * here, not edit them. Editing is the Field Medic's job.
 */
public class SupplyLevelsScreen extends VBox {

    private final ModelManager model;
    private final TableView<Supply> table = new TableView<>();

    public SupplyLevelsScreen(ModelManager model) {
        this.model = model;
        buildUi();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Supply Levels");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        

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

        table.getColumns().addAll(indexCol, nameCol, qtyCol, expiryCol);
        table.setItems(model.getFilteredSupplyList());

        getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }
}
