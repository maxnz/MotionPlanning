import javafx.scene.paint.Color
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

fun makeConvex(points: MutableList<Pair<Double, Double>>): MutableList<Pair<Double, Double>> {

    var vertices = points.toMutableList()
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

    return vertices
}


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

val usedColors = mutableListOf<Color>()
val colors = listOf(
//    Color.ALICEBLUE,
//    Color.ANTIQUEWHITE,
    Color.AQUA,
    Color.AQUAMARINE,
//    Color.AZURE,
//    Color.BEIGE,
//    Color.BISQUE,
    Color.BLACK,
//    Color.BLANCHEDALMOND,
    Color.BLUE,
    Color.BLUEVIOLET,
    Color.BROWN,
    Color.BURLYWOOD,
    Color.CADETBLUE,
    Color.CHARTREUSE,
    Color.CHOCOLATE,
    Color.CORAL,
    Color.CORNFLOWERBLUE,
//    Color.CORNSILK,
    Color.CRIMSON,
    Color.CYAN,
    Color.DARKBLUE,
    Color.DARKCYAN,
    Color.DARKGOLDENROD,
    Color.DARKGRAY,
    Color.DARKGREEN,
    Color.DARKGREY,
    Color.DARKKHAKI,
    Color.DARKMAGENTA,
    Color.DARKOLIVEGREEN,
    Color.DARKORANGE,
    Color.DARKORCHID,
    Color.DARKRED,
    Color.DARKSALMON,
    Color.DARKSEAGREEN,
    Color.DARKSLATEBLUE,
    Color.DARKSLATEGRAY,
    Color.DARKSLATEGREY,
    Color.DARKTURQUOISE,
    Color.DARKVIOLET,
    Color.DEEPPINK,
    Color.DEEPSKYBLUE,
    Color.DIMGRAY,
    Color.DIMGREY,
    Color.DODGERBLUE,
    Color.FIREBRICK,
//    Color.FLORALWHITE,
    Color.FORESTGREEN,
    Color.FUCHSIA,
    Color.GAINSBORO,
//    Color.GHOSTWHITE,
    Color.GOLD,
    Color.GOLDENROD,
    Color.GRAY,
    Color.GREEN,
    Color.GREENYELLOW,
    Color.GREY,
//    Color.HONEYDEW,
    Color.HOTPINK,
    Color.INDIANRED,
    Color.INDIGO,
//    Color.IVORY,
    Color.KHAKI,
    Color.LAVENDER,
    Color.LAVENDERBLUSH,
    Color.LAWNGREEN,
//    Color.LEMONCHIFFON,
    Color.LIGHTBLUE,
    Color.LIGHTCORAL,
//    Color.LIGHTCYAN,
//    Color.LIGHTGOLDENRODYELLOW,
    Color.LIGHTGRAY,
    Color.LIGHTGREEN,
    Color.LIGHTGREY,
    Color.LIGHTPINK,
    Color.LIGHTSALMON,
    Color.LIGHTSEAGREEN,
    Color.LIGHTSKYBLUE,
    Color.LIGHTSLATEGRAY,
    Color.LIGHTSLATEGREY,
    Color.LIGHTSTEELBLUE,
//    Color.LIGHTYELLOW,
    Color.LIME,
    Color.LIMEGREEN,
    Color.LINEN,
    Color.MAGENTA,
    Color.MAROON,
    Color.MEDIUMAQUAMARINE,
    Color.MEDIUMBLUE,
    Color.MEDIUMORCHID,
    Color.MEDIUMPURPLE,
    Color.MEDIUMSEAGREEN,
    Color.MEDIUMSLATEBLUE,
    Color.MEDIUMSPRINGGREEN,
    Color.MEDIUMTURQUOISE,
    Color.MEDIUMVIOLETRED,
    Color.MIDNIGHTBLUE,
//    Color.MINTCREAM,
//    Color.MISTYROSE,
    Color.MOCCASIN,
    Color.NAVAJOWHITE,
    Color.NAVY,
//    Color.OLDLACE,
    Color.OLIVE,
    Color.OLIVEDRAB,
    Color.ORANGE,
    Color.ORANGERED,
    Color.ORCHID,
    Color.PALEGOLDENROD,
    Color.PALEGREEN,
    Color.PALETURQUOISE,
    Color.PALEVIOLETRED,
//    Color.PAPAYAWHIP,
    Color.PEACHPUFF,
    Color.PERU,
    Color.PINK,
    Color.PLUM,
    Color.POWDERBLUE,
    Color.PURPLE,
    Color.RED,
    Color.ROSYBROWN,
    Color.ROYALBLUE,
    Color.SADDLEBROWN,
    Color.SALMON,
    Color.SANDYBROWN,
    Color.SEAGREEN,
//    Color.SEASHELL,
    Color.SIENNA,
    Color.SILVER,
    Color.SKYBLUE,
    Color.SLATEBLUE,
    Color.SLATEGRAY,
    Color.SLATEGREY,
//    Color.SNOW,
    Color.SPRINGGREEN,
    Color.STEELBLUE,
//    Color.TAN,
    Color.TEAL,
    Color.THISTLE,
    Color.TOMATO,
//    Color.TRANSPARENT,
    Color.TURQUOISE,
    Color.VIOLET,
    Color.WHEAT,
//    Color.WHITE,
//    Color.WHITESMOKE,
//    Color.YELLOW,
    Color.YELLOWGREEN
).shuffled()

val darkColors = listOf(
    Color.BLACK,
    Color.BLUE,
    Color.BLUEVIOLET,
    Color.BROWN,
    Color.CHOCOLATE,
    Color.CRIMSON,
    Color.DARKBLUE,
    Color.DARKCYAN,
    Color.DARKGREEN,
    Color.DARKMAGENTA,
    Color.DARKOLIVEGREEN,
    Color.DARKORCHID,
    Color.DARKRED,
    Color.DARKSLATEBLUE,
    Color.DARKSLATEGRAY,
    Color.DARKVIOLET,
    Color.DIMGRAY,
    Color.FIREBRICK,
    Color.FORESTGREEN,
    Color.GRAY,
    Color.GREEN,
    Color.INDIANRED,
    Color.INDIGO,
    Color.LIGHTSLATEGRAY,
    Color.MAGENTA,
    Color.MAROON,
    Color.MEDIUMBLUE,
    Color.MEDIUMPURPLE,
    Color.MEDIUMSLATEBLUE,
    Color.MEDIUMVIOLETRED,
    Color.MIDNIGHTBLUE,
    Color.NAVY,
    Color.OLIVE,
    Color.OLIVEDRAB,
    Color.PALEVIOLETRED,
    Color.PERU,
    Color.PURPLE,
    Color.RED,
    Color.ROYALBLUE,
    Color.SADDLEBROWN,
    Color.SEAGREEN,
    Color.SIENNA,
    Color.SLATEBLUE,
    Color.SLATEGRAY,
    Color.STEELBLUE,
    Color.TEAL
)