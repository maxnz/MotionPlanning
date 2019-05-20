import javafx.scene.layout.Pane
import java.util.*

class ConfigurationSpace {

    private val obstacle = Shape(5)
    private val shapes = mutableListOf(obstacle)
    private val mSums = MinkowskiSums

    init {
        mSums.clearSums()
        fun Shape.addRandVertices(count: Int = 4) {
            val random = Random()
            for (x in 0 until count) {
                this.vertices.add(
                    Pair(
                        (random.nextDouble() * maxX).toInt().toDouble(),
                        (random.nextDouble() * maxY).toInt().toDouble()
                    )
                )
            }
//                    this.vertices.addAll(listOf(Pair(298.0, 333.0), Pair(343.0, 47.0), Pair(335.0, 137.0), Pair(302.0, 443.0)))
            try {
                this.makeConvex()
            } catch (e: IndexOutOfBoundsException) {
                this.addRandVertices(1)
                return
            }
            this.addLines()
            this.setLineIDs(startID = 5)
        }

        obstacle.addRandVertices()
        CritAngles.findCritAngles(listOf(obstacle))

        mSums.createSums(CritAngles.allAngles, shapes, borderShape)
    }


    fun showObstacle(pane: Pane) = obstacle.show(pane)
    fun hideObstacle(pane: Pane) = obstacle.hide(pane)

    fun nextSum(pane: Pane, ladderPane: Pane, graphPane: Pane) = mSums.showNext(pane, ladderPane, graphPane)

}