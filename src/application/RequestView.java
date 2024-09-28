package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import DatabaseClasses.requestDB;

public class RequestView {
    private TableView<Object[]> RequestTable;
    private requestDB requestdb;
    public HBox footerButtons;
    
    public RequestView() {
    	requestdb = new requestDB();
    }

    @SuppressWarnings("unchecked")
	public TableView<Object[]> createRequestTable() {
    	TableView<Object[]> table = new TableView<>();
        table.setEditable(true);

        // Columns
        TableColumn<Object[], String> noColumn = new TableColumn<>("Requisition No");
        noColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));

        TableColumn<Object[], String> roomNoColumn = new TableColumn<>("Room No");
        roomNoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));

        TableColumn<Object[], String> hostelNameColumn = new TableColumn<>("Hostel Name");
        hostelNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        hostelNameColumn.setPrefWidth(170);
        TableColumn<Object[], String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        itemNameColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> itemCategoryColumn = new TableColumn<>("Item Category");
        itemCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 4)));
        itemCategoryColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> quantityColumn = new TableColumn<>("Item Quantity");
        quantityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 5)));
       
     
        TableColumn<Object[], String> dateColumn = new TableColumn<>("Requisition Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 7)));
       
        TableColumn<Object[], String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 6)));
        statusColumn.setPrefWidth(170);
       
        TableColumn<Object[], String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 8)));
        
        table.getColumns().addAll(noColumn, hostelNameColumn, roomNoColumn, itemNameColumn, itemCategoryColumn, quantityColumn, statusColumn, dateColumn,descriptionColumn);

        ObservableList<Object[]> supplierData = FXCollections.observableArrayList(requestdb.requestInfo());
        table.getItems().setAll(supplierData);
        RequestTable = table;
        return table;
    }
    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
    }
    public void searchByHostel(String hostel) {
        // Implement logic to filter the table by hostelName
        String trimmedHostel = hostel.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(requestdb.searchByHostel(trimmedHostel));
        RequestTable.getItems().setAll(filteredData);
    }
    public void searchByRoomNo(String roomNo) {
        // Implement logic to filter the table by room number
        String trimmedRoomNo = roomNo.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(requestdb.searchByRoomNo(trimmedRoomNo));
        RequestTable.getItems().setAll(filteredData);
    }

    public void searchByItems(String items) {
        // Implement logic to filter the table by items
        String trimmedItems = items.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(requestdb.searchByItems(trimmedItems));
        RequestTable.getItems().setAll(filteredData);
    }

    public void searchByItemCategory(String category) {
        // Implement logic to filter the table by item category
        String trimmedCategory = category.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(requestdb.searchByItemCategory(trimmedCategory));
        RequestTable.getItems().setAll(filteredData);
    }

    public void searchByStatus(String status) {
        // Implement logic to filter the table by status
        String trimmedStatus = status.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(requestdb.searchByStatus(trimmedStatus));
        RequestTable.getItems().setAll(filteredData);
    }

    private void openAddRequestPage() {
    	AddRequest addSupplier = new AddRequest(this::refreshRequestTable);
        Stage addSupplierStage = new Stage();
        addSupplier.start(addSupplierStage);

        // Add an event handler to refresh the table when the addhostel stage is closed
        addSupplierStage.setOnCloseRequest(event -> refreshRequestTable());
    }
    
    public void refreshRequestTable() {
    	RequestTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> requestData = FXCollections.observableArrayList(requestdb.requestInfo());
        RequestTable.getItems().setAll(requestData);
    }
    
    private void openEditRequestPage(Object[] selectedRequest) {
        // Get the selected hostel name, room number, and request ID
        String selectedHostelName = getProperty(selectedRequest, 1);
        String selectedRoomNo = getProperty(selectedRequest, 2);
        // Check if the selected hostel and room exist
        if (requestdb.isHostelExists(selectedHostelName) && requestdb.isRoomExists(selectedRoomNo, selectedHostelName)) {
            // The hostel and room exist, proceed to edit the request
            EditRequest editRequest = new EditRequest(selectedRequest, this::refreshRequestTable, requestdb);
            Stage editRequestStage = new Stage();
            editRequest.start(editRequestStage);

            // Add an event handler to refresh the table when the edit request stage is closed
            editRequestStage.setOnCloseRequest(event -> refreshRequestTable());
        } else {
            // Show an alert if the hostel or room does not exist
            showAlert("Error", "Selected hostel or room does not exist. Request can't be edited.");
        }
    }

    public HBox createFooterButtons() {
        Button editRequestButton = new Button("Edit Requisition");
        Button deleteRequestButton =  new Button("Delete Requisition");
        Button addRequestButton = new Button("Add Requisition");
        
        Button RefreshButton = new Button("Refresh");

        HBox footButtons = new HBox(10, RefreshButton,editRequestButton, deleteRequestButton, addRequestButton);
        footButtons.setAlignment(Pos.CENTER_RIGHT);
        footButtons.setPadding(new Insets(10, 10, 10, 0)); // Padding for top, right, and bottom

        // Refresh Hostel table
        RefreshButton.setOnAction(event -> {
        	RequestTable.getItems().clear(); // Clear existing items
            ObservableList<Object[]> requestData1 = FXCollections.observableArrayList(requestdb.requestInfo());
            RequestTable.getItems().setAll(requestData1);
            
            RequestTable.getItems().clear(); // Clear existing items
            ObservableList<Object[]> requestData2 = FXCollections.observableArrayList(requestdb.requestInfo());
            RequestTable.getItems().setAll(requestData2);
        });
        
        // Event handler for Edit Hostel button
        editRequestButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedentry = RequestTable.getSelectionModel().getSelectedItem();

            if (selectedentry != null) {
                // Open the edithostel page with selected data
                openEditRequestPage(selectedentry);
            } else {
                // No row selected, show a warning
            	Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a requisition to edit.");
            	 // Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
            }
        });
     // Event handler for Delete Hostel button
        deleteRequestButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedRequest = RequestTable.getSelectionModel().getSelectedItem();
            int reqNo = (Integer) selectedRequest[0];
            if (selectedRequest != null) {
                // Check the status of the selected request
                String status = getProperty(selectedRequest, 6);

                if ("Accept".equalsIgnoreCase(status) && requestdb.checkDeletion(reqNo)) {
                    // Request is accepted, show a warning
                    showAlert("Error", "Cannot delete accepted requisitions.");
                } else {
                	 // Create an image for the confirmation dialog
                	Image iconImage = new Image("fi.png");
                	// Create a confirmation alert with a custom alert type and set the icon image
                	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this requisition?", ButtonType.OK, ButtonType.CANCEL);
                	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
                	alertStage.getIcons().add(iconImage);
                    // Show confirmation dialog for other statuses
                    if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        // User clicked OK, delete the selected row
                        
                        requestdb.deleteRequestInfo(reqNo);
                        RequestTable.getItems().remove(selectedRequest);
                       // System.out.println("Request deleted!");
                    }
                }
            } else {
                // No row selected, show a warning
            	Alert alert=new Alert(Alert.AlertType.WARNING, "Please select a requisition to delete.");
             // Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
            }
        });

        addRequestButton.setOnAction(event -> {
            // Open the addhostel page
            openAddRequestPage();
        });
        return footButtons;
    }
 // Add a utility method for showing alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }
    
}