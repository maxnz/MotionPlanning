
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.circle
import tornadofx.plusAssign
import kotlin.math.PI
import kotlin.math.atan

data class Line(
    val p1: Pair<Double, Double>,
    val p2: Pair<Double, Double>
) {

    val slope: Double = if (p1.first == p2.first) PI / 2 else (p2.second - p1.second) / (p2.first - p1.first)
    val angle: Double
    val intercept = p1.second - slope * p1.first
    var myLine = javafx.scene.shape.Line().apply {
        startX = p1.first
        endX = p2.first

        startY = p1.second
        endY = p2.second
    }

    init {
        val a = atan(slope)
        angle = if (a < 0) a + PI else a
    }


    fun draw(pane: Pane, weight: Double = 1.0, color: Color = Color.BLACK) {
        if (pane.children.contains(myLine)) return
        pane.apply {
            myLine.apply {
                stroke = color
                strokeWidth = weight
            }
            this += myLine
        }
    }


    fun print() {
        println("   p1: $p1")
        println("   p2: $p2")
        println("   slope: $slope")
        println("   intercept: $intercept")
    }

    fun intersects(line: Line, pane: Pane? = null): Boolean {
        if (p1 == line.p1 && p2 == line.p2 || p1 == line.p2 && p2 == line.p1) return true
        else if (p1 == line.p1 || p1 == line.p2 || p2 == line.p1 || p2 == line.p2) return false

        if (slope == line.slope) return false
        val x = (line.intercept - intercept) / (slope - line.slope)

        val y = x * slope + intercept
        val intersect =
            (x in p1.first..p2.first || x in p2.first..p1.first) &&
                    (x in line.p1.first..line.p2.first || x in line.p2.first..line.p1.first)
        if (x in 0.0..500.0 && y in 0.0..500.0 && intersect)
            pane?.apply {
                circle {
                    centerX = x
                    centerY = y
                    radius = 10.0
                }
            }
        return intersect
    }
}
