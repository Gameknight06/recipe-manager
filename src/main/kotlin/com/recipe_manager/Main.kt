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
 There is a GUI that allows the user to see all the recipes with an image, and a button to add a new recipe or ingredient.
 The adding page has an area to fill out the recipe details plus an area to add ingredients with its own small dialog window.

 Each ingredient is a data class and has details: name, where they are stored, the cost of each ingredient by its entire weight or volume, and the carbs or other nutritional information.
 There is a GUI that allows the user to see all the ingredients in a table view with the ability to edit or delete any of them.
 The adding page for ingredients has an area to fill out the ingredient details.

 Users are able to edit and delete existing recipes and ingredients.

 There is a details page for each recipe that shows all its details and has the option to go back to the recipes page, edit the recipe, or increment how many times it has been made.

 The application persists data using two local files on the computer to store recipes and ingredients, so it's available across sessions.
 */

class Main : Application() {

    override fun start(primaryStage: Stage) {
        Application.setUserAgentStylesheet(CupertinoDark().userAgentStylesheet)

        val fxmlLoader = FXMLLoader(Main::class.java.getResource("/com/recipe_manager/RecipeView.fxml")) // Load the FXML file for the recipe view which will be the starting view
        val scene = Scene(fxmlLoader.load())
        scene.stylesheets.add(
            Main::class.java.getResource("/com/recipe_manager/styles/custom.css").toExternalForm()
        )

        val image: Image = Image("com/recipe_manager/images/recipe.png") // Adding the application icon
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