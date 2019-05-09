import javafx.scene.layout.Pane
import kotlin.math.cos
import kotlin.math.sin


class Shape {

    var vertices = mutableListOf<Pair<Double, Double>>()
    var lines = mutableListOf<Line>()
    private var drawable = false


    private fun makeConvex() {

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
    }

    private fun addLines() {
        if (lines.isNotEmpty()) lines.clear()
        vertices.forEachIndexed { index, pair ->

            val line = Line(pair, vertices[(index + 1) % vertices.size])

            lines.add(line)
        }
    }

    private fun prepare() {
        drawable = true
        makeConvex()
        addLines()
    }

    fun show(pane: Pane) {
        if (!drawable) {
            prepare()
        }
        lines.forEach {
            it.draw(pane, 3.0)
        }
    }

    fun hide(pane: Pane) {
        for (line in lines)
            if (pane.children.contains(line.myLine)) pane.children.remove(line.myLine)
    }


    fun getCriticalAngles(list: MutableList<Line>) {
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

        println(intersections)

        return intersections % 2 != 0
    }

}