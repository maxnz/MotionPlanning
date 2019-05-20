package graph

import com.fxgraph.cells.AbstractCell
import com.fxgraph.cells.CellGestures
import com.fxgraph.graph.Graph
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


class RectangleCell : AbstractCell() {


    var stroke: Color = Color.DODGERBLUE
    var fill: Color = Color.DODGERBLUE
    var width = 50.0
    var height = 50.0

    var resizable = false

    var labelText = ""


    override fun getGraphic(p0: Graph?): Region {
        val view = Rectangle(width, height).apply {
            stroke = this@RectangleCell.stroke
            fill = this@RectangleCell.fill
        }
        val text = Label(labelText)

        val pane = VBox(view, text).apply {
            alignment = Pos.CENTER
        }
        pane.setPrefSize(width, height)
        view.widthProperty().bind(pane.prefWidthProperty())
        view.heightProperty().bind(pane.prefHeightProperty())
        if (resizable) CellGestures.makeResizable(pane)



        return pane
    }

}