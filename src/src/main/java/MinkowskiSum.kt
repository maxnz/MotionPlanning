import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import tornadofx.plusAssign
import kotlin.math.*

class MinkowskiSum(val angle: Double, ladderLength: Double) : Shape() {

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

    private val sumShapes = mutableListOf<Shape>()

    private val sumLines = mutableListOf<Line>()
    private val cellLines = mutableListOf<Line>()

    private val inversePoints = mutableListOf<Pair<Double, Double>>()

    private val polygons = mutableListOf<Polygon>()

    fun addToSum(shape: Shape) {

        val points = mutableListOf<Pair<Double, Double>>()
        for (line in shape.lines) {
            points.add(Pair(line.p1.first - offsetX, line.p1.second - offsetY))
        }
        for (v in shape.vertices) {
            if (!points.contains(v)) points.add(Pair(v.first, v.second))
        }
        inversePoints += shape.vertices
        val mShape = Shape(points)
        var verts = doubleArrayOf()
        for (v in mShape.vertices) {
            if (!verts.contains(v.first)) verts += v.first
            if (!verts.contains(v.second)) verts += v.second
        }
        val p = Polygon().apply {
            this.points.addAll(verts.toTypedArray())
        }
        polygons.add(p)
        sumShapes.add(mShape)
    }

    fun addBoundaryToSum(boundary: Shape) {
        val points = mutableListOf<Pair<Double, Double>>()
        for (line in boundary.lines) {
            points.add(Pair(line.p1.first - offsetX, line.p1.second - offsetY))
        }

        for (p in 0 until points.size) {
            if (points[p].first < 0.0) points[p] = Pair(0.0, points[p].second)
            if (points[p].first > 500.0) points[p] = Pair(500.0, points[p].second)
            if (points[p].second < 0.0) points[p] = Pair(points[p].first, 0.0)
            if (points[p].second > 500.0) points[p] = Pair(points[p].first, 500.0)
        }

        

        sumShapes.add(Shape(points))
    }

    private fun createLines() {
        if (sumLines.isNotEmpty()) sumLines.clear()

        sumShapes.forEach {
            it.addLines()
            sumLines += it.lines
        }

        val lines2 = sumLines.toMutableList()
        for (line in lines2) {
            if (!withinBoundaries(line)) sumLines.remove(line)
        }
        for (line in sumLines) {
            line.trim()
            if (!vertices.contains(line.p1)) vertices.add(line.p1)
            if (!vertices.contains(line.p2)) vertices.add(line.p2)
        }

        addRegionBoundaries()
    }

    private fun withinBoundaries(line: Line): Boolean {

        val within = (line.p1.first in 0.0..500.0 && line.p1.second in 0.0..500.0)
                || (line.p2.first in 0.0..500.0 && line.p2.second in 0.0..500.0)

        if (!within) {
            for (b in borderShape.lines) {
                if (b.intersects(line)) return true
            }
        }
        return within
    }

    infix fun Pair<Double, Double>.outside(maximums: Pair<Double, Double>): Boolean {
        return !(this.first in 0.0..maximums.first && this.second in 0.0..maximums.second)
    }

    override fun show(pane: Pane) {
        createLines()
        pane.apply {
            if (minkowskiToggle.isSelected)
                sumLines.forEach {
                    if (regionToggle.isSelected)
                        it.draw(pane, weight = 3.0, color = Color.ORANGE)
                    else
                        it.draw(pane, color = Color.ORANGE)
                }
            if (regionToggle.isSelected) {
                cellLines.forEach {
                    it.draw(pane, weight = 2.0, color = Color.RED)
                }
                polygons.forEach {
                    it.stroke = Color.ORANGE
                    it.strokeWidth = 3.0
                    it.fill = Color.LIGHTGRAY
                    pane += it
                }
            }
            this += ladder
        }
    }

    override fun hide(pane: Pane) {
        for (line in sumLines)
            if (pane.children.contains(line.myLine)) pane.children.remove(line.myLine)
        for (line in cellLines)
            if (pane.children.contains(line.myLine)) pane.children.remove(line.myLine)
        for (polygon in polygons)
            if (pane.children.contains(polygon)) pane.children.remove(polygon)
        if (pane.children.contains(ladder)) pane.children.remove(ladder)
    }


    private fun addRegionBoundaries() {
        v@ for (v in vertices) {
            var multiplier = 1
            when {
                (v.first == 0.0 || v.first == 500.0) && v.second != 0.0 && v.second != 500.0 ||
                        (v.second == 0.0 || v.second == 500.0) && v.first != 0.0 && v.first != 500.0 ->
                    continue@v
                inversePoints.contains(v) -> multiplier = -1
            }
            val line = Line(
                Pair(v.first, v.second),
                Pair(v.first - (multiplier * 1000.0) * cos(angle), v.second - (multiplier * 1000.0) * sin(angle))
            )

            val intersections = mutableListOf<Pair<Double, Double>>()

            for (l in sumLines) {
                if (line.intersects(l)) intersections.add(line.intersection(l)!!)
            }

            fun length(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double =
                sqrt((p1.first - p2.first).absoluteValue.pow(2) + (p1.second - p2.second).absoluteValue.pow(2))

            var min = line.p2
            for (i in intersections) {
                if (length(line.p1, i) < length(line.p1, min)) min = i
            }

            line.p2 = min
            line.trim()

            cellLines.add(line)
        }
    }
}

