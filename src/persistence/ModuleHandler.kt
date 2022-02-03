package persistence

import data_classes.*
import java.awt.Frame
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import javax.swing.JOptionPane

/*
* handler class to manage modules data type and implement lambdas to manage data collection and persistence with database*/
class ModuleHandler: DBPersistence() {

    override fun save(data: Any): Boolean {
        val module: Module = data as Module
        return saveModule(module)
    }

    override fun loadAllFromDB(id: String?): ArrayList<Any> {
        return loadModules(id)
    }

    override fun get(id: String): Any? {
        return getModule(id)
    }

    override fun update(data: Any, currentID: String): Boolean {
        val module:Module = data as Module
        return updateModule(module, currentID)
    }

    override fun delete(id: String?) {
        deleteModule(id)
    }

    override fun deleteAll(id: String?) {
        deleteAllModules(id)
    }

    private val saveModule: (Module)
    -> Boolean = { module: Module
        ->
        try {
            statement.executeUpdate(
                "INSERT INTO `modules`(`pid`,`mid`,`name`,`type`,`year`,`term`) " +
                        "VALUES('${module.pid}','${module.mid}','${module.name}','${module.type}','${module.year}','${module.term}')"
            )
            true
        } catch (e: SQLIntegrityConstraintViolationException) {
            JOptionPane.showMessageDialog(Frame(), "Duplicate entry '${module.mid}', please enter a unique module ID.")
            false
        } catch (e: SQLException) {

            if (e.message?.contains("UNIQUE") == true) {
                JOptionPane.showMessageDialog(
                    Frame(),
                    "Duplicate entry ${module.mid}, please enter a unique module ID."
                )
            } else {
                JOptionPane.showMessageDialog(Frame(), "Oh my God!!..." + e.message)
            }
            false
        }
    }

    private val getModule = { mid: String
        ->
        try {
            val getModules = statement.executeQuery("SELECT * FROM `modules` WHERE mid = \"$mid\"")
            getModules.next()

            val pid = getModules.getString("pid")
            val name = getModules.getString("name")
            val type = getModules.getString("type")
            val year = getModules.getString("year")
            val term = getModules.getString("term")

            Module(pid, mid, name, type, year, term)

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
            null
        }
    }

    private val loadModules: (String?)
    -> ArrayList<Any> = { pid: String?
        -> val modules = ArrayList<Any>()
        try {
            val getModules = statement.executeQuery("SELECT * FROM `modules` WHERE pid = \"$pid\"")
            while (getModules.next()) {
                val mid = getModules.getString("mid")
                val name = getModules.getString("name")
                val year = getModules.getString("year")
                val term = getModules.getString("term")
                val type = getModules.getString("type")
                val module = Module(pid, mid, name, type, year, term)

                modules.add(module)
            }

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }

        modules
    }

    private val updateModule: (Module, String)
    -> Boolean = { module: Module, currentMid: String
        -> try {
            val newMid = module.mid
            val name = module.name
            val type = module.type
            val term = module.term
            val year = module.year

            statement.executeUpdate(
                "UPDATE `modules` " +
                        "SET mid = \"$newMid\", name = \"$name\", year = \"$year\", term = \"$term\", type = \"$type\"" +
                        "  WHERE mid = \"$currentMid\" "
            )

            true

        } catch (e: SQLIntegrityConstraintViolationException) {
            JOptionPane.showMessageDialog(Frame(), "Duplicate entry '${module.mid}', please enter a unique module ID.")
            false
        } catch (e: SQLException) {

            if (e.message?.contains("UNIQUE") == true) {
                JOptionPane.showMessageDialog(
                    Frame(),
                    "Duplicate entry ${module.mid}, please enter a unique module ID."
                )
            } else {
                JOptionPane.showMessageDialog(Frame(), "Oh my God!!..." + e.message)
            }
            println("Oh my God!!..." + e.message)
            false
        }
    }

    private val deleteModule: (String?)
    -> Unit = { mid: String?
        ->
        try {

            statement.executeUpdate("DELETE FROM `modules` WHERE mid = \"$mid\"")

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }
    }

    private val deleteAllModules: (String?)
    -> Unit = { pid: String?
        ->
        try {

            statement.executeUpdate("DELETE FROM `modules` WHERE pid = \"$pid\"")

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }
    }

}