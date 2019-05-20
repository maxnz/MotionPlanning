class Boundary(topLeft: Pair<Double, Double>,
               topRight: Pair<Double, Double>,
               bottomRight: Pair<Double, Double>,
               bottomLeft: Pair<Double, Double>) : Shape() {

    val top = Line(topLeft, topRight)
    val right = Line(topRight, bottomRight)
    val bottom = Line(bottomRight, bottomLeft)
    val left = Line(bottomLeft, topLeft)

    init {
        vertices.add(topLeft)
        vertices.add(topRight)
        vertices.add(bottomRight)
        vertices.add(bottomLeft)
        addLines()
    }

    fun addLines(reset: Boolean = false) {
        if (lines.isNotEmpty() && reset) lines.clear()
        lines.add(top)
        lines.add(right)
        lines.add(bottom)
        lines.add(left)
    }

    fun toShape() = Shape(vertices)
}