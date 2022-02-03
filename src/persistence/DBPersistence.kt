package persistence

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
/*
* Abstract class to make connection with the database. (Module, Program and Activity classes will
* inherit from this to implement its abstract methods in their scopes to show polymorphism and inheritance)
* */
abstract class DBPersistence {

    /*
    * companion object to make connection with the database and this object does need to be initialised in the child classes.
    * */
    companion object {
        private val conn: Connection = DriverManager.getConnection("jdbc:sqlite:greenwich.db")
        val statement: Statement = conn.createStatement()
    }

    //abstract methods to be implemented by child classes to have custom functionality for each method
    abstract fun save(data: Any): Boolean
    abstract fun loadAllFromDB(id: String?): ArrayList<Any>
    abstract fun delete(id: String?)
    abstract fun deleteAll(id: String?)
    abstract fun update(data: Any, currentID: String): Boolean
    abstract fun get(id: String): Any?

}