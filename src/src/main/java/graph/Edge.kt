package graph

import com.fxgraph.edges.AbstractEdge
import com.fxgraph.graph.Graph
import com.fxgraph.graph.ICell
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Group
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.text.Text

/*
    Converted to Kotlin by IntelliJ before modification
    Original code: https://github.com/sirolf2009/fxgraph/blob/master/src/main/java/com/fxgraph/edges/Edge.java
 */

class Edge(source: ICell, target: ICell) : AbstractEdge(source, target) {

    var color = Color.BLACK
    var width = 1.0

    @Transient
    private val textProperty: StringProperty

    init {
        textProperty = SimpleStringProperty()
    }

    override fun getGraphic(graph: Graph): EdgeGraphic {
        return EdgeGraphic(graph, this, textProperty)
    }

    inner class EdgeGraphic(graph: Graph, edge: Edge, textProperty: StringProperty) : Pane() {

        val group: Group = Group()
        val line: Line = Line()
        val text: Text

        init {

            val sourceX = edge.source.getXAnchor(graph, edge)
            val sourceY = edge.source.getYAnchor(graph, edge)
            val targetX = edge.target.getXAnchor(graph, edge)
            val targetY = edge.target.getYAnchor(graph, edge)

            line.apply {
                startXProperty().bind(sourceX)
                startYProperty().bind(sourceY)

                endXProperty().bind(targetX)
                endYProperty().bind(targetY)
                strokeWidth = this@Edge.width
                stroke = color
            }

            group.children.add(line)

            val textWidth = SimpleDoubleProperty()
            val textHeight = SimpleDoubleProperty()
            text = Text()
            text.textProperty().bind(textProperty)
            text.styleClass.add("edge-text")
            text.xProperty()
                .bind(line.startXProperty().add(line.endXProperty()).divide(2).subtract(textWidth.divide(2)))
            text.yProperty()
                .bind(line.startYProperty().add(line.endYProperty()).divide(2).subtract(textHeight.divide(2)))
            val recalculateWidth = {
                textWidth.set(text.layoutBounds.width)
                textHeight.set(text.layoutBounds.height)
            }
            text.parentProperty().addListener { _, _, _ -> recalculateWidth() }
            text.textProperty().addListener { _, _, _ -> recalculateWidth() }
            group.children.add(text)
            children.add(group)
        }

    }

}