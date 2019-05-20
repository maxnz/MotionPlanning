package graph

import com.fxgraph.cells.AbstractCell
import com.fxgraph.cells.CellGestures
import com.fxgraph.graph.Graph
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle


class CircleCell : AbstractCell() {


    var stroke: Color = Color.RED
    var fill: Color = Color.RED
    var width = 50.0
    var height = width
    var radius = width / 2

    var resizable = false

    var labelText = ""

    override fun getGraphic(p0: Graph?): Region {
        val view = Circle(radius).apply {
            stroke = this@CircleCell.stroke
            fill = this@CircleCell.fill
        }

        val text = Label(labelText)

        val pane = VBox(view, text).apply {
            alignment = Pos.CENTER
        }
        pane.setPrefSize(width, height)
        if (resizable) CellGestures.makeResizable(pane)

        return pane
    }

}