import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXToggleButton
import tornadofx.*
import java.util.*

class Visualization : View() {

    private val obstacle = Shape(5)
    private val shapes = mutableListOf(obstacle)
    private val mSums = MinkowskiSums

    override val root = borderpane {

        left {
            pane {
                minWidth = 10.0
            }
        }
        top {
            pane {
                minHeight = 10.0
            }
        }
//        right {
//            pane {
//                minWidth = 10.0
//            }
//        }

        val mainScreen =
            pane {
                fun Shape.addRandVertices(count: Int = 4) {
                    val random = Random()
                    for (x in 0 until count) {
//                        this.vertices.add(
//                            Pair(
//                                (random.nextDouble() * maxX).toInt().toDouble(),
//                                (random.nextDouble() * maxY).toInt().toDouble()
//                            )
//                        )
                    }
                    this.vertices.addAll(listOf(Pair(298.0, 333.0), Pair(343.0, 47.0), Pair(335.0, 137.0), Pair(302.0, 443.0)))
                    this.makeConvex()
                    this.addLines()
                    this.setLineIDs(startID = 5)
                    println(this.vertices)
                }

                borderShape.show(this)
                obstacle.addRandVertices()
                obstacle.show(this)
                CritAngles.findCritAngles(listOf(obstacle))

                mSums.createSums(CritAngles.critAngles, shapes, borderShape)

            }


        center {
            this += mainScreen
        }

        bottom {
            pane {
                minHeight = 10.0
            }
        }


        val ladderPane = pane {
            minHeight = 200.0
            minWidth = 200.0
        }

        right {
            vbox {
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
                minkowskiToggle = JFXToggleButton().apply {
                    text = "Show Minkowski Exclusion Boundaries"
                    isSelected = true
                    action {
                        if (isSelected) MinkowskiSums[currentSum].showExclusionBoundary(mainScreen)
                        else MinkowskiSums[currentSum].hideExclusionBoundary(mainScreen)
                    }
                }
                this += minkowskiToggle

                // Region toggle
                regionToggle = JFXToggleButton().apply {
                    text = "Show Region Boundaries"
                    isSelected = true
                    action {
                        if (isSelected) MinkowskiSums[currentSum].regionBoundaries.show(mainScreen)
                        else MinkowskiSums[currentSum].regionBoundaries.hide(mainScreen)
                    }
                }
                this += regionToggle

                // Shape toggle
                this += JFXToggleButton().apply {
                    text = "Show Shape"
                    isSelected = true
                    action {
                        if (isSelected) obstacle.show(mainScreen)
                        else obstacle.hide(mainScreen)
                    }
                }

                // Ladder toggle
                this += JFXToggleButton().apply {
                    text = "Show Ladder"
                    isSelected = true
                    action {
                        if (isSelected) MinkowskiSums[currentSum].showLadder(ladderPane)
                        else MinkowskiSums[currentSum].hideLadder(ladderPane)
                    }
                }


                this += JFXButton("Next Minkowski Sum").apply {
                    action {
                        mSums.showNext(mainScreen, ladderPane)
                    }
                }

                this += hbox {
                    pane {
                        minWidth = 15.0
                    }

                    this += ladderPane
                }
            }
        }

//        bottom {
//            vbox {
//                hbox {
//                    this += JFXButton("Show Connections").apply {
//                        action {
//                            if (this.text == "Show Connections") {
//                                CritAngles.showConnections(mainScreen)
//                                this.text = "Hide Connections"
//                            } else if (this.text == "Hide Connections") {
//                                CritAngles.hideConnections(mainScreen)
//                                this.text = "Show Connections"
//                            }
//
//                        }
//                    }
//
//                    this += JFXButton("Hide Shape").apply {
//                        action {
//                            if (this.text == "Hide Shape") {
//                                obstacle.hide(mainScreen)
//                                this.text = "Show Shape"
//                            } else if (this.text == "Show Shape") {
//                                obstacle.show(mainScreen)
//                                this.text = "Hide Shape"
//                            }
//                        }
//                    }
//
//                    this += JFXButton("Show Critical Angles").apply {
//                        action {
//                            if (this.text == "Show Critical Angles") {
//                                CritAngles.showCritAngles(mainScreen)
//                                this.text = "Hide Critical Angles"
//                            } else if (this.text == "Hide Critical Angles") {
//                                CritAngles.hideCritAngles(mainScreen)
//                                this.text = "Show Critical Angles"
//                            }
//                        }
//                    }
//
//                    this += JFXButton("Show Minkowski Sums").apply {
//                        action {
//                            if (this.text == "Show Minkowski Sums") {
//                                mSums.show(mainScreen)
//                                this.text = "Hide Minkowski Sums"
//                            } else if (this.text == "Hide Minkowski Sums") {
//                                mSums.hide(mainScreen)
//                                this.text = "Show Minkowski Sums"
//                            }
//                        }
//                    }
//

//
//                    this += JFXButton("Print").apply{
//                        action{
//                            print(mSums.currentMSum.angle)
//                            print(" : ")
//                            println(mSums.currentMSum.regionBoundaries.boundaryLines)
//                        }
//                    }
//                }
//

//            }
//        }
    }
}