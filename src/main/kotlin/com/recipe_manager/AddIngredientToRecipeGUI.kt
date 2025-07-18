package com.recipe_manager

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.util.StringConverter

class AddIngredientToRecipeGUI {

    @FXML private lateinit var ingredientComboBox: ComboBox<Ingredient>
    @FXML private lateinit var amountField: TextField
    @FXML private lateinit var unitComboBox: ComboBox<String>
    @FXML private lateinit var errorLabel: Label
    @FXML lateinit var addButtonType: ButtonType

    private var allIngredients = FileOperations.loadIngredients().values.toList()

    /**
     * Initializes the UI components and binds their properties for user interaction.
     *
     * - Populates the `ingredientComboBox` with a list of all available ingredients and sets a custom
     *   `StringConverter` to manage display names.
     * - Populates the `unitComboBox` with a list of all available units.
     * - Applies a text formatter to the `amountField` to allow only valid numeric inputs.
     *
     * Additionally, it adds listeners to clear the `errorLabel` text whenever a modification is made
     * to either the selected ingredient, amount, or unit in their respective components.
     */
    @FXML
    fun initialize() {
        ingredientComboBox.items = FXCollections.observableArrayList(allIngredients)
        ingredientComboBox.converter = object : StringConverter<Ingredient>() {
            override fun toString(ingredient: Ingredient?): String = ingredient?.name ?: ""
            override fun fromString(string: String?): Ingredient? = allIngredients.find { it.name == string }
        }

        unitComboBox.items = FXCollections.observableArrayList(UnitConverter.allUnits)

        amountField.textFormatter = createDecimalTextFormatter()

        ingredientComboBox.valueProperty().addListener { _ -> errorLabel.text = "" }
        amountField.textProperty().addListener { _ -> errorLabel.text = "" }
        unitComboBox.valueProperty().addListener { _ -> errorLabel.text = "" }
    }

    /**
     * Validates the user's input for ingredient selection, amount, and unit,
     * and returns a `RecipeIngredient` object if the input is valid. If any of
     * the input fields are invalid, it updates the `errorLabel` with an appropriate
     * error message and returns null.
     *
     * @return a `RecipeIngredient` object containing the selected ingredient,
     * amount, and unit if all inputs are valid; otherwise, null.
     */
    fun validateAndGetResult(): RecipeIngredient? {
        val selectedIngredient = ingredientComboBox.selectionModel.selectedItem
        val amountText = amountField.text
        val selectedUnit = unitComboBox.selectionModel.selectedItem

        if (selectedIngredient == null) {
            errorLabel.text = "Please select an ingredient."
            return null
        }
        if (amountText.isBlank()) {
            errorLabel.text = "Please enter an amount."
            return null
        }
        if (selectedUnit == null) {
            errorLabel.text = "Please select a unit."
            return null
        }
        return RecipeIngredient(selectedIngredient, amountText.toDouble(), selectedUnit)
    }
}