package com.recipe_manager

import atlantafx.base.controls.Notification
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL

class EditIngredientGUI {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var locationField: TextField
    @FXML private lateinit var unitComboBox: ComboBox<String>
    @FXML private lateinit var costField: TextField
    @FXML private lateinit var carbsField: TextField
    @FXML private lateinit var sugarField: TextField
    @FXML private lateinit var defaultAmountField: TextField
    @FXML lateinit var error: Notification
    @FXML lateinit var saveButtonType: ButtonType


    private lateinit var currentIngredient: Ingredient

    /**
     * Initializes the EditIngredientGUI by setting up the UI components and their behavior.
     *
     * - Configures the `error` message close behavior to hide the error message when closed.
     * - Populates the `unitComboBox` with available units from the predefined `listOfUnits`.
     * - Applies decimal-only `TextFormatter` to fields for cost, carbs, and sugar to enforce numeric input.
     */
    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }

        unitComboBox.items = FXCollections.observableArrayList(listOfUnits)
        costField.textFormatter = createDecimalTextFormatter()
        carbsField.textFormatter = createDecimalTextFormatter()
        sugarField.textFormatter = createDecimalTextFormatter()
    }

    /**
     * Updates the UI fields with the details of the given ingredient.
     */
    fun setIngredientDetails(ingredient: Ingredient) {
        currentIngredient = ingredient
        nameField.text = ingredient.name
        locationField.text = ingredient.location
        unitComboBox.selectionModel.select(ingredient.unitType)
        costField.text = ingredient.cost.toString()
        carbsField.text = ingredient.carbs.toString()
        sugarField.text = ingredient.sugar.toString()
        defaultAmountField.text = ingredient.defaultAmount.toString()
    }

    /**
     * Updates the current ingredient's details based on the values from the UI input fields.
     *
     * This method validates the input fields to ensure all required fields are filled and valid.
     * If any field is blank or invalid, an error message is displayed, and the method returns without
     * making any updates.
     *
     * If all fields are valid, the changes are applied to the `currentIngredient` instance.
     */
    fun updateIngredientFromFields() {
        if (nameField.text.isBlank() || locationField.text.isBlank() || unitComboBox.selectionModel.selectedItem == null || costField.text.isBlank() || carbsField.text.isBlank() || sugarField.text.isBlank()) {
            showError("Please fill out all fields!")
            return
        }
        currentIngredient.name = nameField.text
        currentIngredient.location = locationField.text
        currentIngredient.unitType = unitComboBox.selectionModel.selectedItem
        currentIngredient.cost = costField.text.toDouble()
        currentIngredient.carbs = carbsField.text.toDouble()
        currentIngredient.sugar = sugarField.text.toDouble()
        currentIngredient.defaultAmount = defaultAmountField.text.toDouble()
    }

    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}