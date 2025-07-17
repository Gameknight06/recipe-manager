package com.recipe_manager

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory

class IngredientViewGUI {

    @FXML private lateinit var ingredientsTable: TableView<Ingredient>
    @FXML private lateinit var nameColumn: TableColumn<Ingredient, String>
    @FXML private lateinit var locationColumn: TableColumn<Ingredient, String>
    @FXML private lateinit var costColumn: TableColumn<Ingredient, Double>
    @FXML private lateinit var unitColumn: TableColumn<Ingredient, String>
    @FXML private lateinit var carbsColumn: TableColumn<Ingredient, Double>

    @FXML
    private fun initialize() {

        nameColumn.cellValueFactory = PropertyValueFactory("name")
        locationColumn.cellValueFactory = PropertyValueFactory("location")
        costColumn.cellValueFactory = PropertyValueFactory("cost")
        unitColumn.cellValueFactory = PropertyValueFactory("unitType")
        carbsColumn.cellValueFactory = PropertyValueFactory("carbs")

        loadAndDisplayIngredients()
    }

    private fun loadAndDisplayIngredients() {
        val ingredients = FileOperations.loadIngredients()
        ingredientsTable.items = FXCollections.observableArrayList(ingredients.values.toList())
    }
}