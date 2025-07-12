package com.recipe_manager

import atlantafx.base.theme.Tweaks
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2AL
import org.kordamp.ikonli.material2.Material2MZ


class RecipeViewGUI {

    @FXML private lateinit var rootPane: StackPane
    @FXML private lateinit var recipeContainer: VBox
    @FXML private lateinit var addButton: MenuButton
    @FXML private lateinit var exitButton: Button
    @FXML private lateinit var recipeViewButton: Button
    @FXML private lateinit var ingredientViewButton: Button
    @FXML private lateinit var scrollPane: ScrollPane


    @FXML private lateinit var navDrawer: VBox
    @FXML private lateinit var drawerOverlay: Region
    @FXML private lateinit var mainContentPane: BorderPane
    @FXML private lateinit var favoritesButton: Button


    private var isDrawerOpen = false


    @FXML
    private fun initialize() {
        showRecipeView()

        exitButton.graphic = FontIcon(Material2AL.CLOSE)
        recipeViewButton.graphic = FontIcon(Material2MZ.SET_MEAL)
        ingredientViewButton.graphic = FontIcon(Material2AL.LIST)
        favoritesButton.graphic = FontIcon(Material2MZ.STAR)

        drawerOverlay.setOnMouseClicked {
            if (isDrawerOpen) {
                toggleNavDrawer()
            }
        }

        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;

        addButton.styleClass.add(Tweaks.NO_ARROW)
        addButton.popupSide = Side.TOP

        Platform.runLater { rootPane.requestFocus() }
    }

    private fun showRecipeView() {
        loadAndDisplayRecipes()
    }

    private fun showIngredientView() {
        try {
            val ingredientViewLoader = FXMLLoader(javaClass.getResource("IngredientView.fxml"))
            val ingredientViewRoot = ingredientViewLoader.load<VBox>()

            scrollPane.content = ingredientViewRoot
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error loading IngredientView.fxml")
        }
    }

    private fun toggleNavDrawer() {
        val drawerWidth = 200.0
        val transition = TranslateTransition(javafx.util.Duration.millis(200.0), navDrawer)

        isDrawerOpen = !isDrawerOpen
        if (isDrawerOpen) {
            drawerOverlay.isVisible = true
            mainContentPane.isMouseTransparent = true
            transition.toX = 0.0
            transition.onFinished = null
        } else {
            transition.toX = -drawerWidth
            transition.setOnFinished {
                drawerOverlay.isVisible = false
                mainContentPane.isMouseTransparent = false
            }
        }
        transition.play()
    }

    private fun loadAndDisplayRecipes() {
        scrollPane.content = recipeContainer

        val recipes = FileOperations.loadRecipes()

        if (recipes.isEmpty()) {
            val placeholder = Label("No recipes found. Add one using the '+' button!")
            recipeContainer.children.add(placeholder)
        } else {
            val placeholderImage = Image("com/recipe_manager/images/post-placeholder.jpg")

            for (recipe in recipes.values) {
                val imageView = ImageView(placeholderImage)
                imageView.fitHeight = 210.0
                imageView.fitWidth = Double.MAX_VALUE
                imageView.isPreserveRatio = true

                val imageContainer = StackPane(imageView).apply {
                    prefHeight = 243.0
                    styleClass.add("recipe-image-container")

                    onMouseClicked = javafx.event.EventHandler {
                        println("Clicked on recipe: ${recipe.name}")
                        // TODO: Navigate to the recipe details view
                    }
                }

                val imageButton = Button()
                imageButton.graphic = imageView
                imageButton.styleClass.add("image-button")
                imageButton.onAction = javafx.event.EventHandler {
                    println("Clicked on recipe: ${recipe.name}")
                    // TODO: Navigate to the recipe details view
                }

                val titleLabel = Label(recipe.name)
                titleLabel.styleClass.add("recipe-title")

                val descriptionLabel = Label(recipe.description)
                descriptionLabel.styleClass.add("recipe-description")

                val statsLabel = Label("Time to make: ${recipe.timeToCook} | Times Made: ${recipe.timesMade} \nIngredients: ${recipe.returnIngredients().joinToString(", ")}")
                statsLabel.styleClass.add("recipe-stats")

                val textContainer = VBox(titleLabel, descriptionLabel, statsLabel).apply {
                    alignment = Pos.TOP_LEFT
                    spacing = 4.0
                    padding = Insets(10.0, 15.0, 10.0, 15.0)
                }

                val editButton = Button()
                editButton.graphic = FontIcon(Feather.EDIT)
                editButton.setOnAction {
                    println("Edit button clicked for recipe: ${recipe.name}")
                }
                editButton.styleClass.addAll("accent", "text-bold", "button-icon", "small-recipe-button")

                val deleteButton = Button()
                deleteButton.graphic = FontIcon(Feather.TRASH)
                deleteButton.setOnAction {
                    println("Delete button clicked for recipe: ${recipe.name}")
                    FileOperations.deleteRecipe(recipe)
                    loadAndDisplayRecipes()
                }
                deleteButton.styleClass.addAll("danger", "text-bold", "button-icon", "delete-recipe-button")

                val favoriteButton = Button()
                favoriteButton.graphic = FontIcon(if (recipe.isFavorite) Material2MZ.STAR else Material2MZ.STAR_OUTLINE)
                favoriteButton.setOnAction {
                    println("${recipe.name} is now a favorite!")
                    recipe.isFavorite = !recipe.isFavorite
                    favoriteButton.graphic = FontIcon(if (recipe.isFavorite) Material2MZ.STAR else Material2MZ.STAR_OUTLINE)
                    FileOperations.saveRecipes(recipes)
                }
                favoriteButton.styleClass.addAll("accent", "text-bold", "button-icon", "small-recipe-button")

                val buttonContainer = VBox(favoriteButton, editButton, deleteButton)
                buttonContainer.alignment = Pos.TOP_RIGHT
                buttonContainer.spacing = 9.0

                val bottomPane = BorderPane().apply {
                    left = textContainer
                    right = buttonContainer
                    padding = Insets(0.0, 15.0, 10.0, 0.0)
                }

                val recipeCard = VBox(imageContainer, bottomPane).apply {
                    styleClass.addAll("elevated-1", "recipe-button")
                    maxWidth = Double.MAX_VALUE
                    alignment = Pos.TOP_LEFT
                    spacing = 8.0
                }

                recipeContainer.children.add(recipeCard)
            }
        }
    }

