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
        "Cup(s)" to UnitInfo(UnitCategory.VOLUME, 236.588),
        "Tablespoon(s)" to UnitInfo(UnitCategory.VOLUME, 14.7868),
        "Teaspoon(s)" to UnitInfo(UnitCategory.VOLUME, 4.92892),
        "Fluid Ounce(s)" to UnitInfo(UnitCategory.VOLUME, 29.5735),
        "Quart(s)" to UnitInfo(UnitCategory.VOLUME, 946.353),
        "Liter(s)" to UnitInfo(UnitCategory.VOLUME, 1000.0),
        "Milliliter(s)" to UnitInfo(UnitCategory.VOLUME, 1.0),

        // Weight
        "Pound(s)" to UnitInfo(UnitCategory.WEIGHT, 453.592),
        "Ounce(s)" to UnitInfo(UnitCategory.WEIGHT, 28.3495),
        "Gram(s)" to UnitInfo(UnitCategory.WEIGHT, 1.0),
        "Kilogram(s)" to UnitInfo(UnitCategory.WEIGHT, 1000.0),
        "Milligram(s)" to UnitInfo(UnitCategory.WEIGHT, 0.001),
    )

    val allUnits: List<String> = unitMap.keys.sorted()

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