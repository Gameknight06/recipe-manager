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