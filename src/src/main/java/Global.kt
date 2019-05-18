import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

var nextLineID = 1
    get() = field++

const val DEFAULTPRECISION = 4

fun Double.round(decimals: Int = DEFAULTPRECISION): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

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
        if (it.p1.round(1) == point.round(1) || it.p2.round(1) == point.round(1)) return true
    }
    return false
}

infix fun Pair<Double, Double>.dist(p2: Pair<Double, Double>): Double =
    sqrt((this.first - p2.first).absoluteValue.pow(2) + (this.second - p2.second).absoluteValue.pow(2))

infix fun Pair<Double, Double>.on(line: Line) = line.intersects(this)

infix fun Pair<Double, Double>.on(lines: List<Line>): List<Line> {
    val ret = mutableListOf<Line>()
    for (line in lines) {
        if (this on line) ret += line
    }
    return ret
}

fun Pair<Double, Double>.round(precision: Int = DEFAULTPRECISION): Pair<Double, Double> = Pair(
    this.first.round(precision),
    this.second.round(precision)
)