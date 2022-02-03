package persistence

import data_classes.*
import java.awt.Frame
import java.sql.SQLException
import javax.swing.JOptionPane

/*
* handler class to manage modules data type and implement lambdas to manage data collection and persistence with database*/
class ActivityHandler: DBPersistence() {

    override fun save(data: Any): Boolean {
        val activity: Activity = data as Activity

        //returns the lambda function as an object when invoked from GUI
        return saveActivity(activity)
    }

    override fun loadAllFromDB(id: String?): ArrayList<Any> {
        return loadActivities(id)
    }

    private val saveActivity: (Activity)
    -> Boolean = { activity: Activity
        ->
        try {
            statement.executeUpdate(
                "INSERT INTO `activities`(`mid`,`pid`,`type`,`day`,`start`,`end`, 'module_type', 'module_year', 'term') " +
                        "VALUES('${activity.mid}','${activity.pid}','${activity.type}','${activity.dayOfWeek}','${activity.start}','${activity.end}', '${activity.moduleType}', '${activity.moduleYear}', '${activity.term}')"
            )
            true
        }catch (e: SQLException) {

                JOptionPane.showMessageDialog(Frame(), "Oh my God!!..." + e.message)

            false
        }
    }

    private val loadActivities: (String?)
    -> ArrayList<Any> = { pid: String?
        -> val activities = ArrayList<Any>()
        try {
            var getActivities = statement.executeQuery("SELECT * FROM `activities` WHERE pid = \"$pid\"")
            if(pid == null){
                getActivities = statement.executeQuery("SELECT * FROM `activities`")
            }
            while (getActivities.next()) {
                val moduleID = getActivities.getString("mid")
                val programID = getActivities.getString("pid")
                val type = getActivities.getString("type")
                val day = getActivities.getString("day")
                val start = getActivities.getString("start")
                val end = getActivities.getString("end")
                val moduleType = getActivities.getString("module_type")
                val moduleYear = getActivities.getString("module_year")
                val term = getActivities.getString("term")
                val activity = Activity(moduleID, programID, type, day, start, end, moduleType, moduleYear, term)
                activities.add(activity)
            }

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }

        activities
    }

    override fun delete(id: String?) {
        deleteActivities(id)
    }

    private val deleteActivities: (String?)
    -> Unit = { mid: String?
        ->
        try {

            statement.executeUpdate("DELETE FROM `activities` WHERE mid = \"$mid\"")

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }
    }

    override fun deleteAll(id: String?) {
        deleteAllActivities(id)
    }

    private val deleteAllActivities: (String?)
    -> Unit = { pid: String?
        ->
        try {
            if(pid == "delete all"){
                statement.executeUpdate("DELETE FROM `activities` ")
            }else {
                statement.executeUpdate("DELETE FROM `activities` WHERE pid = \"$pid\"")
            }

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }
    }

    override fun update(data: Any, currentID: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(id: String): Any? {
        TODO("Not yet implemented")
    }


}