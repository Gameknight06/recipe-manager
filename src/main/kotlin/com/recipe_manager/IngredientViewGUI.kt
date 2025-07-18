package com.recipe_manager

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle

class IngredientViewGUI {

    @FXML private lateinit var rootBox: VBox
    @FXML private lateinit var ingredientsTable: TableView<Ingredient>
    @FXML private lateinit var nameColumn: TableColumn<Ingredient, String>
    @FXML private lateinit var locationColumn: TableColumn<Ingredient, String>
    @FXML private lateinit var costColumn: TableColumn<Ingredient, Double>
    @FXML private lateinit var unitColumn: TableColumn<Ingredient, String>
    @FXML private lateinit var defaultAmountColumn: TableColumn<Ingredient, Double>

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private fun initialize() {

        nameColumn.cellValueFactory = PropertyValueFactory("name")
        locationColumn.cellValueFactory = PropertyValueFactory("location")
        costColumn.cellValueFactory = PropertyValueFactory("cost")
        unitColumn.cellValueFactory = PropertyValueFactory("unitType")
        defaultAmountColumn.cellValueFactory = PropertyValueFactory("defaultAmount")

        loadAndDisplayIngredients()
    }

    /**
     * Loads ingredients from the file system and populates the TableView.
     */
    private fun loadAndDisplayIngredients() {
        val ingredients = FileOperations.loadIngredients()
        ingredientsTable.items = FXCollections.observableArrayList(ingredients.values.toList())
    }


    /**
     * Displays a dialog for editing an ingredient's details. The dialog is pre-populated
     * with the current details of the specified ingredient, allowing the user to make changes.
     * Once the dialog is confirmed and the changes are valid, the updated ingredient replaces
     * the original in the stored collection.
     */
    private fun showEditIngredientDialog(ingredient: Ingredient) {
            try {
                val originalIngredientName = ingredient.name
                val fxmlLoader = FXMLLoader(javaClass.getResource("EditIngredient.fxml"))
                val dialogPane = fxmlLoader.load<DialogPane>()
                val editIngredientController = fxmlLoader.getController<EditIngredientGUI>()

                editIngredientController.setIngredientDetails(ingredient)

                val dialog = Dialog<Ingredient>()
                createDialogWindow(rootBox, dialogPane, dialog, "Edit Ingredient")

                val saveButton = dialog.dialogPane.lookupButton(editIngredientController.saveButtonType) as Button

                saveButton.addEventFilter(ActionEvent.ACTION) { event ->
                    if (editIngredientController.updateIngredientFromFields() == null) {
                        event.consume()
                    }
                }

                dialog.setResultConverter { buttonType ->
                    if (buttonType == editIngredientController.saveButtonType) {
                        editIngredientController.updateIngredientFromFields()
                        ingredient
                    } else {
                        null
                    }
                }

                dialog.showAndWait().ifPresent { updatedIngredient ->
                    val allIngredients = FileOperations.loadIngredients()
                    allIngredients.remove(originalIngredientName)
                    allIngredients[updatedIngredient.name] = updatedIngredient
                    FileOperations.saveIngredients(allIngredients)

                    println("Ingredient updated: ${updatedIngredient.name}")
                    loadAndDisplayIngredients()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    @FXML
    private fun handleDeleteIngredientClick() {
        val selectedIngredient = ingredientsTable.selectionModel.selectedItem
        if (selectedIngredient == null) {
            return
        }
        FileOperations.deleteIngredient(selectedIngredient)
        loadAndDisplayIngredients()
    }

    @FXML
    private fun handleEditIngredientClick() {
        val selectedIngredient = ingredientsTable.selectionModel.selectedItem
        if (selectedIngredient == null) {
            return
        }
        showEditIngredientDialog(selectedIngredient)
    }

}