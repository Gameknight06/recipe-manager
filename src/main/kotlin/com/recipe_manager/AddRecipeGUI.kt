package com.recipe_manager

import atlantafx.base.controls.Notification
import atlantafx.base.util.DoubleStringConverter
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import javafx.util.Callback
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL
import java.io.File


class AddRecipeGUI {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var timeToCookField: TextField
    @FXML private lateinit var descriptionArea: TextArea
    @FXML private lateinit var instructionArea: TextArea
    @FXML private lateinit var ingredientsListView: ListView<DisplayIngredients>
    @FXML private lateinit var ingredientComboBox: ComboBox<String>
    @FXML private lateinit var amountField: TextField
    @FXML private lateinit var unitComboBox: ComboBox<String>
    @FXML lateinit var saveButtonType: ButtonType
    @FXML lateinit var error: Notification
    @FXML lateinit var selectImageButton: Button
    @FXML lateinit var imagePreview: ImageView
    @FXML private lateinit var ingredientsTableView: TableView<RecipeIngredient>
    @FXML private lateinit var nameColumn: TableColumn<RecipeIngredient, String>
    @FXML private lateinit var amountColumn: TableColumn<RecipeIngredient, Double>
    @FXML private lateinit var unitColumn: TableColumn<RecipeIngredient, String>

    private var selectedImagePath: String? = null
    private val allIngredients = FileOperations.loadIngredients()
    private val recipeIngredients = mutableMapOf<String, RecipeIngredient>()
    private val addIngredientsObservableList = FXCollections.observableArrayList<RecipeIngredient>()

    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }

        ingredientsTableView.items = addIngredientsObservableList
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
            recipeIngredient.amount = event.newValue ?: recipeIngredient.amount
        }

        unitColumn.cellValueFactory = PropertyValueFactory("unit")
        unitColumn.cellFactory = ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(UnitConverter.allUnits))
        unitColumn.setOnEditCommit { event ->
            val recipeIngredient = event.rowValue
            recipeIngredient.unit = event.newValue
        }
    }

    @FXML
    private fun handleSelectImageClick() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select Recipe Image"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"))

        val stage = nameField.scene.window
        val selectedFile: File? = fileChooser.showOpenDialog(stage)

        selectedFile?.let {
            selectedImagePath = it.toURI().toString()
            imagePreview.image = Image(selectedImagePath)
        }
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
                if (recipeIngredients.containsKey(newRecipeIngredient.ingredient.name)) {
                    Alert(
                        Alert.AlertType.WARNING,
                        "'${newRecipeIngredient.ingredient.name}' is already in this recipe. You can edit its amount or unit directly in the table."
                    ).showAndWait()
                } else {
                    recipeIngredients[newRecipeIngredient.ingredient.name] = newRecipeIngredient
                    ingredientsTableView.items.add(newRecipeIngredient)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Alert(Alert.AlertType.ERROR, "Could not open the Add Ingredient dialog: ${e.message}").showAndWait()
        }
    }

    @FXML
    private fun handleRemoveIngredient() {
        val selectedIngredient = ingredientsTableView.selectionModel.selectedItem ?: return

        recipeIngredients.remove(selectedIngredient.ingredient.name)
        addIngredientsObservableList.remove(selectedIngredient)
    }

    fun getRecipeData(): Recipe? {

        if (nameField.text.isBlank() || timeToCookField.text.isBlank() || descriptionArea.text.isBlank() || instructionArea.text.isBlank() || recipeIngredients.isEmpty()) {
            showError("Please fill out all recipe fields!")
            return null
        }

        error.isVisible = false
        return Recipe(
            name = nameField.text,
            timeToCook = timeToCookField.text,
            description = descriptionArea.text,
            ingredients = recipeIngredients,
            instructions = instructionArea.text,
            imagePath = selectedImagePath
        )
    }

    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}