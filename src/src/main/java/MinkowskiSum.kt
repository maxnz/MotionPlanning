import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.plusAssign
import kotlin.math.cos
import kotlin.math.sin

class MinkowskiSum(angle: Double, ladderLength: Double) {

    private val lines = mutableListOf<Line>()
    private val ladder = javafx.scene.shape.Line().apply {
        startX = 100.0
        endX = 100.0 * cos(angle) + 100.0

        startY = 100.0
        endY = 100.0 * sin(angle) + 100.0

        stroke = Color.BLUE
        strokeWidth = 3.0
    }
    private val offsetX = (ladderLength * cos(angle))
    private val offsetY = (ladderLength * sin(angle))

    fun addToSum(shape: Shape) {

        val points = mutableListOf<Pair<Double, Double>>()
        for (line in shape.lines) {
            points.add(Pair(line.p1.first - offsetX, line.p1.second - offsetY))
            lines.add(
                Line(
                    Pair(line.p1.first, line.p1.second),
                    Pair(line.p1.first - offsetX, line.p1.second - offsetY)
                )
            )
        }

        for (index in 0 until points.size) {
            lines.add(
                Line(
                    Pair(points[index].first, points[index].second),
                    Pair(points[(index + 1) % points.size].first, points[(index + 1) % points.size].second)
                )
            )
        }

        val lines2 = lines.toMutableList()
        for (line in lines2) {
            if (!withinBoundaries(line)) lines.remove(line)
        }
    }

    private fun withinBoundaries(line: Line): Boolean {
        return (line.p1.first in 0.0..500.0 &&
                line.p1.first != 0.0 &&
                line.p1.first != 500.0
                && line.p1.second in 0.0..500.0 && line.p1.second != 0.0 && line.p1.second != 500.0) ||
                (line.p2.first in 0.0..500.0 && line.p2.first != 0.0 && line.p2.first != 500.0
                        && line.p2.second in 0.0..500.0 && line.p2.second != 0.0 && line.p2.second != 500.0)
    }

    fun show(pane: Pane) {
        pane.apply {
            lines.forEach {
                it.draw(pane, color = Color.ORANGE)
            }
            this += ladder
        }
    }

    fun hide(pane: Pane) {
        for (line in lines)
            if (pane.children.contains(line.myLine)) pane.children.remove(line.myLine)
        if (pane.children.contains(ladder)) pane.children.remove(ladder)
    }

}

