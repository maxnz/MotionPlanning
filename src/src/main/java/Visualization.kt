
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXToggleButton
import tornadofx.*

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
        right {
            pane {
                minWidth = 10.0
            }
        }

        val mainScreen =
            pane {
                fun Shape.addRandVertices(count: Int = 4) {
//                    val random = Random()
//                    for (x in 0 until count) {
//                        this.vertices.add(
//                            Pair(
//                                random.nextDouble() * maxX,
//                                random.nextDouble() * maxY
//                            )
//                        )
//                    }
                    this.vertices.add(Pair(212.19178247050118, 199.41125225953704))
                    this.vertices.add(Pair(380.00054194846786, 185.41255624578795))
                    this.vertices.add(Pair(335.1452304226062, 290.1791465715141))
                    this.vertices.add(Pair(285.0921122830405, 355.79432650181644))
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
            vbox {
                hbox {
                    this += JFXButton("Show Connections").apply {
                        action {
                            if (this.text == "Show Connections") {
                                CritAngles.showConnections(mainScreen)
                                this.text = "Hide Connections"
                            } else if (this.text == "Hide Connections") {
                                CritAngles.hideConnections(mainScreen)
                                this.text = "Show Connections"
                            }

                        }
                    }

                    this += JFXButton("Hide Shape").apply {
                        action {
                            if (this.text == "Hide Shape") {
                                obstacle.hide(mainScreen)
                                this.text = "Show Shape"
                            } else if (this.text == "Show Shape") {
                                obstacle.show(mainScreen)
                                this.text = "Hide Shape"
                            }
                        }
                    }

                    this += JFXButton("Show Critical Angles").apply {
                        action {
                            if (this.text == "Show Critical Angles") {
                                CritAngles.showCritAngles(mainScreen)
                                this.text = "Hide Critical Angles"
                            } else if (this.text == "Hide Critical Angles") {
                                CritAngles.hideCritAngles(mainScreen)
                                this.text = "Show Critical Angles"
                            }
                        }
                    }

                    this += JFXButton("Show Minkowski Sums").apply {
                        action {
                            if (this.text == "Show Minkowski Sums") {
                                mSums.show(mainScreen)
                                this.text = "Hide Minkowski Sums"
                            } else if (this.text == "Hide Minkowski Sums") {
                                mSums.hide(mainScreen)
                                this.text = "Show Minkowski Sums"
                            }
                        }
                    }

                    this += JFXButton("Next Minkowski Sum").apply {
                        action {
                            mSums.showNext(mainScreen)
                        }
                    }

                    this += JFXButton("Print").apply{
                        action{
                            print(mSums.currentMSum.angle)
                            print(" : ")
                            println(mSums.currentMSum.regionBoundaries.boundaryLines)
                        }
                    }
                }

                regionToggle = JFXToggleButton().apply {
                    text = "Show Region Boundaries"
                    isSelected = false
                }
                minkowskiToggle = JFXToggleButton().apply {
                    text = "Show Minkowski Exclusion Boundaries"
                    isSelected = true
                }

                this += regionToggle
                this += minkowskiToggle
            }
        }
    }
}