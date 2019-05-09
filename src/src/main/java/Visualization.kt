
import com.jfoenix.controls.JFXButton
import javafx.scene.paint.Color
import tornadofx.*
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class Visualization : View() {

    private val connections = mutableListOf<Line>()
    private val criticalAngleLines = mutableListOf<Line>()
    private val criticalAngles = mutableListOf<Double>()
    private val obstacle = Shape()
    private val shapes = mutableListOf(borderShape, obstacle)
    private val mSums = MinkowskiSums()

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
                borderShape.show(this)

                val random = Random()
                for (x in 0..3) {
                    obstacle.vertices.add(
                        Pair(
                            random.nextDouble() * maxX,
                            random.nextDouble() * maxY
                        )
                    )
                }

                obstacle.show(this)
                obstacle.getCriticalAngles(criticalAngleLines)

                for (v1 in borderShape.vertices) {
                    for (v2 in obstacle.vertices) {
                        val line = Line(v1, v2)
                        var intersect = false
                        for (l in obstacle.lines) {
                            if (line.intersects(l)) {
                                intersect = true
                                break
                            }
                        }
                        if (!intersect) connections.add(line)
                    }
                }

                connections.forEach {
                    var line =
                        Line(
                            Pair(100.0, 100.0),
                            Pair(25.0 * cos(it.angle) + 100.0, 25.0 * sin(it.angle) + 100.0)
                        )
                    criticalAngleLines.add(line)
                    line = Line(
                        Pair(100.0, 100.0),
                        Pair(100.0 - 25.0 * cos(it.angle), 100.0 - 25.0 * sin(it.angle))
                    )
                    criticalAngleLines.add(line)
                    criticalAngles.add(it.angle)
                }

                criticalAngles.sort()
                mSums.createSums(criticalAngles, shapes)

            }


        center {
            this += mainScreen
        }

        bottom {
            hbox {
                this += JFXButton("Show Connections").apply {
                    action {
                        if (this.text == "Show Connections") {
                            connections.forEach {
                                it.draw(mainScreen, color = Color.GREEN)
                            }
                            this.text = "Hide Connections"
                        } else if (this.text == "Hide Connections") {
                            for (line in connections) {
                                mainScreen.children.remove(line.myLine)
                            }
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
                            criticalAngleLines.forEach {
                                it.draw(mainScreen, color = Color.RED)
                            }
                            this.text = "Hide Critical Angles"
                        } else if (this.text == "Hide Critical Angles") {
                            criticalAngleLines.forEach {
                                mainScreen.children.remove(it.myLine)
                            }
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
            }
        }
    }

}