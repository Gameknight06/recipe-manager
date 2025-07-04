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

    class Ingredient(name: String, location: String, unitType: String, cost: Double, carbs: Double, sugar: Double, defaultAmount: Double)


}