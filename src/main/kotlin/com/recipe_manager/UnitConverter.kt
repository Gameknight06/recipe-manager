package com.recipe_manager

object UnitConverter {
    private enum class UnitCategory {
        VOLUME, WEIGHT
    }

    private data class UnitInfo(
        val category: UnitCategory,
        val toBaseFactor: Double
    )

    // Base units: Milliliter for Volume, Gram for Weight
    private val unitMap: Map<String, UnitInfo> = mapOf(
        // Volume
        "Cup" to UnitInfo(UnitCategory.VOLUME, 236.588),
        "Tablespoon" to UnitInfo(UnitCategory.VOLUME, 14.7868),
        "Teaspoon" to UnitInfo(UnitCategory.VOLUME, 4.92892),
        "Fluid Ounce" to UnitInfo(UnitCategory.VOLUME, 29.5735),
        "Quart" to UnitInfo(UnitCategory.VOLUME, 946.353),
        "Liter" to UnitInfo(UnitCategory.VOLUME, 1000.0),
        "Milliliter" to UnitInfo(UnitCategory.VOLUME, 1.0),

        // Weight
        "Pound" to UnitInfo(UnitCategory.WEIGHT, 453.592),
        "Ounce" to UnitInfo(UnitCategory.WEIGHT, 28.3495),
        "Gram" to UnitInfo(UnitCategory.WEIGHT, 1.0),
        "Kilogram" to UnitInfo(UnitCategory.WEIGHT, 1000.0),
        "Milligram" to UnitInfo(UnitCategory.WEIGHT, 0.001),
    )

    val allUnits: List<String> = unitMap.keys.sorted()

    /**
     * Converts an amount from one unit to another.
     * @param fromUnit The unit to convert from (e.g., "Cup").
     * @param toUnit The unit to convert to (e.g., "Milliliter").
     * @param amount The amount to convert.
     * @return The converted amount, or null if the units are invalid or belong to different categories.
     */
    fun convert(fromUnit: String, toUnit: String, amount: Double): Double? {
        val from = unitMap[fromUnit]
        val to = unitMap[toUnit]

        if (from == null || to == null) {
            println("Invalid unit: $fromUnit or $toUnit")
            return null
        }

        if (fromUnit == toUnit) return amount

        if (from.category != to.category) {
            println("Cannot convert from $fromUnit to $toUnit")
            return null
        }

        val amountInBaseUnit = amount * from.toBaseFactor
        return amountInBaseUnit / to.toBaseFactor
    }
}