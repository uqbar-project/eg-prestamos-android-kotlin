package org.uqbar.prestamos.persistence

import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.uqbar.prestamos.model.Libro


/**
 * Created by fernando on 10/28/16.
 */

class SQLLiteRepoLibros(var activity: Activity) : RepoLibros {

    private val CAMPOS_LIBRO = arrayOf("titulo, autor, prestado, id")

    var db: PrestamosAppSQLLiteHelper = PrestamosAppSQLLiteHelper.getInstance(activity)

    override fun addLibro(libro: Libro) {
        val con = db.writableDatabase
        try {
            var prestado = 0
            if (libro.estaPrestado()) {
                prestado = 1
            }
            val values = ContentValues()
            values.put("id", libro.id)
            values.put("titulo", libro.titulo)
            values.put("autor", libro.autor)
            values.put("prestado", prestado)

            con.insert("Libros", null, values)
            Log.w("Librex", "Se creó libro $libro")
        } finally {
            //if (con != null) con.close();
        }
    }

    override fun addLibroSiNoExiste(libro: Libro): Libro {
        val libroPosta = this.getLibro(libro)
        if (libroPosta != null) return libroPosta
        this.addLibro(libro)
        return libro
    }

    override fun libros(): List<Libro> {
        val con = db.readableDatabase
        try {
            val result = ArrayList<Libro>()

            val curLibros = con.query("Libros", CAMPOS_LIBRO, null, null, null, null, null)
            while (curLibros.moveToNext()) {
                result.add(crearLibro(curLibros))
            }
            Log.w("Librex", "getLibros | result: $result")
            return result
        } finally {
            //if (con != null) con.close();
        }
    }

    override fun getLibro(libroOrigen: Libro): Libro? {
        return libroPor("titulo = ? ", arrayOf(libroOrigen.titulo))
    }

    override fun getLibro(posicion: Int): Libro? {
        return libroPor("id = ? ", arrayOf("" + posicion))
    }

    override fun librosPrestables(): List<Libro> {
        return internalGetLibro({ con: SQLiteDatabase ->
            con.query(
                "Libros",
                CAMPOS_LIBRO,
                "prestado = ? ",
                arrayOf("0"),
                null,
                null,
                null
            )
        })
    }

    override fun removeLibro(libro: Libro) {
        borrarLibros("id = ? ", arrayOf("" + libro.id!!))
    }

    override fun updateLibro(libro: Libro) {
        this.removeLibro(libro)
        this.addLibro(libro)
    }

    override fun removeLibro(posicion: Int) {
        borrarLibros("id = ? ", arrayOf("" + posicion + 1))
    }

    override fun eliminarLibros() {
        borrarLibros(null, null)
    }

    private fun crearLibro(cursor: Cursor): Libro {
        val id = cursor.getInt(cursor.getColumnIndex("ID")).toLong()
        val titulo = cursor.getString(cursor.getColumnIndex("TITULO"))
        val autor = cursor.getString(cursor.getColumnIndex("AUTOR"))
        val prestado = cursor.getInt(cursor.getColumnIndex("PRESTADO"))
        val libro = Libro(id, titulo, autor)
        if (prestado == 1) {
            libro.prestar()
        }
        Log.w("Librex", "Traigo un libro de SQLite a memoria | id: " + libro.id + " | libro: " + libro)
        return libro
    }

    /***********************************************************************
     * METODOS PRIVADOS
     * *********************************************************************
     */
    /**
     * Abstrae una busqueda general de un libro en base a diferentes criterios
     */
    private fun libroPor(campo: String, condicion: Array<String>): Libro? {
        val libros = internalGetLibro({ con: SQLiteDatabase ->
            con.query(
                "Libros",
                CAMPOS_LIBRO,
                campo,
                condicion,
                null,
                null,
                null
            )
        })
        val libro = libros.stream().findFirst()
        return if (!libro.isPresent) {
            null
        } else libro.get()
    }

    /**
     * Abstrae una busqueda general de varios libros en base a un query que se pasa como Closure
     */
    private fun internalGetLibro(query: (SQLiteDatabase) -> Cursor): List<Libro> {
        val con = db.readableDatabase
        try {
            val result = ArrayList<Libro>()
            val curLibros = query(con)
            while (curLibros.moveToNext()) {
                result.add(crearLibro(curLibros))
            }
            return result
        } finally {
            //if (con != null) con.close();
        }
    }

    private fun borrarLibros(campo: String?, valores: Array<String>?) {
        val con = db.writableDatabase
        try {
            con.delete("Libros", campo, valores)
        } finally {
            //if (con != null) con.close();
        }
    }

}