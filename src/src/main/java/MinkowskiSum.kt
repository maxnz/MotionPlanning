import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import tornadofx.plusAssign
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


// TODO: Clean up
class MinkowskiSum(val angle: Double, ladderLength: Double) : Shape() {

    private val ladder = javafx.scene.shape.Line().apply {
        startX = 100.0 - cos(angle) * ladderLength / 2
        endX = 100.0 + cos(angle) * ladderLength / 2

        startY = 100.0 - sin(angle) * ladderLength / 2
        endY = 100.0 + sin(angle) * ladderLength / 2

        stroke = Color.BLUE
        strokeWidth = 3.0
    }
    private val ladderOrigin = Circle().apply {
        centerX = ladder.startX
        centerY = ladder.startY
        radius = 4.0
        fill = Color.BLUE
    }
    private val offsetX = (ladderLength * cos(angle))
    private val offsetY = (ladderLength * sin(angle))

    private val sumShapes = mutableListOf<Shape>()

    private var sumLines = mutableListOf<Line>()
    private lateinit var boundaryShape: Shape

    private val inversePoints = mutableListOf<Pair<Double, Double>>()

    private val polygons = mutableListOf<Polygon>()

    val regionBoundaries = RegionBoundaries()

    fun addToSum(shape: Shape) {

        val points = mutableListOf<Pair<Double, Double>>()
        shape.lines.forEach { line ->
            points += Pair(line.p1.first - offsetX, line.p1.second - offsetY)
        }
        shape.vertices.forEach { v ->
            if (v !in points) points += Pair(v.first, v.second)
        }

        val mShape = Shape(points).apply {
            vertices.forEach { v ->
                if (v in shape.vertices) inversePoints += v
            }
            addLines()
        }

        m@ for (m in mShape.lines)
            for (s in shape.lines)
                if (m.angle.round() == s.angle.round() &&
                    angle.round() != s.angle.round() &&
                    angle.round() != (s.angle + PI).round()
                ) {
                    m.setID(s.myID)
                    continue@m
                }

//        val polygonPoints = mutableListOf<Pair<Double, Double>>()
//        for (i in 0 until mShape.lines.size) {
//            val v = mShape.lines[i]
//            if (!polygonPoints.contains(v.p1)) polygonPoints.add(v.p1)
//            if (!polygonPoints.contains(v.p2)) polygonPoints.add(v.p2)
//            if (v.p2.first == 0.0 && mShape.lines[(i + 1) % mShape.lines.size].p1.second == 0.0)
//                polygonPoints.add(Pair(0.0, 0.0))
//            if (v.p2.second == 0.0 && mShape.lines[(i + 1) % mShape.lines.size].p1.first == 500.0)
//                polygonPoints.add(Pair(500.0, 0.0))
//            if (v.p2.first == 500.0 && mShape.lines[(i + 1) % mShape.lines.size].p1.second == 500.0)
//                polygonPoints.add(Pair(500.0, 500.0))
//            if (v.p2.second == 500.0 && mShape.lines[(i + 1) % mShape.lines.size].p1.first == 0.0)
//                polygonPoints.add(Pair(0.0, 500.0))
//        }
//        for (v in mShape.lines) {
//            if (!polygonPoints.contains(v.p1)) polygonPoints.add(v.p1)
//            if (!polygonPoints.contains(v.p2)) polygonPoints.add(v.p2)
//        }

//        var verts = doubleArrayOf()
//        for (v in polygonPoints) {
//            verts += v.first
//            verts += v.second
//        }
//        val p = Polygon().apply {
//            this.points.addAll(verts.toTypedArray())
//        }
//        polygons.add(p)
        sumShapes.add(mShape)
    }

    fun addBoundaryToSum(boundary: Shape) {
        val points = mutableListOf<Pair<Double, Double>>()
        boundary.lines.forEach { line ->
            points += Pair(line.p1.first - offsetX, line.p1.second - offsetY)
        }

        for (p in 0 until points.size) {
            if (points[p].first < 0.0) points[p] = Pair(0.0, points[p].second)
            if (points[p].first > 500.0) points[p] = Pair(500.0, points[p].second)
            if (points[p].second < 0.0) points[p] = Pair(points[p].first, 0.0)
            if (points[p].second > 500.0) points[p] = Pair(points[p].first, 500.0)
        }

        boundaryShape = Shape(points)

        fun MutableList<Pair<Double, Double>>.toTypedDoubleArray(): Array<Double> {
            var ret = doubleArrayOf()
            this.forEach {
                ret += it.first
                ret += it.second
            }
            return ret.toTypedArray()
        }

        val polygonPoints = mutableListOf<Pair<Double, Double>>()
        val includePoints = borderShape.vertices.toMutableList()
        points.forEach { p ->
            if (borderShape.vertices.contains(p)) includePoints.remove(p)
        }

        val indices = listOf(
            listOf(1, 2, 3, 1, 2, 3),
            listOf(0, 1, 2, 2, 3, 0),
            listOf(3, 0, 1, 3, 0, 1),
            listOf(0, 3, 2, 2, 1, 0)
        )

        when {
            Pair(0.0, 0.0) !in includePoints -> {
                indices[0].forEachIndexed { index, i ->
                    polygonPoints += if (index < 3) points[i] else borderShape.vertices[i]
                }
            }
            Pair(500.0, 0.0) !in includePoints -> {
                indices[1].forEachIndexed { index, i ->
                    polygonPoints += if (index < 3) points[i] else borderShape.vertices[i]
                }
            }
            Pair(500.0, 500.0) !in includePoints -> {
                indices[2].forEachIndexed { index, i ->
                    polygonPoints += if (index < 3) points[i] else borderShape.vertices[i]
                }
            }
            Pair(0.0, 500.0) !in includePoints -> {
                indices[3].forEachIndexed { index, i ->
                    polygonPoints += if (index < 3) points[i] else borderShape.vertices[i]
                }
            }
        }

        polygons += Polygon().apply {
            this.points.addAll(polygonPoints.toTypedDoubleArray())
        }
        sumShapes += Shape(points, 1)
    }

//    fun consolidate(lines: List<Line>, vertices: MutableList<Pair<Double, Double>>): List<Line> {
//        val combos = mutableListOf<Pair<Line, Line>>()
//        val ret = lines.toMutableList()
//        println(ret)
//        for (line in lines) {
//            l@ for (line2 in lines) {
//                val i = when {
//                    line === line2 -> continue@l
//                    line.p1 == line2.p1 -> 1
//                    line.p1 == line2.p2 -> 2
//                    line.p2 == line2.p1 -> 3
//                    line.p2 == line2.p2 -> 4
//                    else -> continue@l
//                }
//
//                if (truncate(line.angle) == truncate(line2.angle))
//                    if (Pair(line, line2) !in combos && Pair(line2, line) !in combos) {
//                        combos += Pair(line, line2)
//                        when (i) {
//                            1,2 -> vertices.remove()
//                            3,4 -> ret += Line(line.p2, line2.p2)
//                        }
//                        println("$line : $line2")
//                        ret.remove(line)
//                        ret.remove(line2)
//                    }
//            }
//        }
//        if (lines != ret) println(ret)
//        return ret
//    }

