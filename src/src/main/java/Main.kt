
import com.fxgraph.graph.Graph
import com.fxgraph.graph.ICell
import com.jfoenix.controls.JFXToggleButton
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import tornadofx.singleAssign


const val maxX = 500.0
const val maxY = 500.0

val borderShape = Boundary(
    Pair(0.0, 0.0),
    Pair(0.0, maxY),
    Pair(maxX, maxY),
    Pair(maxX, 0.0)
)


var regionToggle: JFXToggleButton by singleAssign()
var minkowskiToggle: JFXToggleButton by singleAssign()

lateinit var currentSpace: ConfigurationSpace

val masterGraph = Graph()

val groups = mutableListOf<List<ICell>>()

fun main(args: Array<String>) = try {
    launch<MyApp>(args)
} catch (e: Exception) {
}


class MyApp : App(Visualization::class) {
    override fun start(stage: Stage) {
        minkowskiToggle = JFXToggleButton().apply { isSelected = true }
        regionToggle = JFXToggleButton().apply { isSelected = true }
        stage.minHeight = maxY + 80
        stage.minWidth = maxX + 40
//        stage.maxHeight = maxY + 80
        currentSpace = ConfigurationSpace()
        super.start(stage)
    }
}


