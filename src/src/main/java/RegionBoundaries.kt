import com.fxgraph.cells.AbstractCell
import com.fxgraph.graph.Graph
import graph.CircleCell
import graph.CircleLayout
import graph.Edge
import graph.RectangleCell
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

    private val boundaryLines = mutableListOf<Line>()
    private val adjacentBoundaries = mutableListOf<Pair<Int, Int>>()
    private val circles = mutableListOf<Circle>()
    private val labels = mutableListOf<Label>()
    private val showCircles = false
    private val showLabels = true
    private lateinit var graph: Graph
    var myColor = Color.WHITE

    var angle: Double = 0.0

    private val anchorPoint: Pair<Double, Double>
        get() = when (angle) {
            0.0 -> Pair(0.0, 0.0)
            in 0.0..(PI / 2) -> Pair(500.0, 0.0)
            in (PI / 2)..PI -> Pair(500.0, 500.0)
            in PI..(3 * PI / 2) -> Pair(0.0, 500.0)
            in (3 * PI / 2)..(2 * PI) -> Pair(0.0, 0.0)
            else -> Pair(0.0, 0.0)
        }

    private val regions = mutableListOf<Region>()

    private fun Pair<Double, Double>.anchorDist() =
        ((anchorPoint dist this) *
                cos((Line(anchorPoint, this).angle - (PI / 2) - angle).absoluteValue)).absoluteValue

    fun findRegionBoundaries(
        angle: Double,
        lines: List<Line>,
        invert: List<Pair<Double, Double>>,
        boundary: Shape,
        obstacles: List<Shape>
    ) {
        if (boundaryLines.isNotEmpty()) return
        this.angle = angle

        // Extension functions

        infix fun Pair<Double, Double>.outside(shape: Shape) =
            (first !in shape.vertices[0].first..shape.vertices[2].first ||
                    second !in shape.vertices[0].second..shape.vertices[2].second)

        val inversePoints = invert.toMutableList()

        when (angle) {
            0.0, PI / 2, PI, 3 * PI / 2 -> {
            }
            in 0.0..PI / 2 -> inversePoints += (Pair(0.0, 0.0))
            in (PI / 2)..PI -> inversePoints += (Pair(500.0, 0.0))
            in PI..(3 * PI / 2) -> inversePoints += (Pair(500.0, 500.0))
            in (3 * PI / 2)..(2 * PI) -> inversePoints += (Pair(0.0, 500.0))
        }

        val extraVerts = mutableListOf<Pair<Double, Double>>()

        for (shape in obstacles)
            if (shape !== boundary)
                for (line in shape.lines)
                    if (line in lines)
                        for (bline in boundary.lines)
                            if (line != bline && line intersects bline) {
                                val intersection = line intersection bline
                                if (intersection != null) extraVerts += intersection
                            }

        extraVerts.forEach { v ->
            boundaryLines.add(
                Line(Pair(v.first, v.second), Pair(v.first, v.second))
                    .apply {
                        circles += Circle().apply {
                            radius = 5.0
                            fill = Color.GREEN
                            centerX = p2.first
                            centerY = p2.second
                        }
                    }
            )
        }


        val vertices = lines.vertices()
            .filter { it !in boundary.vertices }
            .sortedBy { it.anchorDist() } + lines.vertices()
            .filter { it in boundary.vertices }.filter {
                var inShape = false
                obstacles.forEach { ob -> if (ob.checkPointInShape(it)) inShape = true }
                !inShape
            }
            .sortedBy { it.anchorDist() }


        val anchorDists = mutableListOf<Double>()

        val maxList = listOf(0.0, 500.0)
        val a = (PI / 2) + angle

        for (v in vertices) {
            anchorDists += v.anchorDist()
        }

        d@ for (d in anchorDists.groupingBy { it.round(2) }.eachCount()) {
            if (d.value > 1) {
                for (b in boundary.vertices)
                    if (b.anchorDist().round(2) == d.key.round(2) && d.value == 2) continue@d
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
                        Line(Pair(v.first, v.second), Pair(v.first, v.second))
                            .apply {
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
                v outside boundary || boundaryLines.contains(v) -> continue@v
                inversePoints.contains(v) -> multiplier = -1
            }

            boundaryLines.add(
                Line(
                    Pair(v.first, v.second),
                    Pair(
                        v.first - (multiplier * 1000.0) * cos(angle),
                        v.second - (multiplier * 1000.0) * sin(angle)
                    )
                ).apply line@{
                    p2 = mutableListOf<Pair<Double, Double>>()
                        .apply { lines.forEach { if (this@line intersects it) add((this@line intersection it)!!) } }
                        .minBy { it dist p1 } ?: p2

                    trim()

                    circles += Circle().apply {
                        radius = 4.0
                        fill = Color.BLUE
                        centerX = p1.first
                        centerY = p1.second
                    }
                    circles += Circle().apply {
                        radius = 6.0
                        fill = Color.GREEN
                        centerX = p2.first
                        centerY = p2.second
                    }
                }
            )
        }
        boundaryLines.sortBy { it.p1.anchorDist() }
        var b = 10
        boundaryLines.forEach { it.setID(b++); it.showID = false }

        infix fun Double.between(line: Line) =
            this in if (line.p1.first < line.p2.first) line.p1.first..line.p2.first else line.p2.first..line.p1.first

        boundaryLines.forEach {
            line@ for (line in boundaryLines) {
                when {
                    it === line || Pair(it.myID, line.myID) in adjacentBoundaries ||
                            Pair(line.myID, it.myID) in adjacentBoundaries -> continue@line
                    it.intercept != null && it.intercept.round(1) == line.intercept?.round(1) &&
                            (it.p1.first between line || it.p2.first between line ||
                                    line.p1.first between it || line.p2.first between it)
                    -> if (it.myID < line.myID)
                        adjacentBoundaries.add(Pair(it.myID, line.myID))
                    else
                        adjacentBoundaries.add(Pair(line.myID, it.myID))
                }
            }
        }

    }

    fun findRegions(lines: List<Line>) {
        regions.clear()

        val endPoints = mutableMapOf<Line, MutableList<Pair<Int, Int>>>()

        boundaryLines.forEach {

            val p1ints = (it.p1 on lines).filter { it.myID != 0 }
            val p2ints = (it.p2 on lines).filter { it.myID != 0 }

            val linePairs = mutableListOf<Pair<Int, Int>>()
            p1ints.forEach { ln1 ->
                val i1 = ln1.myID
                p2ints.forEach { ln2 ->
                    val i2 = ln2.myID
                    if (i1 != i2 && Pair(i1, i2) !in linePairs && Pair(i2, i1) !in linePairs)
                        linePairs += if (i1 < i2) Pair(i1, i2) else Pair(i2, i1)
                }
            }
//            if (linePairs.isEmpty()) throw Exception()
            if (linePairs.isEmpty()) {
//                it.weight = 5.0
            }

            endPoints[it] = linePairs
        }

        fun MutableList<Region>.contains(id: Pair<Int, Int>): Boolean {
            this.forEach {
                if (it.regionID == id || it.regionID == Pair(id.second, id.first)) return true
            }
            return false
        }

        fun MutableList<Region>.containsSimilar(id: Pair<Int, Int>, line1: Line, line2: Line): Boolean {
            this.forEach {
                if ((it.regionID == id || it.regionID == Pair(id.second, id.first)) &&
                    (it.boundary1.myID == line1.myID || it.boundary2.myID == line2.myID ||
                            it.boundary1.myID == line2.myID || it.boundary2.myID == line1.myID)
                ) return true
            }
            return false
        }

        fun MutableList<Region>.find(id: Pair<Int, Int>): List<Region> {
            val ret = mutableListOf<Region>()
            this.forEach {
                if (it.regionID == id || it.regionID == Pair(id.second, id.first)) ret += it
            }
            return ret
        }

        for (line in endPoints) {
            for (ending in line.value)
                for (target in endPoints) {
                    if (ending in target.value && line != target) {
                        if (!regions.contains(ending)) {
                            regions += Region(ending, line.key, target.key)
                        } else if (!regions.containsSimilar(ending, line.key, target.key)) {
                            var a = 1
                            regions.find(ending).forEach { it.subID = a++ }
                            regions += Region(ending, line.key, target.key).apply { subID = a }
                        }
                    }
                }
        }
    }

    fun createGraph(critAngle: Boolean = true, dryRun: Boolean = false) {

        infix fun Region.nextTo(region: Region) = (boundary1.myID == region.boundary1.myID ||
                boundary1.myID == region.boundary2.myID ||
                boundary2.myID == region.boundary1.myID ||
                boundary2.myID == region.boundary2.myID ||
                Pair(boundary1.myID, region.boundary1.myID) in adjacentBoundaries ||
                Pair(boundary1.myID, region.boundary2.myID) in adjacentBoundaries ||
                Pair(boundary2.myID, region.boundary1.myID) in adjacentBoundaries ||
                Pair(boundary2.myID, region.boundary2.myID) in adjacentBoundaries)

        val nodes = mutableMapOf<Region, AbstractCell>()
        val adjacencyList = mutableListOf<Pair<Region, Region>>()
        val edges = mutableListOf<Edge>()

        val color = colors[z++ % colors.size]
        myColor = color
        regions.forEach {
            nodes += it to
                    if (critAngle) RectangleCell().apply {
                        labelText = it.id
                        fill = color
                        stroke = color
                    }
                    else CircleCell().apply {
                        labelText = it.id
                        fill = color
                        stroke = color
                    }
        }

        regions.forEach { r1 ->
            r2@ for (r2 in regions) {
                when {
                    r1 == r2 || Pair(r1, r2) in adjacencyList || Pair(r2, r1) in adjacencyList -> continue@r2
                    r1 nextTo r2 -> adjacencyList += Pair(r1, r2)
                }
            }
        }

        adjacencyList.forEach {
            edges += Edge(nodes[it.first]!!, nodes[it.second]!!).apply {
                this.color = Color.BLUE
                width = 2.0
            }
        }
        if (!dryRun) {
            graph = Graph().apply {
                beginUpdate()
                nodes.forEach { model.addCell(it.value) }
                edges.forEach { model.addEdge(it) }
                endUpdate()
                layout(CircleLayout())
            }

            masterGraph.apply {
                nodes.forEach { model.addCell(it.value) }
                edges.forEach { model.addEdge(it) }
                endUpdate()
                val group = model.allCells.toMutableList()
                groups.forEach {
                    group.removeAll(it)
                }
                groups.add(group)
            }
        }
    }

    fun show(pane: Pane, graphPane: Pane) {
        showBoundaries(pane)
        showGraph(graphPane)
    }

    fun showBoundaries(pane: Pane) {
        boundaryLines.forEach { it.draw(pane, weight = 1.0, color = Color.RED) }
        if (showCircles) circles.forEach { if (!pane.children.contains(it)) pane += it }
        if (showLabels) labels.forEach { if (!pane.children.contains(it)) pane += it }
    }

    fun showGraph(graphPane: Pane) {
        if (!this::graph.isInitialized) createGraph()
        graphPane += (graph.canvas)
    }


    fun hide(pane: Pane, graphPane: Pane) {
        hideBoundaries(pane)
        hideGraph(graphPane)
    }

    fun hideBoundaries(pane: Pane) {
        boundaryLines.forEach { it.hide(pane) }
        circles.forEach { if (pane.children.contains(it)) pane.children.remove(it) }
        labels.forEach { if (pane.children.contains(it)) pane.children.remove(it) }
    }

    fun hideGraph(graphPane: Pane) {
        if (!this::graph.isInitialized) return
        if (graphPane.children.contains(graph.canvas)) graphPane.children.remove(graph.canvas)
    }

}
