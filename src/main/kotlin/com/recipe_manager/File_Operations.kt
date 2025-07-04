package com.recipe_manager

import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object File_Operations {

    val gson = GsonBuilder().setPrettyPrinting().create()

    private const val INGREDIENTS_FILE = "C:\\Users\\Destr\\Documents\\Recipes\\ingredients.json"
    private const val RECIPES_FILE = "C:\\Users\\Destr\\Documents\\Recipes\\recipes.json"

    fun saveRecipes(recipes: Map<String, Recipe>) {
        val json = gson.toJson(recipes)
        File(RECIPES_FILE).writeText(json)
    }

    fun saveIngredients(ingredients: Map<String, Ingredient>) {
        val json = gson.toJson(ingredients)
        File(INGREDIENTS_FILE).writeText(json)
    }

    fun loadIngredients(): MutableMap<String, Ingredient> {
        val file = File(INGREDIENTS_FILE)
        if (!file.exists()) {
            return mutableMapOf()
        }
        val json = file.readText()
        if (json.isBlank()) {
            return mutableMapOf()
        }
        val type = object : TypeToken<MutableMap<String, Ingredient>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }

    fun loadRecipes(): MutableMap<String, Recipe> {
        val file = File(RECIPES_FILE)
        if (!file.exists()) {
            return mutableMapOf()
        }
        val json = file.readText()
        if (json.isBlank()) {
            return mutableMapOf()
        }
        val type = object : TypeToken<MutableMap<String, Recipe>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }


    fun addIngredient(newIngredient: Ingredient) {
        val ingredients = loadIngredients()
        ingredients[newIngredient.name] = newIngredient
        saveIngredients(ingredients)
    }

    fun addRecipe(newRecipe: Recipe) {
        val recipes = loadRecipes()
        recipes[newRecipe.name] = newRecipe
        saveRecipes(recipes)
    }


}