package org.uqbar.prestamos.persistence


import org.uqbar.prestamos.model.Libro

/**
 *
 * Interfaz del objeto que maneja el origen de datos de los libros
 *
 * Created by fernando on 10/27/16.
 */

interface RepoLibros {
    fun libros(): List<Libro>
    fun librosPrestables(): List<Libro>

    fun addLibro(libro: Libro)
    fun addLibroSiNoExiste(libro: Libro): Libro
    fun getLibro(libroOrigen: Libro): Libro?
    fun getLibro(posicion: Int): Libro?
    fun removeLibro(libro: Libro)
    fun updateLibro(libro: Libro)
    fun removeLibro(posicion: Int)
    fun eliminarLibros()

}
