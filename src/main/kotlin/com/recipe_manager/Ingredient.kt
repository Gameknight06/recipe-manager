package com.recipe_manager

data class Ingredient(
    var name: String,
    var location: String,
    var unitType: String,
    var cost: Double,
    var carbs: Double,
    var sugar: Double,
    var defaultAmount: Double,
)
{
    var amountForRecipe: Double = 0.0
    var currentAmount: Double = 0.0

    class Ingredient(name: String, location: String, unitType: String, cost: Double, carbs: Double, sugar: Double, defaultAmount: Double, amountForRecipe: Double, currentAmount: Double)


}