package com.recipe_manager

import atlantafx.base.controls.Notification
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL


class AddRecipeGUI {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var timeToCookField: TextField
    @FXML private lateinit var descriptionArea: TextArea
    @FXML private lateinit var instructionArea: TextArea
    @FXML private lateinit var ingredientsListView: ListView<String>
    @FXML private lateinit var ingredientComboBox: ComboBox<String>
    @FXML private lateinit var amountField: TextField
    @FXML private lateinit var unitField: TextField
    @FXML lateinit var saveButtonType: ButtonType
    @FXML lateinit var error: Notification



    private val allIngredients = FileOperations.loadIngredients()
    private val recipeIngredients = mutableMapOf<String, RecipeIngredient>()
    private val addIngredientsObservableList = FXCollections.observableArrayList<String>()

    @FXML
    private fun initialize() {
        error.setOnClose {error.isVisible = false}

        ingredientComboBox.items = FXCollections.observableArrayList(allIngredients.keys.sorted())

        ingredientsListView.items = addIngredientsObservableList

        ingredientComboBox.selectionModel.selectedItemProperty().addListener { _, _, selectedIngredientName ->
            val ingredient = allIngredients[selectedIngredientName]
        }
    }

    @FXML
    private fun handleAddIngredient() {
        val selectedIngredientName = ingredientComboBox.selectionModel.selectedItem ?: return
        val amount = amountField.text.toDoubleOrNull()
        val unit = unitField.text
        val ingredient = allIngredients[selectedIngredientName]


        if (amount == null || ingredient == null || unit.isBlank()) {
            showError("Missing input for ingredient")
            return
        }

        val recipeIngredient = RecipeIngredient(ingredient, amount, unit)
        recipeIngredients[selectedIngredientName] = recipeIngredient
        addIngredientsObservableList.add("$selectedIngredientName - $amount $unit")

        ingredientComboBox.selectionModel.clearSelection()
        amountField.clear()
        unitField.clear()
        error.isVisible = false
    }

    fun getNewRecipe(): Recipe? {
        val name = nameField.text
        val timeToCook = timeToCookField.text
        val description = descriptionArea.text
        val instructions = instructionArea.text

        if (name.isBlank() || timeToCook.isBlank() || recipeIngredients.isEmpty()) {
            showError("Validation failed: Name, time to cook, and at least one ingredient are required.")
            return null
        }

        error.isVisible = false
        return Recipe(name, timeToCook, description, recipeIngredients, instructions)
    }

    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}