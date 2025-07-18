package com.recipe_manager

import com.google.gson.Gson
import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object FileOperations {

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private const val INGREDIENTS_FILE = "C:\\Users\\Destr\\Documents\\Recipes\\ingredients.json"
    private const val RECIPES_FILE = "C:\\Users\\Destr\\Documents\\Recipes\\recipes.json"

    /**
     * Saves a collection of recipes to a JSON file.
     */
    fun saveRecipes(recipes: Map<String, Recipe>) {
        val json = gson.toJson(recipes)
        File(RECIPES_FILE).writeText(json)
    }

    /**
     * Saves a collection of ingredients to a JSON file.
     */
    fun saveIngredients(ingredients: Map<String, Ingredient>) {
        val json = gson.toJson(ingredients)
        File(INGREDIENTS_FILE).writeText(json)
    }

    /**
     * Loads the ingredient data from a predefined JSON file into a mutable map.
     *
     * The ingredients are stored as key-value pairs where the key is the ingredient's name
     * and the value is the corresponding `Ingredient` object. If the file does not exist,
     * is empty, or cannot be parsed, an empty mutable map is returned.
     *
     * @return a mutable map containing the ingredient data, or an empty mutable map if the
     *         file is missing, empty, or invalid.
     */
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

    /**
     * Loads recipes from a JSON file and returns them as a mutable map.
     * If the file does not exist or is empty, an empty mutable map is returned.
     *
     * @return A mutable map where the keys are recipe names (as strings) and the values are the corresponding Recipe objects.
     *         Returns an empty mutable map if the file is missing, empty, or the JSON could not be parsed.
     */
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

    fun deleteIngredient(ingredient: Ingredient) {
        val ingredients = loadIngredients()
        ingredients.remove(ingredient.name)
        saveIngredients(ingredients)
    }

    fun addRecipe(newRecipe: Recipe) {
        val recipes = loadRecipes()
        recipes[newRecipe.name] = newRecipe
        saveRecipes(recipes)
    }

    fun deleteRecipe(recipe: Recipe) {
        val recipes = loadRecipes()
        recipes.remove(recipe.name)
        saveRecipes(recipes)
    }


}