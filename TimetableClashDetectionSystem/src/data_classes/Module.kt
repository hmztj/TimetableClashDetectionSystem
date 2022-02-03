package data_classes

data class Module(
    val pid: String?,
    val mid:String,
    var name:String,
    var type:String,
    var year:String,
    var term:String
){
    override fun toString(): String {
        return (
                """
                    [$pid-$mid] $name ${year}_${term} ($type)
                """.trimIndent()
                )
    }
    //var activities = ArrayList<Activity>()
}