package data_classes

data class Activity(
    var mid: String,
    var pid: String,
    var type: String,
    var dayOfWeek: String,
    var start: String,
    var end: String,
    val moduleType: String,
    val moduleYear: String,
    val term: String
) {

    override fun toString(): String {
        return (
                """
                     ${pid}_${mid} activity: $type Day: $dayOfWeek from: $start to: $end
                 """.trimIndent()
                )
    }

    fun clash(other: Activity): Activity? {

        //checks if activity being checked belongs to the same module
        if (this.pid == other.pid && yearClash(other) && termClash(other)) {

            if (midClash(other)) {
                if (bothLectures(other) || (timeClash(other) && !activityTypeClash(other))) {
                    return this
                }
            } else if (timeClash(other)) {
                if (bothLectures(other) && (bothCompulsory(other) || !moduleTypeClash(other))) {
                    println("line 34")
                    return this
                } else if (bothLabs(other) && (bothCompulsory(other) || !moduleTypeClash(other))) {
                    println("line 37")
                    return this
                }else if (!activityTypeClash(other) && (bothCompulsory(other) || !moduleTypeClash(other))){
                    println("line 40")
                    return this
                }
            }
        }
        return null
    }

    private fun termClash(other: Activity): Boolean {
        return this.term == other.term
    }

    //checks if the activity starts at the same time or in between the time range of any existing activity
    private fun timeClash(other: Activity): Boolean {
        val currStart: Int = other.start.toInt()
        val currEnd: Int = other.end.toInt()
        val e: Int = this.end.toInt()
        val s: Int = this.start.toInt()

        //checks range of the starting time.
        return (currEnd in (s + 1)..e || currStart in s until e) && this.dayOfWeek == other.dayOfWeek
    }

    private fun bothCompulsory(other: Activity): Boolean {
        return moduleTypeClash(other) && other.moduleType.equals("compulsory", true)
    }

//    private fun bothOptional(other: Activity): Boolean {
//        return moduleTypeClash(other) && other.moduleType.equals("optional", true)
//    }

    private fun bothLectures(other: Activity): Boolean {
        return activityTypeClash(other) && other.type.equals("Lecture", true)
    }

    private fun bothLabs(other: Activity): Boolean {
        return activityTypeClash(other) && other.type.equals("Lab", true)
    }

    private fun midClash(other: Activity): Boolean {
        return this.mid == other.mid
    }

    private fun yearClash(other: Activity): Boolean {
        return this.moduleYear == other.moduleYear
    }

    private fun moduleTypeClash(other: Activity): Boolean {
        return this.moduleType == other.moduleType
    }

    private fun activityTypeClash(other: Activity): Boolean {
        return this.type == other.type
    }

}
