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
    /**
     * Calculates the total carbohydrate content for all ingredients in a recipe.
     *
     * This property computes the sum of the carbohydrate content for each ingredient
     * based on its amount, unit, and nutritional information. The calculation involves:
     * - Converting the ingredient's amount to its default unit type.
     * - Determining the proportion of the ingredient's quantity relative to its default amount.
     * - Multiplying the proportion by its carbohydrate content.
     *
     * Ingredients with a `defaultAmount` of 0.0 or those where unit conversion fails
     * are excluded from the total calculation, contributing 0.0 to the sum.
     */
    val totalCarbs: Double
        get() = ingredients.values.sumOf { recipeIngredient ->
            val ingredient = recipeIngredient.ingredient
            if (ingredient.defaultAmount == 0.0) return@sumOf 0.0

            val convertedAmount = UnitConverter.convert(recipeIngredient.unit, ingredient.unitType, recipeIngredient.amount)

            convertedAmount?.let {
                (it / ingredient.defaultAmount) * ingredient.carbs
            } ?: 0.0
        }


    /**
     * Calculates the total sugar content for all ingredients in a recipe.
     *
     * This property computes the sum of the sugar content for each ingredient
     * based on its amount, unit, and nutritional information. The calculation involves:
     * - Converting the ingredient's amount to its default unit type.
     * - Determining the proportion of the ingredient's quantity relative to its default amount.
     * - Multiplying the proportion by its sugar content.
     *
     * Ingredients with a `defaultAmount` of 0.0 or those where unit conversion fails
     * are excluded from the total calculation, contributing 0.0 to the sum.
     */
    val totalSugar: Double
        get() = ingredients.values.sumOf { recipeIngredient ->
            val ingredient = recipeIngredient.ingredient
            if (ingredient.defaultAmount == 0.0) return@sumOf 0.0

            val convertedAmount = UnitConverter.convert(recipeIngredient.unit, ingredient.unitType, recipeIngredient.amount)

            convertedAmount?.let {
                (it / ingredient.defaultAmount) * ingredient.sugar
            } ?: 0.0
        }

    /**
     * Calculates the total cost for all ingredients in a recipe.
     *
     * This property computes the sum of the cost for each ingredient
     * based on its amount, unit, and cost. The calculation involves:
     * - Converting the ingredient's amount to its default unit type.
     * - Determining the proportion of the ingredient's quantity relative to its default amount.
     * - Multiplying the proportion by its cost.
     *
     * Ingredients with a `defaultAmount` of 0.0 or those where unit conversion fails
     * are excluded from the total calculation, contributing 0.0 to the sum.
     */
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

    /**
     * Converts the ingredients stored in the recipe into a list of `DisplayIngredients` objects,
     * which include the name, amount, and unit of each ingredient.
     *
     * Returns A list of `DisplayIngredients` representing the details of each ingredient in the recipe.
     */
    fun returnIngredientDetails(): List<DisplayIngredients> {
        return ingredients.values.map { recipeIngredient ->
            DisplayIngredients(recipeIngredient.ingredient.name, recipeIngredient.amount, recipeIngredient.unit)
        }
    }
}
