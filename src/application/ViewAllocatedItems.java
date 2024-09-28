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

import java.util.Optional;

import DatabaseClasses.allocatesdItemsDB;

public class ViewAllocatedItems{
	
    private TableView<Object[]> itemTable;
    private allocatesdItemsDB itemdb;
    private String hostelname;
    private int roomId;
    private BorderPane borderPane;
    private imanViewRooms viewRooms;
    private static ComboBox<String> searchComboBox;
    private static TextField searchTextField;
    static boolean alertShown = false;
    
    public ViewAllocatedItems(imanViewRooms viewRooms, String selectedHostel, int roomId, BorderPane borderPane) {
    	this.hostelname = selectedHostel;
    	this.viewRooms = viewRooms;
		this.roomId = roomId;
		this.borderPane = borderPane;
		itemdb = new allocatesdItemsDB();
		 searchComboBox = new ComboBox<>();
         searchTextField = new TextField();
	}
    


    @SuppressWarnings("unchecked")
    public VBox createItemsTable(Label selectedCountLabel) {
        TableView<Object[]> table = new TableView<>();
        table.setEditable(true);

        // Set selection mode to allow multiple selections
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Columns
        TableColumn<Object[], String> IdColumn = new TableColumn<>("Item Id");
        IdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));
        IdColumn.setVisible(false); // Make the Id column non-visible
        
        TableColumn<Object[], String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        itemNameColumn.setPrefWidth(170);
        
        TableColumn<Object[], String> itemCategoryColumn = new TableColumn<>("Item Category");
        itemCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 2)));
        itemCategoryColumn.setPrefWidth(170);

        TableColumn<Object[], String> noColumn = new TableColumn<>("Requisition no");
        noColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 3)));
        itemNameColumn.setPrefWidth(170);
        // Add all columns to the table
        table.getColumns().addAll(IdColumn, itemNameColumn, itemCategoryColumn, noColumn);

        ObservableList<Object[]> itemData = FXCollections.observableArrayList(itemdb.GetAllocatedItem(hostelname, roomId));
        table.getItems().setAll(itemData);

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
        this.itemTable = table;

        return root;
    }

	private static <T> String getProperty(T[] array, int index) {
	    if (index >= 0 && index < array.length && array[index] != null) {
	        return array[index].toString();
	    } else {
	        return "";
	    }
	}

	 public HBox createTopNavigationBar()  {
	        HBox topNavigationBar = new HBox(15);
	       
	        
	        topNavigationBar.setStyle("-fx-background-color: linear-gradient(to bottom right, #001F3F, #004080); -fx-padding: 20;");

	        ImageView logoImageView = new ImageView(new String("uet.png"));
	        logoImageView.setFitHeight(80);
	        logoImageView.setPreserveRatio(true);

	        // Set the logoImageView to the left corner
	        //HBox.setMargin(logoImageView, new Insets(0, 10, 0, 10)); // Adjust margins as needed

	        searchTextField = new TextField();
	        searchTextField.setMinWidth(170);
	        searchTextField.setMaxWidth(170);
	        searchTextField.setPrefWidth(170);
	        searchTextField.setVisible(false);

	        // Add event listener to searchComboBox
	        searchComboBox.setVisible(false);
	        
	        searchComboBox.setOnAction(event -> {
	            if (searchTextField == null) {
	                //System.out.println("searchTextField is null!");
	            } else {
	                String selectedSearchType = searchComboBox.getValue();
	                if ("Allocated Item Name".equals(selectedSearchType)) {
	                    searchTextField.setPromptText("Enter Item Name");
	                } else if ("Allocated Item Category".equals(selectedSearchType)) {
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
	              
	              if ("Allocated Item Name".equals(searchType)) {
	            	  searchaByItemName(newValue); // Call the method in viewhostel to update the table
	              } else if ("Allocated Item Category".equals(searchType)) {
	            	  searchaByItemCategory(newValue); // Call the method in viewhostel to update the table
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
	    public void searchaByItemName(String itemName) {
		    // Implement logic to filter the table by itemName
		    String trimmedItemName = itemName.trim();
		    ObservableList<Object[]> filteredData = FXCollections.observableArrayList(itemdb.searchaByItemName(trimmedItemName));
		    itemTable.getItems().setAll(filteredData);
		}

		public void searchaByItemCategory(String itemCategory) {
		    // Implement logic to filter the table by itemCategory
		    String trimmedItemCategory = itemCategory.trim();
		    ObservableList<Object[]> filteredData = FXCollections.observableArrayList(itemdb.searchaByCategory(trimmedItemCategory));
		    itemTable.getItems().setAll(filteredData);
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
        public void refreshItemTable() {
    	
    	itemTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> itemData = FXCollections.observableArrayList(itemdb.GetAllocatedItem(hostelname, roomId));
        itemTable.getItems().setAll(itemData);
        }
    
    public HBox createFooterButtons() {
    	Button edititemButton = new Button("Edit Items");
    	Button delitemButton = new Button("Delete Items");
    	Button viewroomsButton = new Button("View Rooms");
    	Button additemButton = new Button("Add Items");
       
      
        HBox footerButtons = new HBox(10,edititemButton,delitemButton,viewroomsButton,additemButton);
        footerButtons.setAlignment(Pos.CENTER_RIGHT);
        footerButtons.setPadding(new Insets(10, 10, 10, 0)); // Padding for top, right, and bottom
        
   	 // Event handler for Edit room button
	    edititemButton.setOnAction(event -> {
	        // Get the selected items
	    	ObservableList<Object[]> selectedItems = itemTable.getSelectionModel().getSelectedItems();

	        if (!selectedItems.isEmpty()) {
	            if(selectedItems.size()==1) {
	            	// Check if ReqNo is null
		            Integer reqNo = getReqNoFromSelectedItem(selectedItems.get(0));
		            if (reqNo != null&& reqNo!=0) {
		                // ReqNo is not null, show warning and prevent editing
		            	showAlert("Error", "This item is allocated through Requisition Number "+reqNo+" and cannot be edited here.");
		            } else {
		                // ReqNo is null, proceed with editing
		                openEditItemPage(selectedItems);
		            }
	            }else {
	                // ReqNo is null, proceed with editing
	                openEditItemPage(selectedItems);
	            }
	        } else {
	            // No row selected, show a warning
	        	 Alert alert= new Alert(Alert.AlertType.WARNING, "Please select an Item to edit.");
	        	 // Set the icon image for the alert
	            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
	            	alertStage.getIcons().add(new Image("fi.png"));

	            	// Show the alert
	            	alert.showAndWait();
	        }
	    });

	    // Event handler for Delete Items button
	    delitemButton.setOnAction(event -> {
	        // Get the selected items
	    	ObservableList<Object[]> selectedItems = itemTable.getSelectionModel().getSelectedItems();

	        if (!selectedItems.isEmpty()) {
	            
	            if(selectedItems.size() ==1) {
	            	// Check if ReqNo is null
		            Integer reqNo = getReqNoFromSelectedItem(selectedItems.get(0));
	            	if (reqNo != null && reqNo!=0) {
		                // ReqNo is not null, show warning and prevent deletion
		            	showAlert("Error", "This item is allocated through Request Number "+reqNo+" and cannot be deleted.");
		            }else {
		                // ReqNo is null, proceed with deletion
		                // Prompt the user for confirmation
		                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		                alert.setTitle("Confirmation Dialog");
		                alert.setHeaderText("Delete Item");
		                // Set the icon image for the alert
		            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		            	alertStage.getIcons().add(new Image("fi.png"));

		                alert.setContentText("Are you sure you want to delete the selected item?");

		                Optional<ButtonType> result = alert.showAndWait();
		                if (result.isPresent() && result.get() == ButtonType.OK) {
		                    // User confirmed, proceed with deletion
		                    itemdb.deleteAllocatedItem(null, hostelname, roomId, selectedItems);
		                    refreshItemTable(); // Refresh the table to reflect the changes
		                }
		            }
	            } else {
	                // ReqNo is null, proceed with deletion
	                // Prompt the user for confirmation
	                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	                alert.setTitle("Confirmation Dialog");
	                alert.setHeaderText("Delete Items");
	                // Set the icon image for the alert
	            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
	            	alertStage.getIcons().add(new Image("fi.png"));

	                alert.setContentText("Are you sure you want to delete the selected items?");

	                Optional<ButtonType> result = alert.showAndWait();
	                if (result.isPresent() && result.get() == ButtonType.OK) {
	                    // User confirmed, proceed with deletion
	                    
		                itemdb.deleteAllocatedItem(null, hostelname, roomId, selectedItems);
	                   
	                    refreshItemTable(); // Refresh the table to reflect the changes
	                }
	            }
	        } else {
	            // No row selected, show a warning
	            Alert alert=new Alert(Alert.AlertType.WARNING, "Please select an Item to delete.");
	            // Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
	        }
	    });
        // Event handler for Add Item button
        additemButton.setOnAction(event -> openAddItemPage());
        viewroomsButton.setOnAction(event -> {
        	// Back to hostels page

        		//refreshItemsContents(viewRooms);
        		borderPane.setCenter(null);
                borderPane.setBottom(null);
                borderPane.setTop(null);
                
                // Add the new components to the BorderPane
                // Add the new components to the BorderPane
                borderPane.setTop(viewRooms.createTopNavigationBar());
                viewRooms.setupRoomSearch();
                // Add the new components to the BorderPane
                borderPane.setCenter(viewRooms.createRoomTable());
                borderPane.setBottom(viewRooms.createFooterButtons());
        	
        });
        

        return footerButtons;
    }
    private void openAddItemPage() {
        // Implement the logic to open the AddItem page
        AddAllocatedItem addInventory = new AddAllocatedItem(hostelname,this::refreshItemTable,roomId);
        Stage addInventoryStage = new Stage();
        addInventory.start(addInventoryStage);            

        // Add an event handler to refresh the table when the add inventory stage is closed
        addInventoryStage.setOnCloseRequest(event -> refreshItemTable());
    }
    
   private void openEditItemPage(ObservableList<Object[]> selectedItems) {
       EditAllocatedItem edititem = new EditAllocatedItem(hostelname, selectedItems, this::refreshItemTable, itemdb,roomId);
        Stage editItemStage = new Stage();
        edititem.start(editItemStage);

       // Add an event handler to refresh the table when the editItem stage is closed
       editItemStage.setOnCloseRequest(event -> refreshItemTable());
   }
   
	public void setRoomTable(TableView<Object[]> itemTable) {
		// TODO Auto-generated method stub
		this.itemTable = itemTable;
	}
	
	 public void setupaSearch() {
	        searchTextField.setPromptText("");
	        // Clear and show search fields for suppliers
	        clearFields();
	        showSearchFields(true);
	        searchComboBox.setPromptText("Select search type");

	        // Set searchComboBox items based on the context (Suppliers)
	        searchComboBox.getItems().clear();
	        searchComboBox.setPromptText("Select search type");
	        searchComboBox.getItems().addAll("Allocated Item Name","Allocated Item Category");
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


	    // Helper method to get ReqNo from selected item
	    private Integer getReqNoFromSelectedItem(Object[] selectedItem) {
	        // Modify this method based on your data model
	        // Assuming ReqNo is in the first column of the selected item
	        Object reqNoObject = selectedItem[3];
	        if (reqNoObject instanceof Integer) {
	            return (Integer) reqNoObject;
	        }
	        return null;
	    }

}