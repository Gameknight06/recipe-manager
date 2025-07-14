package com.recipe_manager

data class RecipeIngredient(
    var ingredient: Ingredient,
    var amount: Double,
    var unit: String,
)


data class Recipe(
    var name: String,
    var timeToCook: String,
    var description: String,
    var ingredients: MutableMap<String, RecipeIngredient> = mutableMapOf(),
    var instructions: String,
    var imagePath: String? = null,
)
{
    val totalCarbs: Double
        get() = ingredients.values.sumOf { it.ingredient.carbs * it.amount }

    val totalSugar: Double
        get() = ingredients.values.sumOf { it.ingredient.sugar * it.amount }

    val estimatedCost: Double
        get() = ingredients.values.sumOf { it.ingredient.cost * it.amount }

    var timesMade: Int = 0
    var isFavorite = false

    fun incrementTimesMade() {
        timesMade++
        for(recipeIngredient in ingredients.values) {
            recipeIngredient.ingredient.currentAmount -= recipeIngredient.amount
        }

    }

    fun returnIngredients(): List<String> {
        return ingredients.keys.toList()
    }

    fun returnIngredientDetails(): List<DisplayIngredients> {
        return ingredients.values.map { recipeIngredient ->
            DisplayIngredients(recipeIngredient.ingredient.name, recipeIngredient.amount, recipeIngredient.unit)
        }
    }

}
