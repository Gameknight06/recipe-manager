package com.recipe_manager

data class Recipe(
    var name: String,
    var timeToCook: String,
    var description: String,
    var ingredients: MutableMap<Double, Ingredient> = mutableMapOf(),
    var instructions: String,
    var totalCarbs: Double,
    var estimatedCost: Double,
)
{

    class Recipe(name: String, timeToCook: String, description: String, ingredients: MutableMap<Double, Ingredient>, instructions: String, totalCarbs: Double, estimatedCost: Double)


}
