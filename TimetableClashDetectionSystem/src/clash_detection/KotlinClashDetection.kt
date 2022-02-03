package clash_detection

import data_classes.Activity

class KotlinClashDetection {

    fun detectClash(data: ArrayList<Object>, currActivity: Activity): Activity?{
        val activities = data as ArrayList<Activity>
        activities.forEach {
            if(it.clash(currActivity) != null){
                return it
            }
        }
        return null
    }
}