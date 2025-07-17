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
    @FXML private lateinit var ingredientsListView: ListView<String>
    @FXML private lateinit var goBackButton: Button
    @FXML private lateinit var editButton: Button

    private var currentRecipe: Recipe? = null

    var onGoBack: () -> Unit = {}
    var onEdit: (Recipe) -> Unit = {}

    fun setRecipeDetails(recipe: Recipe) {
        this.currentRecipe = recipe

        recipeName.text = recipe.name
        recipeTime.text = "Time to Cook: \n\n${recipe.timeToCook}"
        recipeDescription.text = "Description: \n\n${recipe.description}"
        recipeCost.text = "Estimated Cost: \n\n$${recipe.estimatedCost.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP)}"
        recipeIngredients.text = "Ingredients:"
        recipeInstructions.text = "Instructions: \n\n${recipe.instructions}"

        val ingredientStrings = recipe.ingredients.values.map { recipeIngredient ->
            val amount = recipeIngredient.amount

            val formattedAmount = if (amount % 1.0 == 0.0) {
                amount.toInt().toString()
            } else {
                amount.toString()
            }
            "$formattedAmount ${recipeIngredient.unit} of ${recipeIngredient.ingredient.name}"
        }

        ingredientsListView.items.setAll(ingredientStrings)
    }

    @FXML
    private fun handleGoBackClick() {
        onGoBack()
    }

    @FXML
    private fun handleEditClick() {
        currentRecipe?.let { onEdit(it) }
    }

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