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
import javafx.stage.StageStyle
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2AL
import org.kordamp.ikonli.material2.Material2MZ


/**
 * A GUI class responsible for displaying and managing the main user interface for a recipe management application.
 * This includes functionalities for viewing recipes, favorite recipes, ingredients, and editing or deleting recipes.
 *
 * This class also handles user interactions, loading content dynamically, and navigation within the application UI.
 */
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

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * It sets up initial view states, graphic icons for buttons, and event handlers.
     */
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

        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER

        addButton.styleClass.add(Tweaks.NO_ARROW)
        addButton.popupSide = Side.TOP

        Platform.runLater { rootPane.requestFocus() }
    }

    private fun showRecipeView() {
        loadAndDisplayRecipes()
    }

    private fun showFavoritesView() {
        loadAndDisplayRecipes { recipe -> recipe.isFavorite }
    }

    /**
     * Displays the ingredient view within the application's main content area.
     *
     * This method loads the IngredientView.fxml file using an FXMLLoader to create a visual representation
     * of the ingredient management interface. The loaded content is then set as the content of the
     * scroll pane within the primary application layout. If there is an error during the loading process,
     * the exception is caught, logged, and an error message is printed to the console.
     *
     * This functionality is useful for managing the application's ingredient-related operations,
     * allowing users to view and interact with ingredient details.
     */
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

    /**
     * Displays the detailed view of a specific recipe.
     *
     * This method loads the `RecipeDetails.fxml` file, sets the provided `recipe` data to its controller,
     * and then displays this detailed view within the `scrollPane`. It also sets up callbacks for
     * navigating back to the recipe list and for editing the current recipe.
     *
     * @param recipe The [Recipe] object to display in detail.
     */
    private fun showRecipeDetails(recipe: Recipe) {
        try {
            val recipeDetailsLoader = FXMLLoader(javaClass.getResource("RecipeDetails.fxml"))
            val recipeDetailsRoot = recipeDetailsLoader.load<StackPane>()
            val recipeDetailsController = recipeDetailsLoader.getController<RecipeDetailsGUI>()

            recipeDetailsController.onGoBack = { showRecipeView() }
            recipeDetailsController.onEdit = { recipeToEdit -> showEditRecipe(recipeToEdit) }
            recipeDetailsController.setRecipeDetails(recipe)
            scrollPane.content = recipeDetailsRoot

        } catch (e: Exception) {
            e.printStackTrace()
            println("Error loading RecipeDetails.fxml")
        }
    }

    /**
     * Displays a dialog for editing an existing recipe.
     *
     * This method loads the `EditRecipe.fxml` file, initializes its controller with the provided recipe data,
     * and displays it as a modal dialog. It handles saving the updated recipe back to the file system
     * and refreshing the recipe display. The dialog is styled to be undecorated and sized relative
     * to the main application window.
     *
     * @param recipe The [Recipe] object to be edited.
     */
    private fun showEditRecipe(recipe: Recipe) {
        try {
            val originalRecipeName = recipe.name

            val fxmlLoader = FXMLLoader(javaClass.getResource("EditRecipe.fxml"))
            val dialogPane = fxmlLoader.load<DialogPane>()
            val editRecipeController = fxmlLoader.getController<EditRecipeGUI>()

            editRecipeController.setRecipeDetails(recipe)

            val dialog = Dialog<Recipe>()
            createDialogWindow(rootPane, dialogPane, dialog, "Edit Recipe")


            val saveButton = dialog.dialogPane.lookupButton(editRecipeController.saveButtonType) as Button

            saveButton.addEventFilter(ActionEvent.ACTION) { event ->
                if (editRecipeController.updateRecipeFromFields() == null) {
                    event.consume()
                }
            }

            dialog.setResultConverter { buttonType ->
                if (buttonType == editRecipeController.saveButtonType) {
                    editRecipeController.updateRecipeFromFields()
                } else {
                    null
                }
            }

            dialog.showAndWait().ifPresent { updatedRecipe ->
                val allRecipes = FileOperations.loadRecipes()
                allRecipes.remove(originalRecipeName)
                allRecipes[updatedRecipe.name] = updatedRecipe
                FileOperations.saveRecipes(allRecipes)

                println("Recipe updated: ${updatedRecipe.name}")
                loadAndDisplayRecipes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Toggles the navigation drawer's visibility and position.
     *
     * This method animates the navigation drawer sliding in or out from the left side of the screen.
     * It also manages the visibility and mouse transparency of an overlay to prevent interaction with the main content when the drawer is open.
     */
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

    /**
     * Loads recipes from storage and displays them in the UI.
     *
     * This method clears the current recipe display, loads all recipes using `FileOperations.loadRecipes()`,
     * and then iterates through them to create and add a visual card for each recipe to the `recipeContainer`.
     * Each recipe card includes an image, title, description, stats, and buttons for editing, deleting,
     * and marking as favorite.
     */
    private fun loadAndDisplayRecipes(filter: (Recipe) -> Boolean = { true }) {
        scrollPane.content = recipeContainer

        recipeContainer.children.clear()

        val recipes = FileOperations.loadRecipes().values.filter(filter)

        if (recipes.isEmpty()) {
            val placeholder = Label("No recipes found. Add one using the '+' button!")
            recipeContainer.children.add(placeholder)
        } else {
            val placeholderImage = Image("com/recipe_manager/images/post-placeholder.jpg")

            for (recipe in recipes) {
                var imageToDisplay: Image

                if (!recipe.imagePath.isNullOrEmpty()) {
                    try {
                        imageToDisplay = Image(recipe.imagePath)
                    } catch (e: Exception) {
                        println("Error loading image for recipe: ${recipe.name} from path: ${recipe.imagePath}")
                        e.printStackTrace()
                        imageToDisplay = placeholderImage
                    }
                } else {
                    imageToDisplay = placeholderImage
                }

                val imageView = ImageView(imageToDisplay)
                imageToDisplay.errorProperty().addListener { _, _, _ ->
                    println("Error loading image for recipe: ${recipe.name} from path: ${recipe.imagePath}")
                    imageView.image = placeholderImage
                }
                imageView.fitHeight = rootPane.prefWidth * 0.38
                imageView.fitWidth = Double.MAX_VALUE
                imageView.isPreserveRatio = true

                val imageContainer = StackPane(imageView).apply {
                    prefHeight = rootPane.prefWidth * 0.40
                    styleClass.add("recipe-image-container")

                    onMouseClicked = javafx.event.EventHandler {
                        showRecipeDetails(recipe)
                    }
                }

                val titleLabel = Label(recipe.name)
                titleLabel.styleClass.add("recipe-title")

                val descriptionLabel = Label(recipe.description)
                descriptionLabel.styleClass.add("recipe-description")

                val statsLabel = Label("Time to make: ${recipe.timeToCook} | Times Made: ${recipe.timesMade} \nIngredients: ${recipe.ingredients.keys.toList().joinToString(", ")}")
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
                    showEditRecipe(recipe)
                }
                editButton.styleClass.addAll("accent", "text-bold", "button-icon", "small-recipe-button")

                val deleteButton = Button()
                deleteButton.graphic = FontIcon(Feather.TRASH)
                deleteButton.setOnAction {
                    val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
                        title = "Confirm Recipe Deletion"
                        headerText = "Are you sure you want to delete '${recipe.name}'?"
                        contentText = "This action cannot be undone."
                        initStyle(StageStyle.UNDECORATED)
                        initOwner(rootPane.scene.window)
                    }
                    alert.showAndWait().ifPresent { buttonType ->
                        if (buttonType == ButtonType.OK) {
                            println("Deletion confirmed for recipe: ${recipe.name}")
                            FileOperations.deleteRecipe(recipe)
                            loadAndDisplayRecipes(filter)
                        }
                    }
                }
                deleteButton.styleClass.addAll("danger", "text-bold", "button-icon", "delete-recipe-button")

                val favoriteButton = Button()
                favoriteButton.graphic = FontIcon(if (recipe.isFavorite) Material2MZ.STAR else Material2MZ.STAR_OUTLINE)
                favoriteButton.setOnAction {
                    println("${recipe.name} is now a favorite!")
                    recipe.isFavorite = !recipe.isFavorite
                    favoriteButton.graphic = FontIcon(if (recipe.isFavorite) Material2MZ.STAR else Material2MZ.STAR_OUTLINE)
                    val allRecipes = FileOperations.loadRecipes()
                    allRecipes[recipe.name] = recipe
                    FileOperations.saveRecipes(allRecipes)
                    loadAndDisplayRecipes(filter)
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

    /**
     * Handles the action when the "Add Recipe" button is clicked.
     *
     * This method loads the `AddRecipe.fxml` file to display a dialog for adding a new recipe.
     * It sets up the dialog's properties, including its title, style, and ownership.
     * Upon successful addition of a new recipe, it saves the recipe to the file system and refreshes the recipe display.
     */
    @FXML
    private fun handleAddRecipeClick() {
        try {
            val fxmlLoader = FXMLLoader(javaClass.getResource("AddRecipe.fxml"))
            val dialogPane = fxmlLoader.load<DialogPane>()
            val addRecipeController = fxmlLoader.getController<AddRecipeGUI>()

            val dialog = Dialog<Recipe>()
            createDialogWindow(rootPane, dialogPane, dialog, "Add New Recipe")

            val saveButton = dialog.dialogPane.lookupButton(addRecipeController.saveButtonType) as Button

            saveButton.addEventFilter(ActionEvent.ACTION) { event ->
                if (addRecipeController.getRecipeData() == null) {
                    event.consume()
                }
            }

            dialog.setResultConverter { buttonType ->
                if (buttonType == addRecipeController.saveButtonType) {
                    addRecipeController.getRecipeData()
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

    /**
     * Handles the action when the "Add Ingredient" button is clicked.
     *
     * This method loads the `AddIngredient.fxml` file to display a dialog for adding a new ingredient.
     * It sets up the dialog's properties, including its title, style, and ownership.
     * Upon successful addition of a new ingredient, it saves the ingredient to the file system.
     * It also includes validation to prevent the dialog from closing if the ingredient data is invalid.
     */
    @FXML
    private fun handleAddIngredientClick() {
        try {
            val fxmlLoader = FXMLLoader(javaClass.getResource("AddIngredient.fxml"))
            val dialogPane = fxmlLoader.load<DialogPane>()
            val addIngredientController = fxmlLoader.getController<AddIngredientGUI>()

            val dialog = Dialog<Ingredient>()
            createDialogWindow(rootPane, dialogPane, dialog, "Add New Ingredient")

            val saveButton = dialog.dialogPane.lookupButton(addIngredientController.saveButtonType) as Button

            saveButton.addEventFilter(ActionEvent.ACTION) { event ->
                if (addIngredientController.getNewIngredient() == null) {
                    event.consume()
                }
            }

            dialog.setResultConverter { buttonType ->
                if (buttonType == addIngredientController.saveButtonType) {
                    addIngredientController.getNewIngredient()
                } else {
                    null
                }
            }

            val result = dialog.showAndWait()

            result.ifPresent { newIngredient ->
                FileOperations.addIngredient(newIngredient)
                println("Ingredient added: ${newIngredient.name}")
                loadAndDisplayRecipes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        showFavoritesView()
    }
}
