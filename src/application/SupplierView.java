package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import DatabaseClasses.supplierDB;

public class SupplierView{
    private TableView<Object[]> SupplierTable;
    
    private supplierDB supplierdb;
    private BorderPane borderPane;
    private static ComboBox<String> searchComboBox;
    private static TextField searchTextField;
    static boolean alertShown = false;
    public SupplierView(BorderPane borderPane) {
    	 searchComboBox = new ComboBox<>();
         searchTextField = new TextField();
    	this.borderPane = borderPane;
    	supplierdb = new supplierDB();
 
    }
    public HBox createTopNavigationBar()  {
        HBox topNavigationBar = new HBox(15);
        //topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 20;");
        topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 20;");
        ImageView logoImageView = new ImageView(new String("uet.png"));
        logoImageView.setFitHeight(80);
        logoImageView.setPreserveRatio(true);

        // Set the logoImageView to the left corner
       // HBox.setMargin(logoImageView, new Insets(0, 10, 0, 10)); // Adjust margins as needed

        searchTextField = new TextField();
        searchTextField.setMinWidth(170);
        searchTextField.setMaxWidth(170);
        searchTextField.setPrefWidth(170);
        searchTextField.setVisible(false);

        // Add event listener to searchComboBox
        searchComboBox.setVisible(false);
        
        searchComboBox.setOnAction(event -> {
            if (searchTextField == null) {
               // System.out.println("searchTextField is null!");
            } else {
                String selectedSearchType = searchComboBox.getValue();
                if ("Supplier Name".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Supplier Name");
                }
                
                }
           
        });

        // Add styles to make searchComboBox look like a button
        searchComboBox.setStyle("-fx-background-color: white; -fx-border-color: #ffffff; -fx-text-fill: white; -fx-font-size: 12;");
        searchComboBox.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        searchComboBox.setMinWidth(150);
        searchComboBox.setMaxWidth(150);
        searchComboBox.setPrefWidth(150);

        // Add event listener to searchTextField
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (searchComboBox.getSelectionModel().isEmpty()) {
               
					if (!alertShown) {
					    showAlert("Select Search Type", "Please select a search type from the dropdown.");
					    
							alertShown = true;
						
					}
				
                searchTextField.clear();
            } else {
                // Reset the flag if the selection is made
                alertShown = false;

                String searchType = searchComboBox.getValue();
                if ("Supplier Name".equals(searchType)) {
                	searchBySupplierName(newValue); // Call the method in supplierview to update the table
                }
              
              
            }
        });

       


        // Align the elements to the right corner
        HBox rightButtons = new HBox(searchComboBox, searchTextField);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        // Set the rightButtons HBox to take remaining space
        HBox.setHgrow(rightButtons, Priority.ALWAYS);

        topNavigationBar.getChildren().addAll(logoImageView, rightButtons);

        return topNavigationBar;
    }
    @SuppressWarnings("unchecked")
    public TableView<Object[]> createSupplierTable() {
        TableView<Object[]> table = new TableView<>();
        table.setEditable(true);

        // Columns
        TableColumn<Object[], String> supplierIdColumn = new TableColumn<>("Supplier Id");
        supplierIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));
        supplierIdColumn.setVisible(false); // Make the SupplierId column non-visible

        TableColumn<Object[], String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        nameColumn.setPrefWidth(170);
        TableColumn<Object[], String> billNoColumn = new TableColumn<>("Bill No");
        billNoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));
        billNoColumn.setPrefWidth(170);
        TableColumn<Object[], String> deliverDateColumn = new TableColumn<>("Delivered Date");
        deliverDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        deliverDateColumn.setPrefWidth(170);
        TableColumn<Object[], String> itemQuantColumn = new TableColumn<>("Item Quantity");
        itemQuantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 4)));
        itemQuantColumn.setPrefWidth(170);
        TableColumn<Object[], String> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 5)));
        totalPriceColumn.setPrefWidth(170);
        table.getColumns().addAll(supplierIdColumn, nameColumn, billNoColumn, deliverDateColumn, itemQuantColumn, totalPriceColumn);

        ObservableList<Object[]> supplierData = FXCollections.observableArrayList(supplierdb.supplierInfo());
        table.getItems().setAll(supplierData);
        SupplierTable = table;
        return table;
    }

    private static <T> String getProperty(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index].toString();
        } else {
            return ""; // or handle this case in a way that makes sense for your application
        }
    }
    
    public void searchBySupplierName(String supplierName) {
        // Implement logic to filter the table by supplierName
        String trimmedSupplierName = supplierName.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(supplierdb.searchBySupplierName(trimmedSupplierName));
        SupplierTable.getItems().setAll(filteredData);
    }

    private void openAddSupplierPage() {
    	AddSupplier addSupplier = new AddSupplier(this::refreshSupplierTable);
        Stage addSupplierStage = new Stage();
        addSupplier.start(addSupplierStage);

        // Add an event handler to refresh the table when the addhostel stage is closed
        addSupplierStage.setOnCloseRequest(event -> refreshSupplierTable());
    }
    
    public void refreshSupplierTable() {
    	
    	SupplierTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> supplierData = FXCollections.observableArrayList(supplierdb.supplierInfo());
        SupplierTable.getItems().setAll(supplierData);
    }
    
    private void openEditSupplierPage(Object[] selectedSupplier) {
        EditSupplier editSupplier = new EditSupplier(selectedSupplier, this::refreshSupplierTable,supplierdb);
        Stage editSupplierStage = new Stage();
        editSupplier.start(editSupplierStage);

        // Add an event handler to refresh the table when the edithostel stage is closed
        editSupplierStage.setOnCloseRequest(event -> refreshSupplierTable());
    }
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set the icon image for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("fi.png"));
        alert.showAndWait();
    }
    public HBox createFooterButtons() {
        Button editSupplierButton = new Button("Edit Supplier");
        Button deleteSupplierButton =  new Button("Delete Supplier");
        Button viewItemButton = new Button("View Items");
        Button addSupplierButton = new Button("Add Supplier");
        
        Button viewItemSummaryButton = new Button("Item Summary");

        HBox footButtons = new HBox(10, editSupplierButton, deleteSupplierButton, viewItemButton, addSupplierButton,viewItemSummaryButton);
        footButtons.setAlignment(Pos.CENTER_RIGHT);
        footButtons.setPadding(new Insets(10, 10, 10, 0)); // Padding for top, right, and bottom

        
        // Event handler for Edit Hostel button
        editSupplierButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedentry = SupplierTable.getSelectionModel().getSelectedItem();

            if (selectedentry != null) {
                // Open the edithostel page with selected data
                openEditSupplierPage(selectedentry);
            } else {
                // No row selected, show a warning
               Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a supplier to edit.");
               // Set the icon image for the alert
           	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
           	alertStage.getIcons().add(new Image("fi.png"));

           	// Show the alert
           	alert.showAndWait();
            }
        });
        // Event handler for Delete Hostel button
        deleteSupplierButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedSupplier = SupplierTable.getSelectionModel().getSelectedItem();

            if (selectedSupplier != null) {
            	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            	confirmationAlert.setTitle("Confirmation");
            	confirmationAlert.setHeaderText("Confirmation");
            	 // Create an image for the confirmation dialog
            	Image iconImage = new Image("fi.png");
            	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(iconImage);
            	if((Integer) selectedSupplier[4] == 0) {
                	confirmationAlert.getDialogPane().setContentText("Are you sure you want to delete this supplier?");                	
            	}else {
            		// Set a larger font size for the content text
                	confirmationAlert.getDialogPane().setContentText("Are you sure you want to delete this supplier?\nThis action will delete supplied items as well.");                	
            	}
            	
            	// Set a custom size for the dialog pane
            	confirmationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            	ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            	ButtonType cancelButtonInAlert = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            	confirmationAlert.getButtonTypes().setAll(okButton, cancelButtonInAlert);

            	if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == okButton) {
                // Show confirmation dialog
               
                    // User clicked OK, delete the selected row
                    int supplierId = getSupplierIdFromSelectedSupplier(selectedSupplier);
                    boolean check = supplierdb.deleteSupplier(supplierId);
                    if(check) {
                    	SupplierTable.getItems().remove(selectedSupplier);
                    }

                  
                }
            } else {
                // No row selected, show a warning
            	 Alert alert= new Alert(Alert.AlertType.WARNING, "Please select a supplier to delete.");
            	 // Set the icon image for the alert
             	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
             	alertStage.getIcons().add(new Image("fi.png"));

             	// Show the alert
             	alert.showAndWait();
            }
        });

      

        addSupplierButton.setOnAction(event -> {
            // Open the addhostel page
            openAddSupplierPage();
        });

     // Assuming this code is inside your SupplierView class
        viewItemButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedSupplier = SupplierTable.getSelectionModel().getSelectedItem();

            if (selectedSupplier != null) {
                // Call the modified method to open the view items page
                openViewItemsPage(selectedSupplier);
                
            } else {
                // No row selected, show a warning
            	 Alert alert=new Alert(Alert.AlertType.WARNING, "Please select a supplier to view items.");
            	 // Set the icon image for the alert
             	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
             	alertStage.getIcons().add(new Image("fi.png"));

             	// Show the alert
             	alert.showAndWait();
            }
        });
        
        viewItemSummaryButton.setOnAction(event -> {
            // Launch the SupplierTotalItemsPage
            SupplierTotalItemsPage supplierTotalItemsPage = new SupplierTotalItemsPage();
            supplierTotalItemsPage.start(new Stage());
        });

        return footButtons;
    }
    // Helper method to get SupplierId from selected supplier
    private int getSupplierIdFromSelectedSupplier(Object[] selectedSupplier) {
    	// Implement the logic to get SupplierId from selectedSupplier
        // For now, I assume that the SupplierId is present at index 0 of the selectedSupplier array
        return Integer.parseInt(selectedSupplier[0].toString());
    }

 // Modify this method in SupplierView class
    private void openViewItemsPage(Object[] selectedSupplier) {
    	// Modify this line to use SupplierId instead of BillNo
        int supplierId = getSupplierIdFromSelectedSupplier(selectedSupplier);

        
        // Pass the SupplierId to the viewSupplierItems constructor
        viewSupplierItems viewItems = new viewSupplierItems(this, supplierId, borderPane);
        
        borderPane.setCenter(null);
        borderPane.setBottom(null);
        borderPane.setTop(null);
        
        // Add the new components to the BorderPane
        // Add the new components to the BorderPane
        borderPane.setTop(viewItems.createTopNavigationBar());
        viewItems.setupSuppISearch();
        // Create a Label instance
        Label selectedCountLabel = new Label();
        borderPane.setCenter(viewItems.createSupplierItemsTable(selectedCountLabel));
        borderPane.setBottom(viewItems.createFooterButtons());
    }
    
    public void setupSuppliersSearch() {
        searchTextField.setPromptText("");
        // Clear and show search fields for suppliers
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");
        // Set searchComboBox items based on the context (Suppliers)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Supplier Name");
    }
    public void clearFields() {
   	 // Optionally, clear the prompt text
       searchComboBox.setPromptText("");
       searchComboBox.setValue(null); // Set to null to clear the selection
       searchTextField.clear();
   }
   public void showSearchFields(boolean show) {
       searchComboBox.setVisible(show);
       searchTextField.setVisible(show);
   }
}