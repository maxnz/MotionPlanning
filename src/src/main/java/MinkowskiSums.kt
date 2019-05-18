import javafx.scene.layout.Pane
import kotlin.math.PI

object MinkowskiSums {

    private val minkowskiSums = mutableListOf<MinkowskiSum>()

    operator fun get(i: Int): MinkowskiSum = minkowskiSums[i % minkowskiSums.size]

    val numSums: Int
        get() = minkowskiSums.size

    fun createSums(angles: List<Double>, shapes: List<Shape>, boundary: Shape) {
        for (angle in angles) {
            minkowskiSums.add(MinkowskiSum(angle, 25.0).apply {
                for (shape in shapes) {
                    addToSum(shape)
                }
                addBoundaryToSum(boundary)
            })
        }

        for (angle in angles) {
            minkowskiSums.add(MinkowskiSum(angle + PI, 25.0).apply {
                for (shape in shapes) {
                    addToSum(shape)
                }
                addBoundaryToSum(boundary)
            })
        }
    }

//    fun show(pane: Pane) {
//        if (currentSum != -1) minkowskiSums[currentSum % minkowskiSums.size].hide(pane)
//        minkowskiSums.forEach {
//            it.show(pane)
//        }
//        currentSum = -1
//    }

    fun showOne(pane: Pane, sum: Int = currentSum) {
        minkowskiSums[currentSum % minkowskiSums.size].hide(pane)
        currentSum = sum
        minkowskiSums[currentSum % minkowskiSums.size].show(pane)
    }

    fun showNext(pane: Pane, ladderPane: Pane) {
        minkowskiSums[currentSum++ % minkowskiSums.size].hide(pane, ladderPane)
        minkowskiSums[currentSum % minkowskiSums.size].show(pane, ladderPane)
    }

    fun hide(pane: Pane) {
        minkowskiSums.forEach {
            it.hide(pane)
        }
    }

}