    @FXML
    private fun handleExitClick() {
        Platform.exit()
    }

    @FXML
    private fun handleAddRecipeClick() {
        try {
            val fxmlLoader = FXMLLoader(javaClass.getResource("AddRecipe.fxml"))
            val dialogPane = fxmlLoader.load<DialogPane>()
            val addRecipeController = fxmlLoader.getController<AddRecipeGUI>()

            val dialog = Dialog<Recipe>()
            dialog.dialogPane = dialogPane
            dialog.title = "Add New Recipe"
            dialog.initStyle(StageStyle.UNDECORATED)

            val stage = rootPane.scene.window as Stage
            dialog.initOwner(stage)

            dialog.width = stage.widthProperty().value * 0.85
            dialog.height = stage.heightProperty().value * 0.65

            dialog.x = stage.x + (stage.width - dialog.width) / 2
            dialog.y = stage.y + (stage.height - dialog.height) / 2

            val saveButton = dialog.dialogPane.lookupButton(addRecipeController.saveButtonType) as Button

            saveButton.addEventFilter(ActionEvent.ACTION) { event ->
                val newRecipe = addRecipeController.getNewRecipe()
                if (newRecipe == null) {
                    event.consume()
                }
            }

            dialog.setResultConverter { buttonType ->
                if (buttonType == addRecipeController.saveButtonType) {
                    addRecipeController.getNewRecipe()
                } else {
                    null
                }
            }

            val result = dialog.showAndWait()

            result.ifPresent { newRecipe ->
                FileOperations.addRecipe(newRecipe)
                println("Recipe added: ${newRecipe.name}")
                loadAndDisplayRecipes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @FXML
    private fun handleAddIngredientClick() {
        println("Add Ingredient button clicked!")

        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Coming Soon!"
        alert.headerText = null
        alert.contentText = "This feature is coming soon!"
        alert.showAndWait()
    }


    @FXML
    private fun handleMenuClick() {
        toggleNavDrawer()
    }

    @FXML
    private fun handleRecipeViewClick() {
        println("Recipe View button clicked from drawer!")
        if (isDrawerOpen) {
            toggleNavDrawer()
        }
        showRecipeView()
    }

    @FXML
    private fun handleIngredientViewClick() {
        println("Ingredient View button clicked from drawer!")
        if (isDrawerOpen) {
            toggleNavDrawer()
        }
        showIngredientView()
    }

    @FXML
    private fun handleFavoritesClick() {
        println("Favorites button clicked from drawer!")
        if (isDrawerOpen) {
            toggleNavDrawer()
        }
        // TODO: Add logic to switch to the favorites view
    }
}
