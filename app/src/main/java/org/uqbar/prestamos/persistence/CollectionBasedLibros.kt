package org.uqbar.prestamos.persistence

import org.uqbar.prestamos.model.Libro

/**
 * Created by fernando on 10/27/16.
 */

object CollectionBasedLibros : RepoLibros {

    /**
     * ******************************************************************************
     *   Atributos
     * ******************************************************************************
     */
    val libros : MutableList<Libro> = mutableListOf()

    /**
     * ******************************************************************************
     *   NEGOCIO
     * ******************************************************************************
     */
    override fun addLibro(libro: Libro) {
        libros.add(libro)
    }

    override fun addLibroSiNoExiste(libro: Libro) : Libro {
        if (this.getLibro(libro) == null) {
            this.addLibro(libro)
        }
        return libro
    }

    override fun getLibro(libroOrigen: Libro): Libro? {
        return this.libros.find { it.titulo.equals(libroOrigen.titulo, true) }
    }

    override fun getLibro(posicion: Int) : Libro? {
        return libros[posicion]
    }

    override fun librosPrestables(): List<Libro> {
        return this.libros.filter { it.estaDisponible() }
    }

    override fun removeLibro(libro: Libro) {
        libros.remove(libro)
    }

    override fun updateLibro(libro: Libro) {
        this.removeLibro(libro)
        this.addLibro(libro)
    }

    override fun removeLibro(posicion: Int) {
        this.libros.removeAt(posicion)
    }

    override fun eliminarLibros() {
        this.libros.clear()
    }

    override fun libros() : List<Libro> {
        return this.libros
    }
}