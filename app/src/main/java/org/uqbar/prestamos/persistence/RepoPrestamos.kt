package org.uqbar.prestamos.persistence

import org.uqbar.prestamos.model.Prestamo

/**
 * Created by fernando on 10/28/16.
 */

interface RepoPrestamos {

    fun getPrestamosPendientes(): List<Prestamo>
    fun getPrestamo(id: Long): Prestamo?
    fun addPrestamo(prestamo: Prestamo)
    fun removePrestamo(prestamo: Prestamo)
    fun updatePrestamo(prestamo: Prestamo)

}

