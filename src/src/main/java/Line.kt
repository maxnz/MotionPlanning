import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.plusAssign
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan

data class Line(
    var p1: Pair<Double, Double>,
    var p2: Pair<Double, Double>
) {

    val slope: Double? = if (p1.first == p2.first) null else (p2.second - p1.second) / (p2.first - p1.first)
    val angle: Double
    val intercept = if (slope != null) p1.second - slope * p1.first else null
    var myLine = javafx.scene.shape.Line().apply {
        startX = p1.first
        endX = p2.first

        startY = p1.second
        endY = p2.second
    }
    var myID = 0
    lateinit var myLabel: Label

    init {
        val a = if (slope != null) atan(slope) else null
        angle = if (a == null) PI / 2 else if (a < 0) a + PI else a
        trim()
    }

    var weight2 = 1.0

    fun draw(pane: Pane, weight: Double = 1.0, color: Color = Color.BLACK) {
        if (pane.children.contains(myLine)) return
        pane.apply {
            myLine.apply {
                stroke = color
                strokeWidth = if (weight2 != 1.0) weight2 else weight
            }
            this += myLine
            if (myID != 0) {
                myLabel = Label("$myID").apply {
                    when (angle) {
                        in (PI / 4)..(3 * PI / 4), in (5 * PI / 4)..(7 * PI / 4) -> {
                            layoutX = midpoint().first + 5.0
                            layoutY = midpoint().second - 10.0
                        }
                        else -> {
                            layoutX = midpoint().first - 10.0
                            layoutY = midpoint().second + 5.0
                        }
                    }
                }
                this += myLabel
            }
        }
    }

    fun removeLabel(pane: Pane) {
        if (this::myLabel.isInitialized && pane.children.contains(myLabel)) pane.children.remove(myLabel)
    }

    fun print() {
        println("   p1: $p1")
        println("   p2: $p2")
        println("   slope: $slope")
        println("   intercept: $intercept")
    }

    infix fun intersects(line: Line) = when {
        p1 == line.p1 && p2 == line.p2 || p1 == line.p2 && p2 == line.p1 -> true
        p1 == line.p1 || p1 == line.p2 || p2 == line.p1 || p2 == line.p2 -> false
        slope == line.slope -> false
        slope == null ->
            p1.first in line.p1.first..line.p2.first || p1.first in line.p2.first..line.p1.first
        line.slope == null ->
            line.p1.first in p1.first..p2.first || line.p1.first in p2.first..p1.first
        else -> {
            val x = (line.intercept!! - intercept!!) / (slope - line.slope)
            (x in p1.first..p2.first || x in p2.first..p1.first) &&
                    (x in line.p1.first..line.p2.first || x in line.p2.first..line.p1.first)
        }
    }

    fun intersects(point: Pair<Double, Double>): Boolean =
        if (slope == null) truncate(point.first) == truncate(p1.first)
        else truncate(truncate(point.first) * slope + intercept!!) == truncate(point.second)

    infix fun intersection(line: Line): Pair<Double, Double>? {
        return when {
            !intersects(line) -> null
            slope == null -> {
                val x = p1.first
                val y = x * line.slope!! + line.intercept!!
                Pair(x, y)
            }
            line.slope == null -> {
                val x = line.p1.first
                val y = x * slope + intercept!!
                Pair(x, y)
            }
            else -> {
                val x = (line.intercept!! - intercept!!) / (slope - line.slope)
                val y = x * slope + intercept
                Pair(x, y)
            }
        }
    }

    fun Pair<Double, Double>.outside(xMin: Double, xMax: Double, yMin: Double, yMax: Double): Boolean {
        return !(this.first in xMin..xMax && this.second in yMin..yMax)
    }

    fun trim(xmin: Double? = 0.0, xmax: Double? = 500.0, ymin: Double? = 0.0, ymax: Double? = 500.0) {
        val xMin = xmin ?: borderShape.vertices[0].first
        val xMax = xmax ?: borderShape.vertices[2].first
        val yMin = ymin ?: borderShape.vertices[0].second
        val yMax = ymax ?: borderShape.vertices[2].second
        var i = 0
        while ((p1.outside(xMin, xMax, yMin, yMax) || p2.outside(xMin, xMax, yMin, yMax)) && i < 5) {
            when {
                p1.first < xMin && slope != null -> p1 = Pair(xMin, xMin * slope + intercept!!)
                p1.first > xMax && slope != null -> p1 = Pair(xMax, xMax * slope + intercept!!)
                p2.first < xMin && slope != null -> p2 = Pair(xMin, xMin * slope + intercept!!)
                p2.first > xMax && slope != null -> p2 = Pair(xMax, xMax * slope + intercept!!)
                p1.second < yMin -> p1 =
                    if (slope == null) Pair(p1.first, yMin) else Pair((yMin - intercept!!) / slope, yMin)
                p1.second > yMax -> p1 =
                    if (slope == null) Pair(p1.first, yMax) else Pair((yMax - intercept!!) / slope, yMax)
                p2.second < yMin -> p2 =
                    if (slope == null) Pair(p2.first, yMin) else Pair((yMin - intercept!!) / slope, yMin)
                p2.second > yMax -> p2 =
                    if (slope == null) Pair(p2.first, yMax) else Pair((yMax - intercept!!) / slope, yMax)
            }
            i++
        }
        myLine.apply {
            startX = p1.first
            endX = p2.first

            startY = p1.second
            endY = p2.second
        }
    }

    fun midpoint(): Pair<Double, Double> = Pair(
        ((p2.first - p1.first).absoluteValue / 2) + listOf(p1, p2).minBy { it.first }!!.first,
        ((p2.second - p1.second).absoluteValue / 2) + listOf(p1, p2).minBy { it.second }!!.second
    )
}
