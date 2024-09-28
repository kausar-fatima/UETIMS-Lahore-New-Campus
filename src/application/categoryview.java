package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import DatabaseClasses.CategoryDB;

public class categoryview{
    private TableView<Object[]> CategoryTable;
    
    private CategoryDB categorydb;
    public categoryview(BorderPane borderPane) {
    	
    	categorydb = new CategoryDB();
 
    }

    @SuppressWarnings("unchecked")
    public TableView<Object[]> createCategoryTable() {
        TableView<Object[]> table = new TableView<>();
        table.setEditable(true);
        
        
        // Columns
        TableColumn<Object[], String> supplierIdColumn = new TableColumn<>("Category Id");
        supplierIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 0)));
        supplierIdColumn.setVisible(false); // Make the SupplierId column non-visible
        // Columns
        TableColumn<Object[], String> cColumn = new TableColumn<>("Category Name");
        cColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getProperty(cellData.getValue(), 1)));
        cColumn.setPrefWidth(170);

       
       

        table.getColumns().addAll(supplierIdColumn,cColumn);

        ObservableList<Object[]> supplierData = FXCollections.observableArrayList(categorydb.getItemCategoriesInfo());
        table.getItems().setAll(supplierData);
        CategoryTable = table;
        return table;
    }

    private static <T> String getProperty(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index].toString();
        } else {
            return ""; // or handle this case in a way that makes sense for your application
        }
    }
    
   
    private void openAddCategoryPage() {
    	AddCategory addcategory = new AddCategory(this::refreshSupplierTable);
        Stage addCategoryStage = new Stage();
        addcategory.start(addCategoryStage);

        // Add an event handler to refresh the table when the addhostel stage is closed
        addCategoryStage.setOnCloseRequest(event -> refreshSupplierTable());
    }
    
    public void refreshSupplierTable() {
    	
    	CategoryTable.getItems().clear(); // Clear existing items
        ObservableList<Object[]> supplierData = FXCollections.observableArrayList(categorydb.getItemCategoriesInfo());
        CategoryTable.getItems().setAll(supplierData);
    }
    
    private void openEditCategoryPage(Object[] selectedcategory) {
        EditCategory editCategory = new EditCategory(selectedcategory, this::refreshSupplierTable,categorydb);
        Stage editCategoryStage = new Stage();
        editCategory.start(editCategoryStage);

        // Add an event handler to refresh the table when the edithostel stage is closed
        editCategoryStage.setOnCloseRequest(event -> refreshSupplierTable());
    }

    public HBox createFooterButtons() {
        Button editSupplierButton = new Button("Edit Category");
        Button deleteSupplierButton =  new Button("Delete Category");
       
        Button addSupplierButton = new Button("Add Category");

        HBox footButtons = new HBox(10, editSupplierButton, deleteSupplierButton,addSupplierButton);
        footButtons.setAlignment(Pos.CENTER_RIGHT);
        footButtons.setPadding(new Insets(10, 10, 10, 0)); // Padding for top, right, and bottom

        
        // Event handler for Edit Hostel button
        editSupplierButton.setOnAction(event -> {
            // Get the selected item
            Object[] selectedentry = CategoryTable.getSelectionModel().getSelectedItem();

            if (selectedentry != null) {
                // Open the edithostel page with selected data
                openEditCategoryPage(selectedentry);
            } else {
                // No row selected, show a warning
            	// Create an alert with a custom alert type
            	Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a category to edit.");

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
            Object[] selectedSupplier = CategoryTable.getSelectionModel().getSelectedItem();

            if (selectedSupplier != null) {
                // Show confirmation dialog
            	// Create an image for the confirmation dialog
            	Image iconImage = new Image("fi.png");

            	// Create a confirmation alert with a custom alert type and set the icon image
            	Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this category?", ButtonType.OK, ButtonType.CANCEL);
            	Stage alertStage = (Stage) confirmationAlert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(iconImage);

            	// Show the confirmation alert and handle the user's response
            	if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            	    // User clicked OK, proceed with the deletion
            	    int CategoryId = getCategoryIdFromSelectedCategory(selectedSupplier);
            	    categorydb.deleteCategory(CategoryId);
            	    CategoryTable.getItems().remove(selectedSupplier);

            	   }

            } else {
                // No row selected, show a warning
            	Alert alert =  new Alert(Alert.AlertType.WARNING, "Please select a category to delete.");
            	// Set the icon image for the alert
            	Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            	alertStage.getIcons().add(new Image("fi.png"));

            	// Show the alert
            	alert.showAndWait();
            }
        });

      

        addSupplierButton.setOnAction(event -> {
            // Open the addhostel page
            openAddCategoryPage();
        });
        
        return footButtons;

    }
    public void searchByCategory(String itemCategory) {
        // Implement logic to filter the table by itemCategory
        String trimmedItemCategory = itemCategory.trim();
        ObservableList<Object[]> filteredData = FXCollections.observableArrayList(categorydb.searchByCategory(trimmedItemCategory));
        CategoryTable.getItems().setAll(filteredData);
    }

    // Helper method to get SupplierId from selected supplier
    private int getCategoryIdFromSelectedCategory(Object[] selectedSupplier) {
    	// Implement the logic to get SupplierId from selectedSupplier
        // For now, I assume that the SupplierId is present at index 0 of the selectedSupplier array
        return Integer.parseInt(selectedSupplier[0].toString());
    }

	

 
    
   
}