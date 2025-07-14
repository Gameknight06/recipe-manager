package com.recipe_manager

import atlantafx.base.controls.Notification
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL
import java.io.File

class EditRecipeGUI {

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
    @FXML private lateinit var selectImageButton: Button
    @FXML private lateinit var imagePreview: ImageView

    private var selectedImagePath: String? = null
    private lateinit var currentRecipe: Recipe
    private val allIngredients = FileOperations.loadIngredients()
    private val recipeIngredients = mutableMapOf<String, RecipeIngredient>()
    private val addIngredientsObservableList = FXCollections.observableArrayList<DisplayIngredients>()

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

        recipeIngredients.clear()
        recipeIngredients.putAll(recipe.ingredients)
        addIngredientsObservableList.setAll(recipe.returnIngredientDetails())
    }

    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }

        ingredientComboBox.items = FXCollections.observableArrayList(allIngredients.keys.sorted())
        unitComboBox.items = FXCollections.observableArrayList(listOfUnits)
        amountField.textFormatter = createDecimalTextFormatter()
        ingredientsListView.items = addIngredientsObservableList


        ingredientsListView.setCellFactory {
            object : ListCell<DisplayIngredients>() {
                private val hbox = HBox(10.0)
                private val label = Label()
                private val spacer = Region()
                private val deleteButton = Button(null, FontIcon(Feather.TRASH))

                init {
                    deleteButton.styleClass.addAll("danger", "text-bold", "button-icon", "delete-recipe-button")
                    HBox.setHgrow(spacer, Priority.ALWAYS)
                    hbox.children.addAll(label, spacer, deleteButton)
                    hbox.alignment = Pos.CENTER_LEFT
                }

                override fun updateItem(p0: DisplayIngredients?, empty: Boolean) {
                    super.updateItem(p0, empty)
                    if (empty || item == null) {
                        graphic = null
                    } else {
                        label.text = item.toString()
                        deleteButton.setOnAction {
                            recipeIngredients.remove(item.name)
                            listView.items.remove(item)
                        }
                        graphic = hbox
                    }
                }
            }
        }
    }

    @FXML
    private fun handleAddIngredient() {
        val selectedIngredientName = ingredientComboBox.selectionModel.selectedItem ?: return
        val amount = amountField.text.toDoubleOrNull()
        val unit = unitComboBox.selectionModel.selectedItem ?: return
        val ingredient = allIngredients[selectedIngredientName]


        if (amount == null || ingredient == null || unit.isBlank()) {
            showError("Missing or invalid input for ingredient!")
            return
        }

        val recipeIngredient = RecipeIngredient(ingredient, amount, unit)
        recipeIngredients[selectedIngredientName] = recipeIngredient
        addIngredientsObservableList.add(DisplayIngredients(selectedIngredientName, amount, unit))

        ingredientComboBox.selectionModel.clearSelection()
        amountField.clear()
        unitComboBox.selectionModel.clearSelection()
        error.isVisible = false
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