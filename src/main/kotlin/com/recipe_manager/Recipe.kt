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

    fun returnIngredients(): MutableList<String> {
        var allIngredients = mutableListOf<String>()
        for(name in ingredients.keys) {
            allIngredients.add(name)
        }

        return allIngredients
    }


}
