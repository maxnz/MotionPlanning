import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXToggleButton
import javafx.geometry.Pos
import tornadofx.*

class Visualization : View() {


    override val root = borderpane {

        val ladderPane = pane {
            minHeight = 200.0
            minWidth = 200.0
        }

        val graphPane = pane {
            minHeight = 300.0
        }

        val mainScreen =
            pane {
                minWidth = maxX
                minHeight = maxY
                borderShape.show(this)
                currentSpace.showObstacle(this)
                MinkowskiSums.showOne(this, ladderPane, graphPane, MinkowskiSums.currentSum)
            }

        val optionsPane = vbox {
            // Connection Toggle
            this += JFXToggleButton().apply {
                text = "Show Connections"
                isSelected = false
                action {
                    if (isSelected) CritAngles.showConnections(mainScreen)
                    else CritAngles.hideConnections(mainScreen)
                }
            }

            // Minkowski toggle
            this += minkowskiToggle.apply {
                text = "Show Minkowski Exclusion Boundaries"
                action {
                    if (isSelected) MinkowskiSums.showExclusionBoundary(mainScreen)
                    else MinkowskiSums.hideExclusionBoundary(mainScreen)
                }
            }

            // Region toggle
            this += regionToggle.apply {
                text = "Show Region Boundaries"
                action {
                    if (isSelected) MinkowskiSums.showRegionBoundaries(mainScreen)
                    else MinkowskiSums.hideRegionBoundaries(mainScreen)
                }
            }

            // Shape toggle
            this += JFXToggleButton().apply {
                text = "Show Shape"
                isSelected = true
                action {
                    if (isSelected) currentSpace.showObstacle(mainScreen)
                    else currentSpace.hideObstacle(mainScreen)
                }
            }

            // Next sum button
            this += JFXButton("Next Minkowski Sum").apply {
                action { currentSpace.nextSum(mainScreen, ladderPane, graphPane) }
            }
            hbox {
                this += JFXButton("New Shape").apply {
                    action {
                        currentSpace = ConfigurationSpace()
                        replaceWith(Visualization())
                    }
                }
                this += JFXButton("Graph").apply { action { replaceWith(GraphVisualization()) } }
                this += JFXButton("Print AB").apply { action { MinkowskiSums[MinkowskiSums.currentSum].regionBoundaries.printAdjacentBoundaries() } }
            }

            hbox {
                pane { minWidth = 15.0 }
                this += ladderPane
            }
        }


        top { pane { minHeight = 10.0 } }
        bottom {
            minHeight = 400.0
            style { alignment = Pos.CENTER }
            this += graphPane
        }
        center { this += mainScreen }
        left { pane { minWidth = 10.0 } }
        right { this += optionsPane }

    }
}