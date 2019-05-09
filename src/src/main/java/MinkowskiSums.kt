import javafx.scene.layout.Pane
import kotlin.math.PI

class MinkowskiSums {

    private val minkowskiSums = mutableListOf<MinkowskiSum>()

    private var currentSum = -1

    fun createSums(angles: List<Double>, shapes: List<Shape>) {
        for (angle in angles) {
            minkowskiSums.add(MinkowskiSum(angle, 100.0).apply {
                for (shape in shapes) {
                    addToSum(shape)
                }
            })
        }

        for (angle in angles) {
            minkowskiSums.add(MinkowskiSum(angle + PI, 100.0).apply {
                for (shape in shapes) {
                    addToSum(shape)
                }
            })
        }
    }

    fun show(pane: Pane) {
        minkowskiSums.forEach {
            it.show(pane)
        }
        currentSum = -1
    }

    fun showOne(pane: Pane) {
        hide(pane)
        minkowskiSums.first().show(pane)
        currentSum = 0
    }

    fun showNext(pane: Pane) {
        if (currentSum == -1) {
            showOne(pane)
            return
        }
        minkowskiSums[currentSum++ % minkowskiSums.size].hide(pane)
        minkowskiSums[currentSum % minkowskiSums.size].show(pane)
    }

    fun hide(pane: Pane) {
        minkowskiSums.forEach {
            it.hide(pane)
        }
    }

}
