import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.plusAssign
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class MinkowskiSum(val angle: Double, ladderLength: Double) : Shape() {

    // Ladder

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


    // Sum boundary offset

    private val offsetX = (ladderLength * cos(angle))
    private val offsetY = (ladderLength * sin(angle))


    // Other variables

    private val sumShapes = mutableListOf<Shape>()
    private var sumLines = mutableListOf<Line>()
    private val inversePoints = mutableListOf<Pair<Double, Double>>()
    val regionBoundaries = RegionBoundaries()
    val myColor: Color
        get() = regionBoundaries.myColor

    private lateinit var boundaryShape: Shape


    // Add to sum functions

    fun addToSum(shape: Shape) {
        val points = mutableListOf<Pair<Double, Double>>()

        shape.lines.forEach { points += Pair(it.p1.first - offsetX, it.p1.second - offsetY) }
        shape.vertices.forEach { if (it !in points) points += Pair(it.first, it.second) }

        val mShape = Shape(points).apply {
            vertices.forEach { if (it in shape.vertices) inversePoints += it }
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
        sumShapes.add(mShape)
    }

    fun addBoundaryToSum(boundary: Shape) {
        val points = mutableListOf<Pair<Double, Double>>()

        boundary.lines.forEach { points += Pair(it.p1.first - offsetX, it.p1.second - offsetY) }

        for (p in 0 until points.size) {
            if (points[p].first < 0.0) points[p] = Pair(0.0, points[p].second)
            if (points[p].first > 500.0) points[p] = Pair(500.0, points[p].second)
            if (points[p].second < 0.0) points[p] = Pair(points[p].first, 0.0)
            if (points[p].second > 500.0) points[p] = Pair(points[p].first, 500.0)
        }

        boundaryShape = Shape(points)
        sumShapes += Shape(points, 1)
    }


    // Show functions

    fun show(pane: Pane, ladderPane: Pane, graphPane: Pane) {
//        createLines()
        pane.apply {
            if (minkowskiToggle.isSelected) showExclusionBoundary(pane)
            if (regionToggle.isSelected) regionBoundaries.showBoundaries(this)
        }
        ladderPane.apply { showLadder(this) }
        regionBoundaries.showGraph(graphPane)
    }

    fun showExclusionBoundary(pane: Pane) {
        pane.apply {
            if (regionToggle.isSelected) regionBoundaries.hideBoundaries(pane)
            sumLines.forEach { it.draw(pane, weight = 3.0, color = Color.ORANGE) }
            if (regionToggle.isSelected) regionBoundaries.showBoundaries(pane)
        }
    }

    private fun showLadder(pane: Pane) {
        if (!pane.children.contains(ladderOrigin)) pane += ladderOrigin
        if (!pane.children.contains(ladder)) pane += ladder
    }


    // Hide functions

    fun hide(pane: Pane, ladderPane: Pane, graphPane: Pane) {
        hideExclusionBoundary(pane)
        regionBoundaries.hide(pane, graphPane)
        hideLadder(ladderPane)
    }

    fun hideExclusionBoundary(pane: Pane) {
        sumLines.forEach { it.hide(pane) }
    }

    private fun hideLadder(pane: Pane) {
        if (pane.children.contains(ladder)) pane.children.remove(ladder)
        if (pane.children.contains(ladderOrigin)) pane.children.remove(ladderOrigin)
    }


    // Helper functions

    fun createLines(reset: Boolean = false) {
        when {
            reset -> sumLines.clear()
            sumLines.isNotEmpty() -> return
        }

        sumShapes.forEach {
            it.makeConvex()
            it.addLines(trim = false)
            sumLines.addAll(it.lines)
        }

        val lines2 = sumLines.toMutableList()
        for (line in lines2) {
            if (!withinBoundaries(line)) sumLines.remove(line)
        }

//        for (line in sumLines.toMutableList()) if (!withinBoundaries(line)) sumLines.remove(line)

        regionBoundaries.findRegionBoundaries(angle, sumLines, inversePoints, boundaryShape, sumShapes)
        regionBoundaries.findRegions(sumLines)
        regionBoundaries.createGraph(angle in CritAngles.critAngles || angle - PI in CritAngles.critAngles)
    }

    private fun withinBoundaries(line: Line): Boolean {
        if (!this::boundaryShape.isInitialized) boundaryShape = borderShape.toShape()
        val bv = boundaryShape.vertices
        val within =
            (line.p1.first in bv[0].first..bv[2].first && line.p1.second in bv[0].second..bv[2].second) ||
                    (line.p2.first in bv[0].first..bv[2].first && line.p2.second in bv[0].second..bv[2].second)
        boundaryShape.addLines()
        if (!within) for (b in boundaryShape.lines) if (b.intersects(line)) return true
        return within
    }

}

