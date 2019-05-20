import javafx.scene.layout.Pane
import kotlin.math.PI

object MinkowskiSums {

    private val minkowskiSums = mutableListOf<MinkowskiSum>()

    operator fun get(i: Int): MinkowskiSum = minkowskiSums[i % minkowskiSums.size]

    private const val ladderLength = 100.0

    fun clearSums() {
        minkowskiSums.clear()
    }

    fun createSums(angles: List<Double>, shapes: List<Shape>, boundary: Shape) {
        for (angle in angles) {
            minkowskiSums.add(MinkowskiSum(angle, ladderLength).apply {
                for (shape in shapes) addToSum(shape)
                addBoundaryToSum(boundary)
            })
            minkowskiSums.add(MinkowskiSum(angle + PI, ladderLength).apply {
                for (shape in shapes) addToSum(shape)
                addBoundaryToSum(boundary)
            })
        }
        minkowskiSums.sortBy { it.angle }
    }

    fun showOne(pane: Pane, ladderPane: Pane, graphPane: Pane, sum: Int = currentSum) {
        get(currentSum).hide(pane, ladderPane, graphPane)
        currentSum = sum
        get(currentSum).show(pane, ladderPane, graphPane)
    }

    fun showNext(pane: Pane, ladderPane: Pane, graphPane: Pane) {
        get(currentSum++).hide(pane, ladderPane, graphPane)
        get(currentSum).show(pane, ladderPane, graphPane)
    }

    fun hideAll(pane: Pane) = minkowskiSums.forEach { it.hide(pane) }


    fun showExclusionBoundary(pane: Pane) = get(currentSum).showExclusionBoundary(pane)
    fun showRegionBoundaries(pane: Pane) = get(currentSum).regionBoundaries.showBoundaries(pane)

    fun hideExclusionBoundary(pane: Pane) = get(currentSum).hideExclusionBoundary(pane)
    fun hideRegionBoundaries(pane: Pane) = get(currentSum).regionBoundaries.hideBoundaries(pane)

    fun showGraph(graphPane: Pane) = get(currentSum).regionBoundaries.showGraph(graphPane)
    fun hideGraph(graphPane: Pane) = get(currentSum).regionBoundaries.hideGraph(graphPane)

}
