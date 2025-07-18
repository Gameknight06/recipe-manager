package com.recipe_manager

import atlantafx.base.controls.Notification
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL

class RecipeDetailsGUI {

    @FXML private lateinit var rootPane: StackPane
    @FXML private lateinit var recipeName: Label
    @FXML private lateinit var recipeTime: Label
    @FXML private lateinit var recipeDescription: Label
    @FXML private lateinit var recipeIngredients: Label
    @FXML private lateinit var recipeInstructions: Label
    @FXML private lateinit var recipeCost: Label
    @FXML private lateinit var recipeCarbs: Label
    @FXML private lateinit var recipeSugar: Label
    @FXML private lateinit var ingredientsListView: ListView<String>
    @FXML private lateinit var goBackButton: Button
    @FXML private lateinit var editButton: Button

    private var currentRecipe: Recipe? = null

    /**
     * Callback functions that are invoked to handle the action of navigating back from the current view and editing the recipe.
     */
    var onGoBack: () -> Unit = {}
    var onEdit: (Recipe) -> Unit = {}

    /**
     * Updates the current recipe details in the GUI, including recipe name, time, description, cost,
     * carbohydrate content, sugar content, ingredients, and instructions.
     * Also updates a list view with formatted ingredient details.
     */
    fun setRecipeDetails(recipe: Recipe) {
        this.currentRecipe = recipe

        recipeName.text = recipe.name
        recipeTime.text = "Time to Cook: \n\n${recipe.timeToCook}"
        recipeDescription.text = "Description: \n\n${recipe.description}"
        recipeCost.text = "Estimated Cost: \n\n$${recipe.estimatedCost.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP)}"
        recipeCarbs.text = "Total Carbs: \n\n${recipe.totalCarbs.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP)}"
        recipeSugar.text = "Total Sugar: \n\n${recipe.totalSugar.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP)}"
        recipeIngredients.text = "Ingredients:"
        recipeInstructions.text = "Instructions: \n\n${recipe.instructions}"

        // An array which stores the formatted ingredient details for each ingredient in the recipe
        val ingredientStrings = createIngredientStrings(recipe)

        ingredientsListView.items.setAll(ingredientStrings)
    }

    /**
     * Handles the action of clicking the "Go Back" button in the recipe details GUI.
     *
     * This method triggers the navigation back to the previous view, typically
     * the main recipe list screen. It delegates the navigation logic to the
     * `onGoBack` function provided during initialization.
     */
    @FXML
    private fun handleGoBackClick() {
        onGoBack()
    }

    /**
     * Handles the action of clicking the "Edit" button in the recipe details GUI.
     *
     * This method allows the user to initiate the editing process for the currently
     * displayed recipe. It checks if there is a selected or active recipe (`currentRecipe`)
     * and, if so, delegates the editing logic to the `onEdit` function by passing the
     * current recipe as its parameter.
     */
    @FXML
    private fun handleEditClick() {
        currentRecipe?.let { onEdit(it) }
    }

    /**
     * Handles the action of clicking the "Increment Times Made" button in the recipe details GUI.
     *
     * This method displays a confirmation dialog to the user. If the user confirms, it increments
     * the `timesMade` property of the currently displayed recipe, saves the updated recipe
     * to the file system, and prints a confirmation message to the console.
     * The recipe data is loaded, updated, and then saved back to maintain persistence.
     */
    @FXML
    private fun handleIncrementTimesMadeClick() {
        showConfirmationDialog {
            currentRecipe?.let { recipeToUpdate ->
                recipeToUpdate.incrementTimesMade()

                val allRecipes = FileOperations.loadRecipes()
                allRecipes[recipeToUpdate.name] = recipeToUpdate
                FileOperations.saveRecipes(allRecipes)

                println("Incremented times made for ${recipeToUpdate.name}")
            }
        }
    }

    /**
     * Displays a confirmation dialog to the user.
     * onConfirm is A lambda function to be executed if the user confirms the action.
     */
    private fun showConfirmationDialog(onConfirm: () -> Unit) {
        val ntf = Notification("Are you sure you want to increment how many times you've made this recipe?",FontIcon(Material2OutlinedAL.HELP_OUTLINE))
        ntf.maxWidth = 350.0
        ntf.maxHeight = 100.0
        ntf.styleClass.addAll("elevated-1", "text-bold")

        val yesButton = Button("Yes")
        val noButton = Button("No")

        yesButton.setOnAction {
            rootPane.children.remove(ntf)
            onConfirm()
        }

        noButton.setOnAction {
            rootPane.children.remove(ntf)
        }

        ntf.primaryActions.addAll(yesButton, noButton)

        if (!rootPane.children.contains(ntf)) {
            rootPane.children.add(ntf)
        }
    }
}