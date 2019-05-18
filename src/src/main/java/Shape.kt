import javafx.scene.layout.Pane
import kotlin.math.cos
import kotlin.math.sin


open class Shape() {

    var vertices = mutableListOf<Pair<Double, Double>>()
    var lines = mutableListOf<Line>()
    var startIDIndex = 0
    private var drawable = false

    constructor(startID: Int) : this() {
        startIDIndex = startID
    }

    constructor(v: List<Pair<Double, Double>>, startID: Int = 0) : this() {
        startIDIndex = startID
        vertices = v.toMutableList()
        makeConvex()
    }

    fun setLineIDs(startID: Int) {
        var id = startID
        for (line in lines) {
            line.myID = id++
            println(line.myID)
        }
    }

    fun makeConvex() {
//        for (i in 0 until vertices.size) {
//            vertices[i] = Pair(vertices[i].first.round(), vertices[i].second.round())
//        }

        val hull: MutableList<Pair<Double, Double>>

        vertices.sortBy { it.first }    // sorting by x-value
        val removals = mutableListOf<Pair<Double, Double>>()
        for (pair in vertices) {
            if (vertices.reduce { acc: Pair<Double, Double>, vertex: Pair<Double, Double> ->
                    if (vertex.first == pair.first && vertex.second == pair.second && vertex !== pair &&
                        !removals.contains(vertex)
                    ) Pair(1.0, 0.0) else acc
                }.first == 1.0) removals.add(pair)
        }
        vertices.removeAll(removals)        // remove duplicates

        val anchor = vertices.first()       // anchor is the furthest left
        vertices.removeAt(0)

        val vertical = vertices.filter { it.first == anchor.first }
        vertices.removeAll(vertical)        // Remove any vertices that could cause a divide by 0 error

        // Sort vertices
        vertices.sortBy {
            (it.second - anchor.second) / (it.first - anchor.first)
        }

        val sVert = vertical.sortedBy { it.second }.firstOrNull()
        val lVert = vertical.sortedBy { it.second }.lastOrNull()
        if (sVert?.second ?: 10000.0 < anchor.second) vertices.add(0, sVert!!)
        if (lVert?.second ?: -10000.0 > anchor.second) vertices.add(lVert!!)


        // prepare start of hull
        hull = mutableListOf(anchor, vertices.first())
        vertices.remove(vertices.first())
        hull.add(vertices.first())
        vertices.remove(vertices.first())
        vertices.add(anchor)

        fun checkLeft(): Boolean {
            val a = 2
            val b = 1
            val c = 0
            return (
                    ((hull[hull.lastIndex - b].first - hull[hull.lastIndex - a].first)
                            * (hull[hull.lastIndex - c].second - hull[hull.lastIndex - a].second))
                            - ((hull[hull.lastIndex - b].second - hull[hull.lastIndex - a].second)
                            * (hull[hull.lastIndex - c].first - hull[hull.lastIndex - a].first))
                    ) / 2.0 > 0       // Also removes collinear points

        }

        // Graham Scan Algorithm
        for (vertex in vertices) {
            hull.add(vertex)
            while (!checkLeft()) {
                hull.removeAt(hull.lastIndex - 1)
            }
        }
        vertices = hull
        vertices.removeAt(vertices.size - 1)

        // Remove any remaining collinear points
        val removeVerts = mutableListOf<Pair<Double, Double>>()
        for (i in 0 until vertices.size) {
            if (Line(vertices[i], vertices[(i + 1) % vertices.size]).angle.round(3) == Line(
                    vertices[(i + 1) % vertices.size],
                    vertices[(i + 2) % vertices.size]
                ).angle.round(3)
            ) removeVerts += vertices[(i + 1) % vertices.size]
        }
        for (vert in removeVerts) vertices.remove(vert)
    }

    open fun addLines(reset: Boolean = false) {
        when {
            lines.isNotEmpty() && reset -> lines.clear()
            lines.isNotEmpty() -> return
        }
        var currentID = startIDIndex
        vertices.forEachIndexed { index, pair ->
            val line = Line(pair, vertices[(index + 1) % vertices.size])
            if (currentID != 0) line.myID = currentID++
            lines.add(line)
        }
    }

    private fun prepare() {
        drawable = true
        makeConvex()
        addLines()
    }

    open fun show(pane: Pane) {
        if (!drawable) {
            prepare()
        }
        lines.forEach {
            it.draw(pane, 3.0)
        }
    }

    open fun hide(pane: Pane) {
        for (line in lines) {
            line.hide(pane)
        }
    }


    fun findCriticalAngles(list: MutableList<Line>) {
        lines.forEach {
            val line = Line(Pair(100.0, 100.0), Pair(25.0 * cos(it.angle) + 100.0, 25.0 * sin(it.angle) + 100.0))
            if (!list.contains(line)) list += line
        }
    }


    fun checkPointInShape(x: Double, y: Double): Boolean {
        val testLine = Line(Pair(x, y), Pair(1000.0, y))

        var intersections = 0
        lines.forEach {
            if (it.intersects(testLine)) intersections++
        }

        return intersections % 2 != 0
    }

    fun checkSideOfShape(vertex: Pair<Double, Double>, angle: Double): Boolean {
        val testLine = Line(vertex, Pair(1000.0 * cos(angle), 1000.0 * sin(angle)))

        var intersections = 0
        lines.forEach {
            if (it.intersects(testLine)) intersections++
        }

        return intersections > 0
    }

}