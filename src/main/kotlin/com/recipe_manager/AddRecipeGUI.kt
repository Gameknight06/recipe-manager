package com.recipe_manager

import atlantafx.base.controls.Notification
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL


class AddRecipeGUI {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var timeToCookField: TextField
    @FXML private lateinit var descriptionArea: TextArea
    @FXML private lateinit var instructionArea: TextArea
    @FXML private lateinit var ingredientsListView: ListView<DisplayIngredients>
    @FXML private lateinit var ingredientComboBox: ComboBox<String>
    @FXML private lateinit var amountField: TextField
    @FXML private lateinit var unitComboBox: ComboBox<String>
    @FXML lateinit var saveButtonType: ButtonType
    @FXML lateinit var error: Notification



    private val allIngredients = FileOperations.loadIngredients()
    private val recipeIngredients = mutableMapOf<String, RecipeIngredient>()
    private val addIngredientsObservableList = FXCollections.observableArrayList<DisplayIngredients>()
    val listOfUnits: MutableList<String> = mutableListOf("Cup(s)", "Tablespoon(s)", "Teaspoon(s)", "Ounce(s)", "Fluid Ounce(s)", "Pound(s)", "Quart(s)", "Liter(s)", "Milliliter(s)", "Milligram(s)", "Gram(s)", "Kilogram(s)")

    private data class DisplayIngredients(val name: String, val amount: Double, val unit: String) {
        override fun toString(): String = "$name - $amount $unit"
    }

    @FXML
    private fun initialize() {
        error.setOnClose { error.isVisible = false }

        ingredientComboBox.items = FXCollections.observableArrayList(allIngredients.keys.sorted())
        ingredientsListView.items = addIngredientsObservableList
        unitComboBox.items = FXCollections.observableArrayList(listOfUnits)

        ingredientsListView.setCellFactory {
            object : ListCell<DisplayIngredients>() {
                private val hbox = HBox(10.0)
                private val label = Label()
                private val spacer = Region()
                private val deleteButton = Button(null, FontIcon(Feather.TRASH))

                init {
                    deleteButton.styleClass.addAll("danger", "text-bold", "button-icon", "delete-recipe-button")
                    HBox.setHgrow(spacer, Priority.ALWAYS)
                    hbox.children.addAll(label, spacer, deleteButton)
                    hbox.alignment = Pos.CENTER_LEFT
                }

                override fun updateItem(p0: DisplayIngredients?, empty: Boolean) {
                    super.updateItem(p0, empty)
                    if (empty || item == null) {
                        graphic = null
                    } else {
                        label.text = item.toString()
                        deleteButton.setOnAction {
                            recipeIngredients.remove(item.name)
                            listView.items.remove(item)
                        }
                        graphic = hbox
                    }
                }
            }
        }
    }

    @FXML
    private fun handleAddIngredient() {
        val selectedIngredientName = ingredientComboBox.selectionModel.selectedItem ?: return
        val amount = amountField.text.toDoubleOrNull()
        val unit = unitComboBox.selectionModel.selectedItem ?: return
        val ingredient = allIngredients[selectedIngredientName]


        if (amount == null || ingredient == null || unit.isBlank()) {
            showError("Missing or invalid input for ingredient!")
            return
        }

        val recipeIngredient = RecipeIngredient(ingredient, amount, unit)
        recipeIngredients[selectedIngredientName] = recipeIngredient
        addIngredientsObservableList.add(DisplayIngredients(selectedIngredientName, amount, unit))

        ingredientComboBox.selectionModel.clearSelection()
        amountField.clear()
        unitComboBox.selectionModel.clearSelection()
        error.isVisible = false
    }

    fun getNewRecipe(): Recipe? {
        val name = nameField.text
        val timeToCook = timeToCookField.text
        val description = descriptionArea.text
        val instructions = instructionArea.text

        if (name.isBlank() || timeToCook.isBlank() || recipeIngredients.isEmpty() || instructions.isBlank() || description.isBlank()) {
            showError("Please fill out all recipe fields!")
            return null
        }

        error.isVisible = false
        return Recipe(name, timeToCook, description, recipeIngredients, instructions)
    }

    private fun showError(message: String) {
        error.message = message
        error.graphic = FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        error.isVisible = true
    }
}