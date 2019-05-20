data class Region(
    val regionID: Pair<Int, Int>,
    val boundary1: Line,
    val boundary2: Line
) {

    var subID = 0

    val id: String
        get() = regionID.toString() + if (subID != 0) ("-$subID") else ""

}