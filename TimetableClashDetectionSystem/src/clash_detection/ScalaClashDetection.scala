package clash_detection

import data_classes.Activity
import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala

object ScalaClashDetection {
  def detectClash(data: util.ArrayList[Object], currActivity: Activity): Activity = {
    val activities = data.asScala.toList.asInstanceOf[List[Activity]]
    //repeats until clash detected and return it.
    //returns the clashing activity if the clash happens otherwise null
    val list = for (x <- activities if x.clash(currActivity) != null) yield x
    if (!list.isEmpty) {
      return list.apply(0)
    }
    null
  }
}
