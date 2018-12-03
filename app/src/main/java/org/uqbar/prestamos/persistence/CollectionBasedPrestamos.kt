package org.uqbar.prestamos.persistence

import org.uqbar.prestamos.model.Prestamo

/**
 * Created by fernando on 10/28/16.
 */
object CollectionBasedPrestamos : RepoPrestamos {

    /**
     * ******************************************************************************
     *   DEFINICION
     * ******************************************************************************
     */

    var prestamos : MutableList<Prestamo> = mutableListOf()

    /**
     * ******************************************************************************
     *   IMPLEMENTACION DEL SINGLETON
     * ******************************************************************************
     */
    override fun getPrestamosPendientes() : List<Prestamo> {
        return this.prestamos.filter { it.estaPendiente() }
    }

    override fun getPrestamo(id: Long) : Prestamo? {
        return this.prestamos.find { it.id!!.equals(id) }
    }

    override fun addPrestamo(prestamo: Prestamo) {
        prestamo.id = (this.prestamos.size + 1).toLong()
        this.prestamos.add(prestamo)
    }

    override fun removePrestamo(prestamo : Prestamo) {
        this.prestamos.remove(prestamo)
    }

    override fun updatePrestamo(_prestamo: Prestamo) {
        val prestamo = this.getPrestamo(_prestamo.id!!)!!
        this.removePrestamo(prestamo)
        this.addPrestamo(_prestamo)
    }

}