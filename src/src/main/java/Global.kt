import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

var nextLineID = 1
    get() = field++

fun truncate(double: Double): Double = String.format("%.4f", double).toDouble()

fun List<Line>.vertices(): List<Pair<Double, Double>> {
    val ret = mutableListOf<Pair<Double, Double>>()
    this.forEach {
        if (!ret.contains(it.p1)) ret.add(it.p1)
        if (!ret.contains(it.p2)) ret.add(it.p2)
    }
    return ret
}

fun MutableList<Line>.contains(point: Pair<Double, Double>): Boolean {
    this.forEach {
        if (it.p1 == point || it.p2 == point) return true
    }
    return false
}

infix fun Pair<Double, Double>.dist(p2: Pair<Double, Double>): Double =
    sqrt((this.first - p2.first).absoluteValue.pow(2) + (this.second - p2.second).absoluteValue.pow(2))

infix fun Pair<Double, Double>.on(line: Line) = line.intersects(this)
