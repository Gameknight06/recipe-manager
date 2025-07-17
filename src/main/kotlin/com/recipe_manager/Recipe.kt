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
        get() = ingredients.values.sumOf { recipeIngredient ->
            val ingredient = recipeIngredient.ingredient
            if (ingredient.defaultAmount == 0.0) return@sumOf 0.0

            val convertedAmount = UnitConverter.convert(recipeIngredient.unit, ingredient.unitType, recipeIngredient.amount)

            convertedAmount?.let {
                (it / ingredient.defaultAmount) * ingredient.cost
            } ?: 0.0
        }

    var timesMade: Int = 0
    var isFavorite = false

    fun incrementTimesMade() {
        timesMade++
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
