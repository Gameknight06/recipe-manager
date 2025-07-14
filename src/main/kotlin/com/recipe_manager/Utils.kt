package com.recipe_manager

import javafx.scene.control.TextFormatter
import java.util.function.UnaryOperator

val listOfUnits: MutableList<String> = mutableListOf("Cup(s)", "Tablespoon(s)", "Teaspoon(s)", "Ounce(s)", "Fluid Ounce(s)", "Pound(s)", "Quart(s)", "Liter(s)", "Milliliter(s)", "Milligram(s)", "Gram(s)", "Kilogram(s)")

fun createDecimalTextFormatter(): TextFormatter<String> {
    val validDecimalText ="\\d*\\.?\\d*".toRegex()

    val filter = UnaryOperator<TextFormatter.Change> { change ->
        val newText = change.controlNewText
        if (newText.matches(validDecimalText)) {
            change
        } else {
            null
        }
    }
    return TextFormatter(filter)
}

data class DisplayIngredients(val name: String, val amount: Double, val unit: String) {
    override fun toString(): String = "$amount $unit of $name"
}