    private fun createLines(pane: Pane) {
        if (sumLines.isNotEmpty()) sumLines.clear()

        sumShapes.forEach {
            it.makeConvex()
            it.addLines()
//            println(it.lines)
//            consolidate(it.lines, it.vertices)
//            println(it.lines)
            sumLines.addAll(it.lines)
        }

        val lines2 = sumLines.toMutableList()
        for (line in lines2) {
            if (!withinBoundaries(line)) sumLines.remove(line)
        }
        for (line in sumLines) {
            line.trim(
                xmin = boundaryShape.vertices[0].first,
                xmax = boundaryShape.vertices[2].first,
                ymin = boundaryShape.vertices[0].second,
                ymax = boundaryShape.vertices[2].second
            )
            if (!vertices.contains(line.p1)) vertices.add(line.p1)
            if (!vertices.contains(line.p2)) vertices.add(line.p2)
        }

        regionBoundaries.findRegionBoundaries(angle, sumLines, inversePoints, boundaryShape)
        regionBoundaries.findRegions(sumLines, pane)
    }

    private fun withinBoundaries(line: Line): Boolean {
        if (!this::boundaryShape.isInitialized) boundaryShape = borderShape.toShape()
        val within =
            (line.p1.first in boundaryShape.vertices[0].first..boundaryShape.vertices[2].first
                    && line.p1.second in boundaryShape.vertices[0].second..boundaryShape.vertices[2].second)
                    || (line.p2.first in boundaryShape.vertices[0].first..boundaryShape.vertices[2].first
                    && line.p2.second in boundaryShape.vertices[0].second..boundaryShape.vertices[2].second)
        boundaryShape.addLines()
        if (!within) {
            for (b in boundaryShape.lines) {
                if (b.intersects(line)) return true
            }
        }
        return within
    }

    override fun show(pane: Pane) = show(pane, null)

    fun show(pane: Pane, ladderPane: Pane? = null) {
        createLines(pane)
        pane.apply {
            if (minkowskiToggle.isSelected)
                showExclusionBoundary(pane)
            if (regionToggle.isSelected) {
                regionBoundaries.show(this)
//                polygons.forEach {
//                    it.stroke = Color.ORANGE
//                    it.strokeWidth = 3.0
//                    it.fill = Color.LIGHTGRAY
//                    pane += it
//                }
            }
        }
        ladderPane?.apply {
            showLadder(this)
        }
    }

    fun showExclusionBoundary(pane: Pane) {
        createLines(pane)
        pane.apply {
            if (regionToggle.isSelected)
                regionBoundaries.hide(pane)
            sumLines.forEach {
                it.draw(pane, weight = 3.0, color = Color.ORANGE)
            }
            if (regionToggle.isSelected)
                regionBoundaries.show(pane)
        }
    }

    fun showLadder(pane: Pane) {
        if (!pane.children.contains(ladderOrigin))
            pane += ladderOrigin
        if (!pane.children.contains(ladder))
            pane += ladder
    }

    override fun hide(pane: Pane) = hide(pane, null)

    fun hide(pane: Pane, ladderPane: Pane? = null) {
        hideExclusionBoundary(pane)
        regionBoundaries.hide(pane)

        for (polygon in polygons)
            if (pane.children.contains(polygon)) pane.children.remove(polygon)
        if (ladderPane != null) hideLadder(ladderPane)
    }

    fun hideExclusionBoundary(pane: Pane) {
        sumLines.forEach {
            it.hide(pane)
        }
    }

    fun hideLadder(pane: Pane) {
        if (pane.children.contains(ladder))
            pane.children.remove(ladder)
        if (pane.children.contains(ladderOrigin))
            pane.children.remove(ladderOrigin)
    }

}

