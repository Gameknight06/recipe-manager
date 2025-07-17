package com.recipe_manager

import atlantafx.base.controls.Notification
import atlantafx.base.util.DoubleStringConverter
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import javafx.util.Callback
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL
import java.io.File

class EditRecipeGUI {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var timeToCookField: TextField
    @FXML private lateinit var descriptionArea: TextArea
    @FXML private lateinit var instructionArea: TextArea
    @FXML lateinit var saveButtonType: ButtonType
    @FXML lateinit var error: Notification
    @FXML private lateinit var selectImageButton: Button
    @FXML private lateinit var imagePreview: ImageView
    @FXML private lateinit var ingredientsTableView: TableView<RecipeIngredient>
    @FXML private lateinit var nameColumn: TableColumn<RecipeIngredient, String>
    @FXML private lateinit var amountColumn: TableColumn<RecipeIngredient, Double>
    @FXML private lateinit var unitColumn: TableColumn<RecipeIngredient, String>

    private var selectedImagePath: String? = null
    private lateinit var currentRecipe: Recipe
    private val recipeIngredients = mutableMapOf<String, RecipeIngredient>()

    fun setRecipeDetails(recipe: Recipe) {
        currentRecipe = recipe
        nameField.text = recipe.name
        timeToCookField.text = recipe.timeToCook
        descriptionArea.text = recipe.description
        instructionArea.text = recipe.instructions

        recipe.imagePath?.let { path ->
            selectedImagePath = path
            try {
                imagePreview.image = Image(path)
            } catch (e: Exception) {
                println("Error loading image: $path")
                imagePreview.image = null
            }
        }

        ingredientsTableView.items = FXCollections.observableArrayList(currentRecipe.ingredients.values)
    }

    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }

        setupIngredientsTable()
    }

    private fun setupIngredientsTable() {
        ingredientsTableView.isEditable = true

        nameColumn.cellValueFactory = Callback { cellData ->
            SimpleStringProperty(cellData.value.ingredient.name)
        }

        amountColumn.cellValueFactory = PropertyValueFactory("amount")
        amountColumn.cellFactory = TextFieldTableCell.forTableColumn(DoubleStringConverter())
        amountColumn.setOnEditCommit { event ->
            val recipeIngredient = event.rowValue
            recipeIngredient.amount = event.newValue
        }

        unitColumn.cellValueFactory = PropertyValueFactory("unit")
        unitColumn.cellFactory = ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(UnitConverter.allUnits))
        unitColumn.setOnEditCommit { event ->
            val recipeIngredient = event.rowValue
            recipeIngredient.unit = event.newValue
        }
    }

    @FXML
    private fun handleRemoveIngredient() {
        val selectedIngredient = ingredientsTableView.selectionModel.selectedItem ?: return

        currentRecipe .ingredients.remove(selectedIngredient.ingredient.name)
        ingredientsTableView.items.remove(selectedIngredient)
    }

    @FXML
    private fun handleAddIngredient() {
        try {
            val fxmlLoader = FXMLLoader(javaClass.getResource("AddIngredientToRecipe.fxml"))
            val dialogPane: DialogPane = fxmlLoader.load()
            val controller = fxmlLoader.getController<AddIngredientToRecipeGUI>()

            val dialog = Dialog<RecipeIngredient>()
            dialog.dialogPane = dialogPane
            dialog.title = "Add Ingredient"
            dialog.initOwner(ingredientsTableView.scene.window)
            dialog.initStyle(StageStyle.UNDECORATED)


            val addButton = dialog.dialogPane.lookupButton(controller.addButtonType)
            addButton.addEventFilter(ActionEvent.ACTION) { e ->
                if (controller.validateAndGetResult() == null) {
                    e.consume()
                }
            }

            dialog.setResultConverter { buttonType ->
                if (buttonType == controller.addButtonType) {
                    controller.validateAndGetResult()
                } else null
            }

            dialog.showAndWait().ifPresent { newRecipeIngredient ->
                if (currentRecipe.ingredients.containsKey(newRecipeIngredient.ingredient.name)) {
                    Alert(
                        Alert.AlertType.WARNING,
                        "'${newRecipeIngredient.ingredient.name}' is already in this recipe. You can edit its amount or unit directly in the table."
                    ).showAndWait()
                } else {
                    currentRecipe.ingredients[newRecipeIngredient.ingredient.name] = newRecipeIngredient
                    ingredientsTableView.items.add(newRecipeIngredient)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Alert(Alert.AlertType.ERROR, "Could not open the Add Ingredient dialog: ${e.message}").showAndWait()
        }
    }

    @FXML
    private fun handleSelectImageClick() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select Recipe Image"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        )
        val stage = nameField.scene.window
        val selectedFile: File? = fileChooser.showOpenDialog(stage)

        selectedFile?.let {
            selectedImagePath = it.toURI().toString()
            imagePreview.image = Image(selectedImagePath)
        }
    }

    fun updateRecipeFromFields() {
        if (nameField.text.isBlank() || timeToCookField.text.isBlank() || descriptionArea.text.isBlank() || instructionArea.text.isBlank() || recipeIngredients.isEmpty()) {
            showError("Please fill out all recipe fields!")
            return
        }
        currentRecipe.name = nameField.text
        currentRecipe.timeToCook = timeToCookField.text
        currentRecipe.description = descriptionArea.text
        currentRecipe.instructions = instructionArea.text
        currentRecipe.imagePath = selectedImagePath
        currentRecipe.ingredients.clear()
        currentRecipe.ingredients.putAll(recipeIngredients)

    }

    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}