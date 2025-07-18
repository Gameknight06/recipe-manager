package com.recipe_manager

import com.sun.javafx.util.Utils
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.control.TextFormatter
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.util.function.UnaryOperator

val listOfUnits: List<String> = UnitConverter.allUnits

/**
 * Creates a TextFormatter that only allows numbers (e.g., "123", "12.3", ".5").
 */
fun createDecimalTextFormatter(): TextFormatter<String> {
    val validDecimalText ="\\d*\\.?\\d*".toRegex()

    val filter = UnaryOperator<TextFormatter.Change> { change ->
        val newText = change.controlNewText
        if (newText.matches(validDecimalText)) {
            change
        } else {
            null
        }
    }
    return TextFormatter(filter)
}

/**
 * Creates a list of formatted strings representing the ingredients of a given recipe.
 */
fun createIngredientStrings(recipe: Recipe): List<String> {
    val ingredientStrings = recipe.ingredients.values.map { recipeIngredient ->
        val amount = recipeIngredient.amount

        val formattedAmount = if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            amount.toString()
        }
        if (amount > 1) {
            "$formattedAmount ${recipeIngredient.unit}" + "s" + " of ${recipeIngredient.ingredient.name}"
        } else {
            "$formattedAmount ${recipeIngredient.unit} of ${recipeIngredient.ingredient.name}"
        }
    }
    return ingredientStrings
}

/**
 * Displays the "Add Ingredient" dialog for adding a new ingredient to a recipe.
 *
 * This method opens a modal dialog that allows the user to select an ingredient,
 * specify an amount, and choose a unit. It validates the user input and returns
 * the resulting RecipeIngredient object if the input is valid, or null if the dialog
 * is canceled or the input is invalid.
 *
 * @param ownerWindow the Window instance that owns the dialog. This parameter determines
 *                    the parent window for the dialog and ensures it is modal.
 * @return a RecipeIngredient object containing the ingredient, amount, and unit specified
 *         by the user, or null if the dialog is canceled or validation fails.
 */
fun showAddIngredientDialog(ownerWindow: Window): RecipeIngredient? {
    return try {
        val resourceURL = DisplayIngredients::class.java.getResource("AddIngredientToRecipe.fxml")
        val fxmlLoader = FXMLLoader(resourceURL)
        val dialogPane: DialogPane = fxmlLoader.load()
        val controller = fxmlLoader.getController<AddIngredientToRecipeGUI>()

        val dialog = Dialog<RecipeIngredient>()
        dialog.dialogPane = dialogPane
        dialog.title = "Add Ingredient"
        dialog.initOwner(ownerWindow)
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

        dialog.showAndWait().orElse(null)
    } catch (e: Exception) {
        e.printStackTrace()
        Alert(Alert.AlertType.ERROR, "Could not open the Add Ingredient dialog: ${e.message}").showAndWait()
        null
    }
}

/**
 * Creates a dialog window with specified properties and owner.
 *
 * This function configures and positions a dialog window based on the owner's stage,
 * dialog pane, title, and style settings.
 *
 * @param ownerNode The owner node whose scene and stage the dialog will inherit for ownership.
 * @param dialogPane The `DialogPane` to be displayed within the dialog window.
 * @param dialog The `Dialog` object to configure and display.
 * @param title The title of the dialog window.
 */
fun createDialogWindow(ownerNode: Node, dialogPane: DialogPane, dialog: Dialog<*>, title: String) {
    dialog.dialogPane = dialogPane
    dialog.title = title
    dialog.initStyle(StageStyle.UNDECORATED)

    val stage = ownerNode.scene.window as Stage
    dialog.initOwner(stage)
    dialog.width = stage.widthProperty().value * 0.85
    dialog.height = stage.heightProperty().value * 0.65
    dialog.x = stage.x + (stage.width - dialog.width) / 2
    dialog.y = stage.y + (stage.height - dialog.height) / 2
}

/**
 * Represents an ingredient to be displayed, including its name, amount, and unit.
 */
data class DisplayIngredients(val name: String, val amount: Double, val unit: String) {
    override fun toString(): String = "$amount $unit of $name"
}
