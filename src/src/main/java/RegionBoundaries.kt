import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.plusAssign
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class RegionBoundaries {

    val boundaryLines = mutableListOf<Line>()
    val circles = mutableListOf<Circle>()
    val labels = mutableListOf<Label>()

    var angle: Double = 0.0

    val anchorPoint: Pair<Double, Double>
        get() = when (angle) {
            0.0 -> Pair(0.0, 0.0)
            in 0.0..(PI / 2) -> Pair(500.0, 0.0)
            in (PI / 2)..PI -> Pair(500.0, 500.0)
            in PI..(3 * PI / 2) -> Pair(0.0, 500.0)
            in (3 * PI / 2)..(2 * PI) -> Pair(0.0, 0.0)
            else -> Pair(0.0, 0.0)
        }

    private fun Pair<Double, Double>.anchorDist(): Double =
        ((anchorPoint dist this) * cos(
            (Line(
                anchorPoint,
                this
            ).angle - (PI / 2) - angle).absoluteValue
        )).absoluteValue

    fun findRegionBoundaries(angle: Double, lines: List<Line>, invert: List<Pair<Double, Double>>, boundary: Shape) {
        this.angle = angle

        // Extension functions

        infix fun Pair<Double, Double>.outside(shape: Shape): Boolean {
            return (first !in shape.vertices[0].first..shape.vertices[2].first ||
                    second !in shape.vertices[0].second..shape.vertices[2].second)
        }

        val inversePoints = invert.toMutableList()

        when (angle) {
            0.0, PI / 2, PI, 3 * PI / 2 -> {
            }
            in 0.0..PI / 2 -> inversePoints += (Pair(0.0, 0.0))
            in (PI / 2)..PI -> inversePoints += (Pair(500.0, 0.0))
            in PI..(3 * PI / 2) -> inversePoints += (Pair(500.0, 500.0))
            in (3 * PI / 2)..(2 * PI) -> inversePoints += (Pair(0.0, 500.0))
        }

        val vertices = lines.vertices().sortedBy { it.anchorDist() }
        val anchorDists = mutableListOf<Double>()

        val maxList = listOf(0.0, 500.0)
        val a = (PI / 2) + angle

        for (v in vertices) {
            anchorDists += v.anchorDist()
        }

        for (d in anchorDists.groupingBy { truncate(it) }.eachCount()) {
            if (d.value > 1) {
                for (b in boundary.vertices)
                    if (truncate(b.anchorDist()) == d.key && d.value > 2) continue
                val x = Pair(
                    anchorPoint.first + (cos(a) * d.key),
                    anchorPoint.second + (sin(a) * d.key)
                )
                boundaryLines.add(
                    Line(
                        Pair(x.first + 500.0 * cos(angle), x.second + 500.0 * sin(angle)),
                        Pair(x.first - 500.0 * cos(angle), x.second - 500.0 * sin(angle))
                    ).apply {
                        trim(
                            boundary.vertices[0].first, boundary.vertices[2].first,
                            boundary.vertices[0].second, boundary.vertices[2].second
                        )
                        circles += Circle().apply {
                            radius = 8.0
                            fill = Color.RED
                            centerX = p1.first
                            centerY = p1.second
                        }
                        circles += Circle().apply {
                            radius = 8.0
                            fill = Color.ORANGE
                            centerX = p2.first
                            centerY = p2.second
                        }

                    }
                )
            }
        }

        v@ for (v in vertices) {
            var multiplier = 1
            when {
                (v.first in maxList && v.second !in maxList) ||
                        (v.second in maxList && v.first !in maxList) -> {
                    boundaryLines.add(
                        Line(
                            Pair(v.first, v.second),
                            Pair(v.first, v.second)
                        ).apply {
                            circles += Circle().apply {
                                radius = 5.0
                                fill = Color.GREEN
                                centerX = p2.first
                                centerY = p2.second
                            }
                        }
                    )
                    continue@v
                }
                v outside boundary -> continue@v
                inversePoints.contains(v) -> multiplier = -1
            }

            boundaryLines.add(
                Line(
                    Pair(v.first, v.second),
                    Pair(v.first - (multiplier * 1000.0) * cos(angle), v.second - (multiplier * 1000.0) * sin(angle))
                ).apply line@{
                    p2 =
                        mutableListOf<Pair<Double, Double>>()
                            .apply {
                                lines.forEach {
                                    if (this@line intersects it) add((this@line intersection it)!!)
                                }
                            }
                            .minBy { it dist p1 } ?: p2
                    trim()

                    circles += Circle().apply {
                        radius = 5.0
                        fill = Color.BLUE
                        centerX = p1.first
                        centerY = p1.second
                    }
                    circles += Circle().apply {
                        radius = 5.0
                        fill = Color.GREEN
                        centerX = p2.first
                        centerY = p2.second
                    }
                }
            )
        }
        boundaryLines.sortBy { it.p1.anchorDist() }
    }

    fun findRegions(boundary: Shape, obstacles: List<Shape>) {

        val lines = boundary.lines +
                mutableListOf<Line>().apply {
                    obstacles.forEach {
                        this += it.lines
                    }
                }

        for (b in boundaryLines.sortedBy { it.p1.anchorDist() }.reversed()) {
            println("A: ${b.p2}")
            val l = lines.filter { b.p2 on it }.minBy { it.p1.anchorDist() } ?: continue
            println("B: ${l.myID}")

        }


    }

    fun show(pane: Pane) {
        boundaryLines.forEach {
            it.draw(pane, weight = 1.0, color = Color.RED)
        }
        circles.forEach {
            pane += it
        }
        labels.forEach {
            pane += it
        }

    }

    fun hide(pane: Pane) {
        boundaryLines.forEach {
            if (pane.children.contains(it.myLine)) pane.children.remove(it.myLine)
        }
        circles.forEach {
            if (pane.children.contains(it)) pane.children.remove(it)
        }
        labels.forEach {
            if (pane.children.contains(it)) pane.children.remove(it)
        }
    }
}
