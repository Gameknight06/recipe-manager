package com.recipe_manager

import atlantafx.base.controls.Notification
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL

class AddIngredientGUI {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var locationField: TextField
    @FXML private lateinit var unitComboBox: ComboBox<String>
    @FXML private lateinit var costField: TextField
    @FXML private lateinit var carbsField: TextField
    @FXML private lateinit var sugarField: TextField
    @FXML private lateinit var defaultAmountField: TextField
    @FXML lateinit var saveButtonType: ButtonType
    @FXML lateinit var error: Notification

    /**
     * Initializes the AddIngredientGUI by configuring UI components and setting up default values or behaviors.
     *
     * - Sets up an action to hide the error notification when closed.
     * - Populates the `unitComboBox` with a list of available units.
     * - Assigns decimal-only text formatters to input fields: `costField`, `carbsField`, `sugarField`, and `defaultAmountField`.
     */
    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }
        unitComboBox.items = FXCollections.observableArrayList(listOfUnits)

        costField.textFormatter = createDecimalTextFormatter()
        carbsField.textFormatter = createDecimalTextFormatter()
        sugarField.textFormatter = createDecimalTextFormatter()
        defaultAmountField.textFormatter = createDecimalTextFormatter()
    }


    /**
     * Creates a new `Ingredient` object based on the user input from the GUI fields.
     * Validates that all required fields are filled and contain valid data before creating the ingredient.
     * If any validation fails, an error message is displayed and `null` is returned.
     *
     * @return A new `Ingredient` object if all input is valid, or `null` if validation fails.
     */
    fun getNewIngredient(): Ingredient? {
        val name = nameField.text
        val location = locationField.text
        val unit = unitComboBox.selectionModel.selectedItem ?: return null
        val cost = costField.text.toDoubleOrNull()
        val carbs = carbsField.text.toDoubleOrNull()
        val sugar = sugarField.text.toDoubleOrNull()
        val defaultAmount = defaultAmountField.text.toDoubleOrNull()


        if (name.isBlank() || location.isBlank() || unit.isBlank() || cost == null || carbs == null || sugar == null || defaultAmount == null) {
            showError("Please fill out all fields!")
            return null
        }

        error.isVisible = false
        return Ingredient(name, location, unit, cost, carbs, sugar, defaultAmount)
    }


    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}

