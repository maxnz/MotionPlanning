
import com.jfoenix.controls.JFXButton
import graph.Edge
import graph.GroupedCircleLayout
import graph.RectangleCell
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.action
import tornadofx.vbox

class GraphVisualization : View() {

    override val root = vbox {

        this.add(JFXButton("Back").apply {
            action {
                replaceWith(Visualization())
            }
        })
        for (i in 0 until groups.size) {
            groups[i].forEach { c1 ->
                groups[(i + 1) % groups.size].forEach { c2 ->
                    if (c1 is RectangleCell && c2 is RectangleCell && c1.labelText == c2.labelText)
                        masterGraph.model.addEdge(Edge(c1, c2).apply {
                            color = Color.GREEN
                            width = 3.0
                        })
                }
            }
        }
        masterGraph.endUpdate()

        masterGraph.apply {
            layout(GroupedCircleLayout())
        }
        this.add(masterGraph.canvas)
    }

}