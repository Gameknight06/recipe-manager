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

    /**
     * Updates the GUI components with the details of the given recipe.
     *
     * This method populates various fields in the edit recipe form, such as the recipe's name,
     * cooking time, description, instructions, and ingredients. It also handles loading and
     * displaying the associated image for the recipe, if available.
     */
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

    /**
     * Configures the ingredients table for the recipe editing interface.
     *
     * This method makes the ingredients table editable and sets up its columns
     *
     * Handles editing actions by updating the underlying `recipeIngredients` data model
     * whenever changes are made to the amount or unit in the respective columns.
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

        currentRecipe.ingredients.remove(selectedIngredient.ingredient.name)
        ingredientsTableView.items.remove(selectedIngredient)
    }

    /**
     * Handles the logic for adding a new ingredient to the currently edited recipe.
     *
     * This method opens a dialog window for adding an ingredient to the recipe. The dialog allows
     * the user to specify the ingredient, its amount, and its unit of measurement. It validates
     * user input to ensure all required fields are filled correctly. If the input is valid, the
     * ingredient is added to the recipe and displayed in the ingredients table view.
     *
     * If the ingredient already exists in the recipe, a warning alert is displayed, indicating
     * the user can edit the ingredient directly within the table instead of adding it again.
     *
     * In case of any error while opening the dialog, an error alert is shown to the user.
     */
    @FXML
    private fun handleAddIngredient() {
        val newIngredient = showAddIngredientDialog(ingredientsTableView.scene.window)

        newIngredient?.let {
            if (currentRecipe.ingredients.containsKey(it.ingredient.name)) {
                showError("Ingredient '${it.ingredient.name}' already exists in the recipe!")
            } else {
                currentRecipe.ingredients[it.ingredient.name] = it
                ingredientsTableView.items.add(it)
            }
        }
    }

    /**
     * Handles the event triggered when the "Select Image" button is clicked.
     *
     * This method opens a file chooser dialog, allowing the user to select an image file
     * (e.g., PNG, JPG, JPEG) to associate with the current recipe. Upon successful
     * selection of a file, the image is previewed in the designated image preview area
     * and its path is stored for later use.
     *
     * If no file is selected, the method performs no action.
     */
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

    /**
     * Updates the current recipe instance with data entered in the GUI fields.
     *
     * This method retrieves input values from various GUI components, such as name, time to cook,
     * description, instructions, and image. It validates that all required fields are filled.
     * If validation passes, it updates the properties of the current recipe with the field values.
     *
     * Additionally, it processes the ingredients from the GUI table to create a mapping of ingredient
     * names to `RecipeIngredient` objects. The method validates that all ingredients in the table exist
     * in the ingredient database. If an ingredient is not found, an error message is displayed and
     * the recipe is not updated.
     *
     * @return The updated `Recipe` object if all fields are valid and the update process completes successfully.
     *         Returns `null` in cases where validation fails or an error occurs during the update.
     */
    fun updateRecipeFromFields(): Recipe? {
        val recipe = currentRecipe ?: return null

        if (nameField.text.isBlank() || timeToCookField.text.isBlank() || descriptionArea.text.isBlank() || instructionArea.text.isBlank()) {
            showError("Please fill out all recipe fields!")
            return null
        }

        recipe.name = nameField.text
        recipe.timeToCook = timeToCookField.text
        recipe.description = descriptionArea.text
        recipe.instructions = instructionArea.text
        recipe.imagePath = selectedImagePath

        val ingredientsFromTable = ingredientsTableView.items
        val allIngredients = FileOperations.loadIngredients()
        val updatedIngredientsMap = mutableMapOf<String, RecipeIngredient>()

        for (displayIngredient in ingredientsFromTable) {
            val fullIngredient = allIngredients[displayIngredient.ingredient.name]
            if (fullIngredient != null) {
                updatedIngredientsMap[displayIngredient.ingredient.name] = RecipeIngredient(
                    ingredient = fullIngredient,
                    amount = displayIngredient.amount,
                    unit = displayIngredient.unit
                )
            } else {
                showError("Ingredient '${displayIngredient.ingredient.name}' not found in database.")
                return null
            }
        }
        recipe.ingredients = updatedIngredientsMap

        return recipe
    }

    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}