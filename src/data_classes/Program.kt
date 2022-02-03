package data_classes

data class Program(
    val pid:String,
    val name:String,
    val type:String
){

    override fun toString(): String {
        return (
                """
                    [$pid] $name ($type)
                """.trimIndent()
                )
    }

}
