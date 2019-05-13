import com.jfoenix.controls.JFXToggleButton
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import tornadofx.singleAssign


const val maxX = 500.0
const val maxY = 500.0
val space = Pair(maxX, maxY)

val angles = mutableListOf<Double>()

val borderShape = Boundary(
    Pair(0.0, 0.0),
    Pair(0.0, maxY),
    Pair(maxX, maxY),
    Pair(maxX, 0.0)
)

var regionToggle: JFXToggleButton by singleAssign()
var minkowskiToggle: JFXToggleButton by singleAssign()

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
        stage.minWidth = maxX + 40
    }
}


