import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kotlin.math.cos
import kotlin.math.sin

object CritAngles {

    private val critAngleLines = mutableListOf<Line>()
    val critAngles = mutableListOf<Double>()


    private val connections = mutableListOf<Line>()


    private fun findConnections(obstacles: List<Shape>) {
        for (obstacle in obstacles)
            for (v1 in borderShape.vertices)
                for (v2 in obstacle.vertices) {
                    val line = Line(v1, v2)
                    var intersect = false
                    o@ for (o in obstacles)
                        for (l in o.lines)
                            if (line.intersects(l)) {
                                intersect = true
                                break@o
                            }
                    if (!intersect) connections.add(line)
                }
    }

    fun findCritAngles(obstacles: List<Shape>) {
        for (obstacle in obstacles) {
            obstacle.findCriticalAngles(critAngleLines)
        }
        findConnections(obstacles)

        for (line in critAngleLines) {
            if (!critAngles.contains(line.angle)) critAngles.add(line.angle)
        }

        connections.forEach {
            var line =
                Line(
                    Pair(100.0, 100.0),
                    Pair(25.0 * cos(it.angle) + 100.0, 25.0 * sin(it.angle) + 100.0)
                )
            critAngleLines.add(line)
            line = Line(
                Pair(100.0, 100.0),
                Pair(100.0 - 25.0 * cos(it.angle), 100.0 - 25.0 * sin(it.angle))
            )
            critAngleLines.add(line)
            critAngles.add(it.angle)
        }

        critAngles.sort()
    }

    fun showCritAngles(pane: Pane) {
        critAngleLines.forEach {
            it.draw(pane, color = Color.RED)
        }
    }

    fun hideCritAngles(pane: Pane) {
        critAngleLines.forEach {
            pane.children.remove(it.myLine)
        }
    }

    fun showConnections(pane: Pane) {
        connections.forEach {
            it.draw(pane, color = Color.GREEN)
        }
    }

    fun hideConnections(pane: Pane) {
        connections.forEach {
            pane.children.remove(it.myLine)
        }
    }
}