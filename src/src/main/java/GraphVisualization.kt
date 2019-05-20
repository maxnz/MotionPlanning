import com.jfoenix.controls.JFXButton
import tornadofx.View
import tornadofx.action
import tornadofx.pane

class GraphVisualization : View() {

    override val root = pane {

//        this.add(
////            MinkowskiSums[currentSum].regionBoundaries.createGraph().canvas
//        )

        this.add(JFXButton("Back").apply {
            action {
                replaceWith(Visualization())
            }
        })

    }

}