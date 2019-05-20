package graph

import com.fxgraph.graph.Graph
import com.fxgraph.layout.Layout
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CircleLayout : Layout {

    override fun execute(graph: Graph?) {
        if (graph == null) return
        val cells = graph.model.allCells

        val a = 2 * PI / cells.size
        val distanceBetween = 100.0
        val distanceFromCenter = (distanceBetween * 0.5) / sin(a * 0.5)
        val centerPoint = distanceFromCenter + 20.0
        var angle = 0.0

        for (cell in cells) {
            graph.getGraphic(cell).relocate(
                cos(angle) * distanceFromCenter + centerPoint,
                sin(angle) * distanceFromCenter + centerPoint
            )
            angle += a
        }
    }

}