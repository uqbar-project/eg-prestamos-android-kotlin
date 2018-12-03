package org.uqbar.prestamos.model


import java.io.Serializable

/**
 * Representa un libro, un documento que puede ser prestado a un contacto.
 * Created by fernando on 10/27/16.
 */

class Libro(_id: Long?, _titulo: String?, _autor: String?) : Serializable {

    /*****************************************************
     * Atributos
     */
    var id: Long? = _id
    var titulo: String = _titulo ?: ""
    var autor: String = _autor ?: ""
    var isPrestado: Boolean = false

    init {
        isPrestado = false
    }

    override fun toString(): String {
        return "$titulo ($autor)"
    }

    fun prestar() {
        isPrestado = true
    }

    fun devolver() {
        isPrestado = false
    }

    fun estaPrestado(): Boolean {
        return isPrestado
    }

    fun estaDisponible(): Boolean {
        return !isPrestado
    }
}