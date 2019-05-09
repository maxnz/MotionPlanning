import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch


const val maxX = 500.0
const val maxY = 500.0

val angles = mutableListOf<Double>()

val borderShape = Shape().apply {
    this.vertices.add(Pair(0.0, 0.0))
    this.vertices.add(Pair(0.0, maxY))
    this.vertices.add(Pair(maxX, maxY))
    this.vertices.add(Pair(maxX, 0.0))
}

fun main(args: Array<String>) {
    try {
        launch<MyApp>(args)
    } catch (e: Exception) {

    }
}


class MyApp : App(Visualization::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.minHeight = maxY + 70
        stage.minWidth = maxX + 20
    }
}


