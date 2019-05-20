import javafx.scene.layout.Pane
import kotlin.math.cos
import kotlin.math.sin


open class Shape() {

    var vertices = mutableListOf<Pair<Double, Double>>()
    var lines = mutableListOf<Line>()
    private var startIDIndex: Int? = null
    private var drawable = false

    constructor(startID: Int) : this() {
        startIDIndex = startID
    }

    constructor(v: List<Pair<Double, Double>>, startID: Int? = null) : this() {
        startIDIndex = startID
        vertices = v.toMutableList()
        makeConvex()
    }

    fun setLineIDs(startID: Int) {
        var id = startID
        for (line in lines) {
            line.myID = id++
        }
    }

    open fun addLines(reset: Boolean = false, trim: Boolean = true) {
        when {
            reset -> lines.clear()
            lines.isNotEmpty() -> return
        }
        vertices.forEachIndexed { index, pair ->
            lines.add(Line(pair, vertices[(index + 1) % vertices.size], skipTrim = !trim).apply {
                myID = startIDIndex?.plus(index) ?: 0
            })
        }
    }

    fun makeConvex() {
        vertices = makeConvex(vertices)
    }

    private fun prepare() {
        drawable = true
        makeConvex()
        addLines()
    }

    open fun show(pane: Pane) {
        if (!drawable) prepare()
        lines.forEach { it.draw(pane, 3.0) }
    }

    open fun hide(pane: Pane) {
        for (line in lines) line.hide(pane)
    }


    fun findCriticalAngles(list: MutableList<Line>) {
        lines.forEach {
            val line = Line(Pair(100.0, 100.0), Pair(25.0 * cos(it.angle) + 100.0, 25.0 * sin(it.angle) + 100.0))
            if (!list.contains(line)) list += line
        }
    }

    fun checkPointInShape(point: Pair<Double, Double>) = point !in makeConvex((vertices + point).toMutableList())

    fun checkPointInShape(x: Double, y: Double) = checkPointInShape(Pair(x, y))

}