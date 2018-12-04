package org.uqbar.prestamos.persistence


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by fernando on 10/28/16.
 */

class PrestamosAppSQLLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Script para iniciar la base
     */
    override fun onCreate(db: SQLiteDatabase) {
        var crearTablas = StringBuffer()
        crearTablas.append("CREATE TABLE Libros (ID INTEGER PRIMARY KEY AUTOINCREMENT,")
        crearTablas.append(" TITULO TEXT NOT NULL,")
        crearTablas.append(" AUTOR TEXT NOT NULL,")
        crearTablas.append(" PRESTADO INTEGER NOT NULL);")
        db.execSQL(crearTablas.toString())

        crearTablas = StringBuffer()
        crearTablas.append("CREATE TABLE Prestamos (ID INTEGER PRIMARY KEY AUTOINCREMENT,")
        crearTablas.append(" LIBRO_ID INTEGER NOT NULL,")
        crearTablas.append(" CONTACTO_PHONE TEXT NOT NULL,")
        crearTablas.append(" FECHA TEXT NOT NULL,")
        crearTablas.append(" FECHA_DEVOLUCION TEXT NULL);")
        db.execSQL(crearTablas.toString())

    }

    /**
     * Estrategia para migrar de una version a otra
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Libros; ")
        db.execSQL("DROP TABLE IF EXISTS Prestamos; ")
        onCreate(db)
    }

    companion object {

        private val DATABASE_NAME = "librex.db"
        private val DATABASE_VERSION = 15

        /** Definicion del Singleton  */
        internal var instance: PrestamosAppSQLLiteHelper? = null

        fun getInstance(context: Context): PrestamosAppSQLLiteHelper {
            if (instance == null) {
                instance = PrestamosAppSQLLiteHelper(context)
            }
            return instance!!
        }
    }

}