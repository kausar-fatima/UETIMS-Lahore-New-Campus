package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import DatabaseClasses.InventoryItemsDB;

public class InventoryView  {
    private TableView<Object[]> InventoryTable;
    private InventoryItemsDB inventorydb;
    public InventoryView(BorderPane borderPane) {
    	inventorydb = new InventoryItemsDB();
    	
    }

    @SuppressWarnings("unchecked")
    public VBox createInventoryTable(Label selectedCountLabel) {
        TableView<Object[]> table = new TableView<>();
        table.setEditable(true);
        
        // Set selection mode to allow multiple selections
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Columns
        TableColumn<Object[], String> ItemNameColumn = new TableColumn<>("Item Name");
        ItemNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        ItemNameColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> ItemCategoryColumn = new TableColumn<>("Item Category");
        ItemCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));
        ItemCategoryColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> ItemStatusColumn = new TableColumn<>("Item Reference");
        ItemStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        ItemStatusColumn.setPrefWidth(170);
        
        // Add all columns to the table
        table.getColumns().addAll(ItemNameColumn, ItemCategoryColumn, ItemStatusColumn);

        ObservableList<Object[]> InventoryData = FXCollections.observableArrayList(inventorydb.getInventoryItems());
        table.getItems().setAll(InventoryData);

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
        this.InventoryTable = table;

        return root;
    }

    private static <T> String getProperty(T[] array, int index) {
        return array[index].toString();
    }

    public void searchByItemName(String itemName) {
        // Implement logic to filter the table by itemName
        String trimmedItemName = itemName.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(inventorydb.searchInventoryByItemName(trimmedItemName));
        InventoryTable.getItems().setAll(filteredData);
    }

    public void searchByItemCategory(String itemCategory) {
        // Implement logic to filter the table by itemCategory
        String trimmedItemCategory = itemCategory.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(inventorydb.searchInventoryByCategory(trimmedItemCategory));
        InventoryTable.getItems().setAll(filteredData);
    }

    public HBox createFooterButtons() {
    	  
    	    Button addItemButton = new Button("Add Item");
    	    Button deleteItemButton = new Button("Delete Item");
    	    Button editItemButton = new Button("Edit Item");
    	    
    	    Button totalItemsButton = new Button("Total Items: " + getTotalItems());

    	    HBox footerButtons = new HBox(10, editItemButton , deleteItemButton,addItemButton,totalItemsButton);
    	    footerButtons.setAlignment(Pos.CENTER_RIGHT);
    	    footerButtons.setPadding(new Insets(10, 10, 10, 0));

    	 // Event handler for Edit Item button
    	    editItemButton.setOnAction(event -> {
    	        // Get the selected items
    	        ObservableList<Object[]> selectedItems = InventoryTable.getSelectionModel().getSelectedItems();

    	        if (!selectedItems.isEmpty()) {
    	            // Open the edit item page with selected data
    	            try {
    	                    openEditItemPage(selectedItems);   
    	                    totalItemsButton.setText("Total Items: " + getTotalItems());

    	            } catch (SQLException e) {
    	                e.printStackTrace();
    	            }
    	        } else {
    	            // No row selected, show a warning
    	        	 Alert alert= new Alert(Alert.AlertType.WARNING, "Please select items to edit.");
    	         // Set the icon image for the alert
                	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                	alertStage.getIcons().add(new Image("fi.png"));

                	// Show the alert
                	alert.showAndWait();
    	        }
    	    });


    	    // Event handler for Add Item button
    	    addItemButton.setOnAction(event -> {
    	    	openAddItemPage();
    	    	totalItemsButton.setText("Total Items: " + getTotalItems());

    	    });

    	    // Event handler for Delete Item button
    	    deleteItemButton.setOnAction(event -> {
    	        // Get the selected items
    	        ObservableList<Object[]> selectedItems = InventoryTable.getSelectionModel().getSelectedItems();
    	     // Create an image for the confirmation dialog
            	Image iconImage = new Image("fi.png");

            	// Create a confirmation alert with a custom alert type and set the icon image
            	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the selected items?", ButtonType.OK, ButtonType.CANCEL);
            	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(iconImage);
    	        if (!selectedItems.isEmpty()) {
    	            if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
    	                // Iterate through selected items and delete each one
    	            	inventorydb.deleteInventoryItem(selectedItems);
    	            	
    	                refreshInventoryTable(); // Refresh the table after deletion
    	                totalItemsButton.setText("Total Items: " + getTotalItems());
    	            }
    	        } else {
    	            // No row selected, show a warning
    	            Alert alert=new Alert(Alert.AlertType.WARNING, "Please select items to delete.");
    	         // Set the icon image for the alert
                	Stage alertStage1 = (Stage) alert.getDialogPane().getScene().getWindow();
                	alertStage1.getIcons().add(new Image("fi.png"));

                	// Show the alert
                	alert.showAndWait();
    	        }
    	    });
    	    totalItemsButton.setOnAction(event -> {
    	        TotalItemsPage totalItemsPage = new TotalItemsPage();
    	        Stage totalItemsStage = new Stage();
    	        totalItemsPage.start(totalItemsStage);
    	    });

    	    return footerButtons;
    	}

    
    private int getTotalItems() {
        return InventoryTable.getItems().size();
    }
    
    
    private void openAddItemPage() {
        // Implement the logic to open the AddItem page
        AddInventoryItem addInventory = new AddInventoryItem(this::refreshInventoryTable);
        Stage addInventoryStage = new Stage();
        addInventory.start(addInventoryStage);

        // Add an event handler to refresh the table when the add inventory stage is closed
        addInventoryStage.setOnCloseRequest(event -> {
        	refreshInventoryTable();
    		getTotalItems();
        });
        
    }

    public void refreshInventoryTable() {
    	
	    InventoryTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> supplierData = FXCollections.observableArrayList(inventorydb.getInventoryItems());
        InventoryTable.getItems().setAll(supplierData);
        
    }
   

private void openEditItemPage(ObservableList<Object[]> selectedItems) throws SQLException {
    EditInventoryItem edititem = new EditInventoryItem(selectedItems, this::refreshInventoryTable, inventorydb);
    Stage editItemStage = new Stage();
    edititem.start(editItemStage);

    // Add an event handler to refresh the table when the editItem stage is closed
    editItemStage.setOnCloseRequest(event -> refreshInventoryTable());
}

    
}