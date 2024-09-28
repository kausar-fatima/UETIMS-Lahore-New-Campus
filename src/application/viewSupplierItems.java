package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.JOptionPane;

import DatabaseClasses.SupplierItemsDB;


public class viewSupplierItems{
	
    private TableView<Object[]> supplierItemsTable;
    private SupplierView supplierView;
    private SupplierItemsDB itemsdb;
    private int supplierId;
    private BorderPane borderPane;
    private static ComboBox<String> searchComboBox;
    private static TextField searchTextField;
    static boolean alertShown = false;
    public viewSupplierItems(SupplierView supplierView,int selectedSupplier,BorderPane borderPane) {
    	this.supplierId = selectedSupplier;
    	itemsdb = new SupplierItemsDB();
    	this.supplierView = supplierView;
    	this.borderPane = borderPane;
    	 searchComboBox = new ComboBox<>();
         searchTextField = new TextField();
	}
	
	
    @SuppressWarnings("unchecked")
    public VBox createSupplierItemsTable(Label selectedCountLabel) {
        TableView<Object[]> table = new TableView<>();
        table.setEditable(true);
        
        // Set selection mode to allow multiple selections
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Columns
        TableColumn<Object[], String> noColumn = new TableColumn<>("no");
        noColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));
        
        TableColumn<Object[], String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        nameColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));
        categoryColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        priceColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> supplierIdColumn = new TableColumn<>("Supplier Id");
        supplierIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 4)));
        supplierIdColumn.setVisible(false); // Make the SupplierId column non-visible
        
        TableColumn<Object[], String> itemStatusColumn = new TableColumn<>("Item Status");
        itemStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 5)));
        itemStatusColumn.setPrefWidth(170);
        
        // Add all columns to the table
        table.getColumns().addAll(noColumn, nameColumn, categoryColumn, priceColumn, supplierIdColumn, itemStatusColumn);
        
        ObservableList<Object[]> hostelData = FXCollections.observableArrayList(itemsdb.supplierItemsInfo(supplierId));
        table.getItems().setAll(hostelData);
        
        // Set the preferred height of the TableView
        table.setPrefHeight(640); // Adjust the height as needed
        
        VBox root = new VBox();
        root.getChildren().addAll(table, selectedCountLabel);

        // Add a listener to update the selected count label
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Object[]>) c -> {
            int selectedCount = table.getSelectionModel().getSelectedItems().size();
            if (selectedCount > 1) {
                selectedCountLabel.setText("Selected count: " + selectedCount);
                selectedCountLabel.setVisible(true);
            } else {
                selectedCountLabel.setVisible(false);
            }
        });

        // Assign the created TableView to the class variable
        this.supplierItemsTable = table;

        return root;
    }

    private static <T> String getProperty(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index].toString();
        } else {
            return ""; // or handle this case in a way that makes sense for your application
        }
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
                if ("Supplier Item Name".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Item Name");
                } else if ("Supplier Item Category".equals(selectedSearchType)) {
                    searchTextField.setPromptText("Enter Item Category");
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
              
              if ("Supplier Item Name".equals(searchType)) {
            	  searchInventoryByItemName(newValue); // Call the method in viewhostel to update the table
              } else if ("Supplier Item Category".equals(searchType)) {
            	  searchInventoryByItemCategory(newValue); // Call the method in viewhostel to update the table
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
    public void searchInventoryByItemName(String itemName) {
        // Implement logic to filter the table by itemName
        String trimmedItemName = itemName.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(itemsdb.searchInventoryByItemName(trimmedItemName));
        supplierItemsTable.getItems().setAll(filteredData);
    }

    public void searchInventoryByItemCategory(String itemCategory) {
        // Implement logic to filter the table by itemCategory
        String trimmedItemCategory = itemCategory.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(itemsdb.searchInventoryByCategory(trimmedItemCategory));
        supplierItemsTable.getItems().setAll(filteredData);
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
    private void openAddItemPage() {
    	AddSupplierItem addItem = new AddSupplierItem(this::refreshItemTable,supplierId);
        Stage addItemStage = new Stage();
        addItem.start(addItemStage);

        // Add an event handler to refresh the table when the addroom stage is closed
        addItemStage.setOnCloseRequest(event -> refreshItemTable());
    }
    
    public void refreshItemTable() {
    	
    	supplierItemsTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> roomData = FXCollections.observableArrayList(itemsdb.supplierItemsInfo(supplierId));
        supplierItemsTable.getItems().setAll(roomData);
    }
    
    private void openEditItemPage(ObservableList<Object[]> selectedItems) {
        EditSupplierItems edititem = new EditSupplierItems(selectedItems, supplierId,this::refreshItemTable,itemsdb);
        Stage editItemStage = new Stage();
        edititem.start(editItemStage);

        // Add an event handler to refresh the table when the editroom stage is closed
        editItemStage.setOnCloseRequest(event -> refreshItemTable());
    }
    
    public HBox createFooterButtons() {
        Button edititemButton = new Button("Edit Item");
        Button deleteitemButton = new Button("Delete Item");
        Button viewSuppliersButton = new Button("View Suppliers");
        Button additemButton = new Button("Add Item");

        HBox footerButtons = new HBox(10, edititemButton, deleteitemButton, viewSuppliersButton, additemButton); // Include the button in the HBox
        footerButtons.setAlignment(Pos.CENTER_RIGHT);
        footerButtons.setPadding(new Insets(10, 10, 10, 0));
        
        // Event handler for Edit room button
        edititemButton.setOnAction(event -> {
        	// Get the selected items
	        ObservableList<Object[]> selectedItems = supplierItemsTable.getSelectionModel().getSelectedItems();

            if (!selectedItems.isEmpty()) {
            	boolean check = false;
            	if(selectedItems.size()==1)
            	{
            		if(selectedItems.get(0)[5]=="Unallocated") {
            			// Open the editroom page with selected data
                		openEditItemPage(selectedItems);
            		}else {
            			JOptionPane.showMessageDialog(null, "Supplier Item update failed. Item does not exist in inventory. It might have been allocated");
            		}
            	}else {
            		// Open the editroom page with selected data
            		openEditItemPage(selectedItems);
            	} 
            } else {
                // No row selected, show a warning
                Alert alert=new Alert(Alert.AlertType.WARNING, "Please select an item to edit.");
                // Set the icon image for the alert
               	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
               	alertStage.getIcons().add(new Image("fi.png"));

               	// Show the alert
               	alert.showAndWait();
            }
        });
        // Event handler for Delete room button
        deleteitemButton.setOnAction(event -> {
        	// Get the selected items
	        ObservableList<Object[]> selectedItems = supplierItemsTable.getSelectionModel().getSelectedItems();
           
            if (!selectedItems.isEmpty()) {
            	// Create an image for the confirmation dialog
            	Image iconImage = new Image("fi.png");

            	// Create a confirmation alert with a custom alert type and set the icon image
            	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the selected items?", ButtonType.OK, ButtonType.CANCEL);
            	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(iconImage);
                // Show confirmation dialog
                if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    // User clicked OK, delete the selected row
                	
                	itemsdb.deleteSupplierItem(selectedItems);
                	//supplierItemsTable.getItems().remove(selectedRoom);
                    
                    // Add your logic for deleting the room from the database or any other data source
                    // For now, I'll print a message
                	refreshItemTable();
                   // System.out.println("Item deleted!");
                }
            } else {
                // No row selected, show a warning
                Alert alert=new Alert(Alert.AlertType.WARNING, "Please select an item to delete.");
                // Set the icon image for the alert
               	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
               	alertStage.getIcons().add(new Image("fi.png"));

               	// Show the alert
               	alert.showAndWait();
            }
        }
    );    
        viewSuppliersButton.setOnAction(event -> {
        	//refreshItemsContents(viewRooms);
    		borderPane.setCenter(null);
            borderPane.setBottom(null);
            borderPane.setTop(null);
            
            // Add the new components to the BorderPane
            // Add the new components to the BorderPane
            borderPane.setTop(supplierView.createTopNavigationBar());
            supplierView.setupSuppliersSearch();
            // Add the new components to the BorderPane
            borderPane.setCenter(supplierView.createSupplierTable());
            borderPane.setBottom(supplierView.createFooterButtons());
        });

        additemButton.setOnAction(event -> {
            // Open the addroom page
            openAddItemPage();
        });

        return footerButtons;
    }
    public void setupSuppISearch() {
        searchTextField.setPromptText("");
        // Clear and show search fields for inventory
        clearFields();
        showSearchFields(true);
        searchComboBox.setPromptText("Select search type");

        // Set searchComboBox items based on the context (Inventory)
        searchComboBox.getItems().clear();
        searchComboBox.setPromptText("Select search type");
        searchComboBox.getItems().addAll("Supplier Item Name", "Supplier Item Category");
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