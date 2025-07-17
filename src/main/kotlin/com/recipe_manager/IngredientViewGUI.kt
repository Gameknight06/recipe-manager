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

    @FXML
    private fun initialize() {

        nameColumn.cellValueFactory = PropertyValueFactory("name")
        locationColumn.cellValueFactory = PropertyValueFactory("location")
        costColumn.cellValueFactory = PropertyValueFactory("cost")
        unitColumn.cellValueFactory = PropertyValueFactory("unitType")
        defaultAmountColumn.cellValueFactory = PropertyValueFactory("defaultAmount")

        loadAndDisplayIngredients()
    }

    private fun loadAndDisplayIngredients() {
        val ingredients = FileOperations.loadIngredients()
        ingredientsTable.items = FXCollections.observableArrayList(ingredients.values.toList())
    }


    private fun showEditIngredientDialog( ingredient: Ingredient) {
            try {
                val originalIngredientName = ingredient.name

                val fxmlLoader = FXMLLoader(javaClass.getResource("EditIngredient.fxml"))
                val dialogPane = fxmlLoader.load<DialogPane>()
                val editIngredientController = fxmlLoader.getController<EditIngredientGUI>()

                editIngredientController.setIngredientDetails(ingredient)

                val dialog = Dialog<Ingredient>()
                dialog.dialogPane = dialogPane
                dialog.title = "Edit Ingredient"
                dialog.initStyle(StageStyle.UNDECORATED)

                val stage = rootBox.scene.window as Stage
                dialog.initOwner(stage)
                dialog.width = stage.widthProperty().value * 0.85
                dialog.height = stage.heightProperty().value * 0.65
                dialog.x = stage.x + (stage.width - dialog.width) / 2
                dialog.y = stage.y + (stage.height - dialog.height) / 2

                val saveButton = dialog.dialogPane.lookupButton(editIngredientController.saveButtonType) as Button

                saveButton.addEventFilter(ActionEvent.ACTION) { event ->
                    val newIngredient = editIngredientController.updateIngredientFromFields()
                    if (newIngredient == null) {
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