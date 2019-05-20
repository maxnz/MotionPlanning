data class Region(
    val regionID: Pair<Int, Int>,
    val boundary1: Line,
    val boundary2: Line
) {

    var subID = 0

    val id: String
        get() = regionID.toString() + if (subID != 0) ("-$subID") else ""

    infix fun nextTo(region: Region): Boolean {

        return (boundary1.myID == region.boundary1.myID ||
                boundary1.myID == region.boundary2.myID ||
                boundary2.myID == region.boundary1.myID ||
                boundary2.myID == region.boundary2.myID ||
                Pair(boundary1.myID, region.boundary1.myID) in adjacentBoundaries ||
                Pair(boundary1.myID, region.boundary2.myID) in adjacentBoundaries ||
                Pair(boundary2.myID, region.boundary1.myID) in adjacentBoundaries ||
                Pair(boundary2.myID, region.boundary2.myID) in adjacentBoundaries)
    }


}