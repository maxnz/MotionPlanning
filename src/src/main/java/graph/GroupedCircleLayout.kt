package graph

import com.fxgraph.graph.Graph
import com.fxgraph.graph.ICell
import com.fxgraph.layout.Layout
import groups
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class GroupedCircleLayout : Layout {


    override fun execute(graph: Graph?) {

        fun List<ICell>.getGroupDistance(): Double = 50.0 / sin(PI / this.size)

        if (graph == null) return
        val groups = groups
        val a = 2 * PI / groups.size
        val groupDistanceBetween = groups.maxBy { it.getGroupDistance() }!!.getGroupDistance() * 3
        val groupDistanceFromCenter = (groupDistanceBetween * 0.5) / sin(a * 0.5)

        val centerPoint = groupDistanceFromCenter + 20.0

        var groupAngle = 0.0

        for (group in groups) {

            val centerX = cos(groupAngle) * groupDistanceFromCenter + centerPoint
            val centerY = sin(groupAngle) * groupDistanceFromCenter + centerPoint
            val b = 2 * PI / group.size

            val distanceBetween = 100.0
            val distanceFromCenter = (distanceBetween * 0.5) / sin(b * 0.5)

            var angle = 0.0
            for (cell in group) {
                graph.getGraphic(cell).relocate(
                    cos(angle) * distanceFromCenter + centerX,
                    sin(angle) * distanceFromCenter + centerY
                )
                angle += b
            }
            groupAngle += a
        }
    }

}