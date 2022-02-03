package persistence

import data_classes.Program
import java.awt.Frame
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import javax.swing.JOptionPane

/*
* handler class to manage Program data type and implement lambdas to manage data collection and persistence with database*/
class ProgramHandler : DBPersistence() {

    /*
    * overridden inherited functions to invoke lambdas from Java GUI*/
    override fun save(data: Any): Boolean {
        val program: Program = data as Program
        return saveProgram(program)
    }

    override  fun get(id: String): Any? {
        return getProgram(id)
    }

    override fun loadAllFromDB(id: String?): ArrayList<Any> {
        return loadPrograms()
    }

    override fun delete(id: String?) {
        deleteProgram(id)
    }

    override fun deleteAll(id: String?) {
        deleteAllPrograms
    }

    override fun update(data: Any, currentID: String): Boolean {
        val program: Program = data as Program
        return updateProgram(program, currentID)
    }

//=========================================================================================
    private val saveProgram: (Program) -> Boolean = { program: Program ->

        try {
            statement.executeUpdate(
                "INSERT INTO `programs`(`pid`,`name`,`type`) " +
                        "VALUES('${program.pid}','${program.name}','${program.type}')"
            )
            true
        } catch (e: SQLIntegrityConstraintViolationException) {
            JOptionPane.showMessageDialog(Frame(), "Duplicate entry ${program.pid}, please enter a unique program ID.")
            false
        } catch (e: SQLException) {

            if (e.message?.contains("UNIQUE") == true) {
                JOptionPane.showMessageDialog(
                    Frame(),
                    "Duplicate entry ${program.pid}, please enter a unique program ID."

                )
            } else {
                JOptionPane.showMessageDialog(Frame(), "Oh my God!!..." + e.message)
            }
            false
        }

    }

    private val loadPrograms: () -> ArrayList<Any> = {
        val programs = ArrayList<Any>()
        try {
            val getProgram = statement.executeQuery("SELECT * FROM `programs`")

            while (getProgram.next()) {
                val name = getProgram.getString("name")
                val id = getProgram.getString("pid")
                val type = getProgram.getString("type")
                val program = Program(id, name, type)
                programs.add(program)
            }

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }

        programs
    }

    private val deleteProgram: (String?)
    -> Unit = { pid: String?
        ->
        try {

            statement.executeUpdate("DELETE FROM `programs` WHERE pid = \"$pid\"")
            statement.executeUpdate("DELETE FROM `modules` WHERE pid = \"$pid\"")

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }
    }

    private val deleteAllPrograms: ()
    -> Unit = {
        try {

            statement.executeUpdate("DELETE FROM `programs` ")
            statement.executeUpdate("DELETE FROM `modules` ")

        } catch (e: SQLException) {
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message)
        }
    }

    val updateProgram: (Program, String)
    -> Boolean = { program: Program, currentPid: String
        ->
        try {

            val newPid = program.pid
            val name = program.name
            val type = program.type

            statement.executeUpdate(
                "UPDATE `programs` " +
                        "SET pid = \"$newPid\", name = \"$name\", type = \"$type\" WHERE pid = \"$currentPid\""
            )
            statement.executeUpdate(
                "UPDATE `modules` " +
                        "SET pid = \"$newPid\" WHERE pid = \"$currentPid\""
            )

            true

        } catch (e: SQLIntegrityConstraintViolationException) {
            JOptionPane.showMessageDialog(Frame(), "Duplicate entry ${program.pid}, please enter a unique program ID.")
            false
        } catch (e: SQLException) {

            if (e.message?.contains("UNIQUE") == true) {
                JOptionPane.showMessageDialog(
                    Frame(),
                    "Duplicate entry ${program.pid}, please enter a unique program ID."
                )
            } else {
                JOptionPane.showMessageDialog(Frame(), "Oh my God!!..." + e.message)
            }
            false
        }
    }

    private val getProgram: (String)
    -> Any? = { pid: String
        ->
         try {
            val getProgram= statement.executeQuery("SELECT * FROM `programs` WHERE pid = \"$pid\" ")
            getProgram.next()

            val name = getProgram.getString("name")
            val id = getProgram.getString("pid")
            val type = getProgram.getString("type")

            Program(id, name, type)

        } catch (e:SQLException){
            JOptionPane.showMessageDialog(Frame(), "Oh dear! there's a problem..." + e.message)
            println("Oh dear! there's a problem..." + e.message )
            null
        }
    }


}