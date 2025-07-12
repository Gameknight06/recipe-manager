package com.recipe_manager

import atlantafx.base.theme.CupertinoDark
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage


/**
Outline for the program:

 Each recipe is a data class and has its own ingredients and details: name, time to cook, description of product, ingredients and their amounts, instructions, total carbs or nutritional information, and estimated cost.
 Each ingredient should be a data class and have details: name, where they are stored, the cost of each ingredient by its entire weight or volume, and the carbs or other nutritional information.

 There should be a GUI that allows the user to see all the recipes with perhaps an image, and a button to add a new one.
 The adding page should have an area to fill out the recipe details plus an area to add ingredients with a plus button.
 There should also be an option to add new ingredients to be chosen when adding a recipe, and they should then have their details stored so they can be used again.
 There should be a search/filter functionality for recipes (e.g., by name, ingredient, cooking time).
 Users should be able to edit and delete existing recipes and ingredients.

 The application should persist data (e.g., using a local database or file storage), so it's available across sessions.
 There should be a feature to input how much of each ingredient you have. Everytime you click a button to increment how many times you've made a specific recipe,
 it would take out the amount of each ingredient it uses to let you know how much of each ingredient you have.

 */

class Main : Application() {

    override fun start(primaryStage: Stage) {
        Application.setUserAgentStylesheet(CupertinoDark().userAgentStylesheet)

        val fxmlLoader = FXMLLoader(Main::class.java.getResource("/com/recipe_manager/RecipeView.fxml"))
        val scene = Scene(fxmlLoader.load())
        scene.stylesheets.add(
            Main::class.java.getResource("/com/recipe_manager/styles/custom.css").toExternalForm()
        )

        val image: Image = Image("com/recipe_manager/images/recipe.png")
        primaryStage.icons.add(image)

        primaryStage.title = "Recipe Manager"
        primaryStage.scene = scene
        primaryStage.show()

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)

        }
    }


}