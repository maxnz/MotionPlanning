

class RegionBoundary(val intercept: Double?) {

    val lines = mutableListOf<Line>()


    override fun toString(): String {
        return "$intercept: $lines"
    }
}