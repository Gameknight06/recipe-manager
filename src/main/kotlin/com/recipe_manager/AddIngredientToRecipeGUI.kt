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