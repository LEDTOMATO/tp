package meditrack.ui.screen;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import meditrack.commons.core.Constants;
import meditrack.model.ModelManager;
import meditrack.model.Supply;

/**
 * Read-only screen showing supplies expiring within 30 days.
 *
 * <p>Available to the Field Medic. No action buttons — this is purely
 * informational so the medic knows what's about to expire.
 */
public class ExpiringSoonScreen extends VBox {

    private final ModelManager model;
    private final TableView<Supply> table = new TableView<>();

    public ExpiringSoonScreen(ModelManager model) {
        this.model = model;
        buildUi();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Expiring Soon (within " + Constants.EXPIRY_THRESHOLD_DAYS + " days)");
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

        // populate with the filtered expiring list
        List<Supply> expiring = model.getExpiringSupplies(Constants.EXPIRY_THRESHOLD_DAYS);
        table.setItems(FXCollections.observableArrayList(expiring));

        getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }
}
