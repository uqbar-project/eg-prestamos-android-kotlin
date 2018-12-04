package org.uqbar.prestamos.persistence

import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import org.uqbar.prestamos.config.PrestamosConfig
import org.uqbar.prestamos.model.Contacto
import org.uqbar.prestamos.model.Prestamo
import org.uqbar.prestamosapp.util.DateUtil


class SQLLiteRepoPrestamos(var activity: Activity) : RepoPrestamos {

    var TABLA_PRESTAMOS = "Prestamos"
    var CAMPOS_PRESTAMO = arrayOf("id, fecha, fecha_devolucion, contacto_phone, libro_id")

    var db: PrestamosAppSQLLiteHelper = PrestamosAppSQLLiteHelper.getInstance(activity)

    override fun getPrestamosPendientes(): List<Prestamo> {
        return getPrestamos("fecha_devolucion is null", null)
    }

    override fun getPrestamo(id: Long): Prestamo? {
        val prestamos = getPrestamos("id", arrayOf(id.toString()))
        return if (prestamos.isEmpty()) {
            null
        } else prestamos[0]
    }

    override fun addPrestamo(prestamo: Prestamo) {
        val con = db.writableDatabase
        try {
            val values = ContentValues()
            var idPrestamo = prestamo.id
            if (idPrestamo == null) {
                idPrestamo = getMaxIdPrestamo()
                prestamo.id = idPrestamo
            }
            values.put("id", idPrestamo)
            Log.w("Crear prestamo", idPrestamo!!.toString())
            values.put("libro_id", prestamo.libro!!.id)
            values.put("contacto_phone", prestamo.telefono())
            // uso de extension methods de DateUtil
            values.put("fecha", DateUtil.asString(prestamo.fechaPrestamo))
            if (prestamo.estaPendiente()) {
                values.put("fecha_devolucion", null as String?)
            } else {
                values.put("fecha_devolucion", prestamo.fechaDevolucion!!.toString())
            }
            con.insert(TABLA_PRESTAMOS, null, values)
            Log.w("Librex", "Se creó préstamo $prestamo en SQLite")
        } finally {
            // Intuitivamente deberíamos cerrar la conexión, no obstante, esto cierra también
            // la base de datos, así que no debemos hacer eso
            // :)
            //if (con != null) con.close();
        }
    }

    override fun removePrestamo(prestamo: Prestamo) {
        val con = db.readableDatabase
        try {
            con.delete(TABLA_PRESTAMOS, "ID = ? ", arrayOf("" + prestamo.id!!))
        } finally {
            //if (con != null) con.close();
        }
    }

    override fun updatePrestamo(prestamo: Prestamo) {
        this.removePrestamo(prestamo)
        this.addPrestamo(prestamo)
    }

    private fun crearPrestamo(cursor: Cursor): Prestamo {
        val idPrestamo = cursor.getInt(cursor.getColumnIndex("ID")).toLong()
        val idLibro = cursor.getInt(cursor.getColumnIndex("LIBRO_ID"))
        val numeroTelContacto = cursor.getString(cursor.getColumnIndex("CONTACTO_PHONE"))
        val fecha = cursor.getString(cursor.getColumnIndex("FECHA"))
        val fechaDevolucion = cursor.getString(cursor.getColumnIndex("FECHA_DEVOLUCION"))
        val contactoBuscar = Contacto(null, numeroTelContacto, null, null, null)
        val contacto = PrestamosConfig.repoContactos(activity).getContacto(contactoBuscar)
        val libro = PrestamosConfig.repoLibros(activity).getLibro(idLibro)
        val prestamo = Prestamo(idPrestamo, contacto, libro)
        prestamo.fechaPrestamo = DateUtil.asDate(fecha)!!
        if (fechaDevolucion != null) {
            prestamo.fechaDevolucion = DateUtil.asDate(fechaDevolucion)
        }
        return prestamo
    }

    fun getMaxIdPrestamo(): Long? {
        val con = db.readableDatabase
        try {
            val curPrestamos = con.rawQuery("select MAX(ID) AS MAX_ID FROM $TABLA_PRESTAMOS", null)
            var idMax: Long? = 0L
            if (curPrestamos.moveToFirst()) {
                idMax = curPrestamos.getLong(curPrestamos.getColumnIndex("MAX_ID"))
            }
            curPrestamos.close()
            return idMax!! + 1
        } finally {
            //if (con != null) con.close();
        }
    }

    /***********************************************************************
     * METODOS INTERNOS
     *
     */
    private fun getPrestamos(campos: String, valores: Array<String>?): List<Prestamo> {
        val result = ArrayList<Prestamo>()
        val con = db.readableDatabase
        try {
            val curPrestamos = con.query(TABLA_PRESTAMOS, CAMPOS_PRESTAMO, campos, valores, null, null, null)
            while (curPrestamos.moveToNext()) {
                result.add(crearPrestamo(curPrestamos))
            }
            curPrestamos.close()
            Log.w("Librex", "Préstamos pendientes $result")
            return result
        } finally {
            //if (con != null) con.close();
        }
    }

}