import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.plusAssign
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin


// TODO: Refactor
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
        if (boundaryLines.isNotEmpty()) return
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

        val vertices = lines.vertices()
            .filter { it !in boundary.vertices }
            .sortedBy { it.anchorDist() } + lines.vertices()
            .filter { it in boundary.vertices }
            .sortedBy { it.anchorDist() }
        val anchorDists = mutableListOf<Double>()

        val maxList = listOf(0.0, 500.0)
        val a = (PI / 2) + angle

        for (v in vertices) {
            anchorDists += v.anchorDist().round(2)
        }

        d@ for (d in anchorDists.groupingBy { it.round(2) }.eachCount()) {
            println(d)
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
                v outside boundary || boundaryLines.contains(v) -> continue@v
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

    }

    fun findRegions(lines: List<Line>, pane: Pane) {

        val endPoints = mutableMapOf<Line, MutableList<Pair<Int, Int>>>()

        var b = 10
        boundaryLines.forEach {

            it.setID(b++)
//            println("${b - 1} $it ${it.p1.anchorDist()}")
            val p1ints = (it.p1 on lines).filter { it.myID != 0 }
            val p2ints = (it.p2 on lines).filter { it.myID != 0 }


            val linePairs = mutableListOf<Pair<Int, Int>>()
            p1ints.forEach { l1 ->
                val i1 = l1.myID
                p2ints.forEach { l2 ->
                    val i2 = l2.myID
                    if (i1 != i2 && Pair(i1, i2) !in linePairs && Pair(i2, i1) !in linePairs)
                        linePairs += if (i1 < i2) Pair(i1, i2) else Pair(i2, i1)
                }
            }
            if (linePairs.isEmpty()) throw Exception()
//            if (linePairs.isEmpty()) {
//                it.weight = 5.0
//                println("================================================")
//                print("\tP1: ")
//                val p1ints2 = (it.p1 on lines)
//                p1ints2.forEach {
//                    print("${it.myID} ")
//                }
//                println()
//                val p2ints2 = (it.p2 on lines)
//                print("\tP2: ")
//                p2ints2.forEach {
//                    print("${it.myID} ")
//                }
//                println()
//            }

            endPoints[it] = linePairs
        }

//        endPoints.forEach {
//            println("${it.key.myID} : ${it.value}")
//        }

        fun MutableList<RegionBoundary>.contains(intercept: Double?): Boolean {
            for (rb in this)
                if (intercept?.round(2) == rb.intercept) return true
            return false
        }

        fun MutableList<RegionBoundary>.contains(line: Line): Boolean {
            for (rb in this)
                if (line in rb.lines) return true
            return false
        }

        fun MutableList<RegionBoundary>.find(intercept: Double): RegionBoundary {
            for (rb in this)
                if (intercept.round(2) == rb.intercept) return rb
            return this[0]
        }

        fun MutableList<RegionBoundary>.find(line: Line): RegionBoundary {
            for (rb in this) {
                if (rb.lines.contains(line)) return rb
            }
            throw Exception()
        }

        val regionLines = mutableListOf<RegionBoundary>()

        for (line in boundaryLines) {
            if (line.p1 dist line.p2 == 0.0) regionLines += RegionBoundary(null).apply {
                this.lines += line
            }
            val intercept = line.intercept ?: continue
            if (!regionLines.contains(line.intercept))
                regionLines += RegionBoundary(intercept.round(2)).apply {
                    this.lines += line
                }
            else if (!regionLines.contains(line))
                regionLines.find(intercept).lines += line
        }

        val regionLinePairs =
            mutableMapOf<Pair<Int, Int>, MutableList<Pair<RegionBoundary, RegionBoundary>>>()

        for (line in endPoints) {
            for (ending in line.value)
                for (target in endPoints) {
                    if (ending in target.value && line !== target) {
                        println("${line.key.myID} - ${target.key.myID}: $ending")

                        if (regionLinePairs[ending] == null) regionLinePairs[ending] = mutableListOf()
                        if (Pair(regionLines.find(line.key), regionLines.find(target.key))
                            !in regionLinePairs[ending]!! &&
                            Pair(regionLines.find(target.key), regionLines.find(line.key))
                            !in regionLinePairs[ending]!!
                        )
                            regionLinePairs[ending]!!.add(
                                Pair(
                                    regionLines.find(line.key),
                                    regionLines.find(target.key)
                                )
                            )
                    }
                }
        }
        regionLinePairs.forEach {
            println(it)
        }

        regionLinePairs.forEach {
            for (v in it.value)
                for (r in regionLinePairs)
                    for (rv in r.value)
                        if (it !== r && (v.first == rv.first || v.first == rv.second || v.second == rv.first || v.second == rv.second))
                            println("${it.key} : ${r.key}")

        }

    }

    fun show(pane: Pane) {
        boundaryLines.forEach {
            it.draw(pane, weight = 1.0, color = Color.RED)
        }
        circles.forEach {
            if (!pane.children.contains(it))
                pane += it
        }
        labels.forEach {
            if (!pane.children.contains(it))
                pane += it
        }

    }

    fun hide(pane: Pane) {
        boundaryLines.forEach {
            it.hide(pane)
        }
        circles.forEach {
            if (pane.children.contains(it)) pane.children.remove(it)
        }
        labels.forEach {
            if (pane.children.contains(it)) pane.children.remove(it)
        }
    }
}
