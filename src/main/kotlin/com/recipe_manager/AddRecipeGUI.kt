package com.recipe_manager

import atlantafx.base.controls.Notification
import atlantafx.base.util.DoubleStringConverter
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.util.Callback
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
    private val recipeIngredients = mutableMapOf<String, RecipeIngredient>()
    private val addIngredientsObservableList = FXCollections.observableArrayList<RecipeIngredient>()

    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }

        ingredientsTableView.items = addIngredientsObservableList
        setupIngredientsTable()
    }

    /**
     * Configures the ingredients table to enable editing and data binding for recipe ingredients.
     *
     * This method sets up the `ingredientsTableView` by making it editable and associating
     * specific columns with properties of `RecipeIngredient` objects. It also defines how
     * edits are handled in the table for each column.
     *
     * Editing event handlers are registered for the `amountColumn` and `unitColumn` to
     * immediately update the corresponding `RecipeIngredient` object when a change is made.
     */
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

    /**
     * Handles the click event for selecting an image in the "Add Recipe" form.
     *
     * This method opens a file chooser dialog to allow the user to select an image file
     * with one of the supported extensions (.jpg, .jpeg, .png). Upon successful selection,
     * the selected image is set as a preview in the corresponding `ImageView` and the
     * image path is stored for later use.
     *
     * - Configures a `FileChooser` with a title and file type filters for images.
     * - Opens the file chooser dialog using the current window as a parent.
     * - Updates the `imagePreview` with the selected image.
     * - Sets the `selectedImagePath` to the URI of the selected image.
     *
     * If no file is selected, the method exits without making changes.
     */
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

    /**
     * Handles user interaction for adding a new ingredient to the recipe.
     *
     * - Invokes the `showAddIngredientDialog` method to display the input dialog for adding an ingredient.
     * - Validates whether the input ingredient already exists within `recipeIngredients`.
     * - If the ingredient exists, displays an error message to the user using `showError`.
     * - If the ingredient is new, adds it to `recipeIngredients` and updates `addIngredientsObservableList`.
     */
    @FXML
    private fun handleAddIngredient() {
        val newIngredient = showAddIngredientDialog(ingredientsTableView.scene.window)

        newIngredient?.let {
            if (recipeIngredients.containsKey(it.ingredient.name)) {
                showError("Ingredient '${it.ingredient.name}' already exists in the recipe!")
            } else {
                recipeIngredients[it.ingredient.name] = it
                addIngredientsObservableList.add(it)
            }
        }
    }

    /**
     * Handles the removal of the selected ingredient from the recipe being created or edited.
     *
     * - Retrieves the currently selected ingredient in `ingredientsTableView`.
     * - If no ingredient is selected, the method exits without performing any action.
     * - Removes the selected ingredient from the `recipeIngredients` map using the ingredient's name as the key.
     * - Removes the selected ingredient from the `addIngredientsObservableList` to update the UI.
     *
     * It ensures that both the internal recipe data and the UI remain in sync after the ingredient is removed.
     */
    @FXML
    private fun handleRemoveIngredient() {
        val selectedIngredient = ingredientsTableView.selectionModel.selectedItem ?: return

        recipeIngredients.remove(selectedIngredient.ingredient.name)
        addIngredientsObservableList.remove(selectedIngredient)
    }

    /**
     * Retrieves recipe data from the provided input fields.
     *
     * This method validates that all required fields are filled, including name, cooking time,
     * description, instructions, and ingredients. If any of these fields are blank or
     * the ingredients are empty, an error message is displayed and the method returns `null`.
     * Otherwise, it constructs and returns a `Recipe` object based on the current input data.
     *
     * @return A `Recipe` object if all fields are valid; otherwise, `null` if validation fails.
     */
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