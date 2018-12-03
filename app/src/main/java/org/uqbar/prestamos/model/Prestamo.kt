package org.uqbar.prestamos.model


import java.text.SimpleDateFormat
import java.util.Date
/**
 *
 * Representa el préstamo de un libro a un contacto
 *
 * Created by fernando on 10/27/16.
 */

class Prestamo(var id: Long?, var contacto: Contacto?, var libro: Libro?) {
    /*****************************************************
     * Atributos
     */
    var fechaPrestamo: Date = Date()
    var fechaDevolucion: Date? = null

    val datosPrestamo: String
        get() = SimpleDateFormat("dd/MM/yyyy").format(fechaPrestamo) + " a " + contacto!!.toString()

    fun telefono(): String {
        return contacto?.numero!!
    }

    fun contactoMail(): String {
        return contacto?.email!!
    }

    /*****************************************************
     * Negocio
     */
    fun estaPendiente(): Boolean {
        return fechaDevolucion == null
    }

    override fun toString(): String {
        return libro!!.toString() + " - " + this.datosPrestamo
    }

    fun prestar() {
        if (libro == null) {
            throw BusinessException("Debe ingresar libro")
        }
        if (contacto == null) {
            throw BusinessException("Debe ingresar contacto")
        }
        fechaPrestamo = Date()
        libro!!.prestar()
    }

    fun devolver() {
        libro!!.devolver()
        fechaDevolucion = Date()
    }
}

