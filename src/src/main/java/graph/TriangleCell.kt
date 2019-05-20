package graph

import com.fxgraph.cells.AbstractCell
import com.fxgraph.cells.CellGestures
import com.fxgraph.graph.Graph
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.transform.Scale




class TriangleCell : AbstractCell() {


    var stroke: Color = Color.RED
    var fill: Color = Color.RED
    var width = 50.0
    var height = 50.0

    var resizable = false


    override fun getGraphic(p0: Graph?): Region {
        val view = Polygon(width / 2, 0.0, width, height, 0.0, height)

        view.stroke = stroke
        view.fill = fill

        val pane = Pane(view)
        pane.setPrefSize(width, height)
        val scale = Scale(1.0, 1.0)
        view.transforms.add(scale)
        scale.xProperty().bind(pane.widthProperty().divide(width))
        scale.yProperty().bind(pane.heightProperty().divide(height))
        if (resizable) CellGestures.makeResizable(pane)

        return pane
    }

}