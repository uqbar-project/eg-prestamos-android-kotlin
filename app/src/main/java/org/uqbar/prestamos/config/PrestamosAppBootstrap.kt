package org.uqbar.prestamos.config

import android.util.Log
import org.uqbar.prestamos.model.Contacto
import org.uqbar.prestamos.model.Libro
import org.uqbar.prestamos.model.Prestamo
import org.uqbar.prestamos.persistence.PhoneBasedContactos
import org.uqbar.prestamos.util.ImageUtil
import org.uqbar.prestamosapp.MainActivity

/**
 * Created by fernando on 10/28/16.
 */

class PrestamosAppBootstrap(activity: MainActivity) {

    init {
        /**
         * inicializamos la información de la aplicación
         */
        val repoContactos = PrestamosConfig.repoContactos(activity)
        repoContactos.addContactoSiNoExiste(
                Contacto("1", "46425829", "Chiara Dodino", "kiki.dodain@gmail.com", ImageUtil.convertToImage(activity, "kiarush.png"))
        )
        repoContactos.addContactoSiNoExiste(
                Contacto("2", "45387743", "Ornella Bordino", "ornelia@yahoo.com.ar", ImageUtil.convertToImage(activity, "ornella.jpg")))
        repoContactos.addContactoSiNoExiste(
                Contacto("3", "47067261", "Federico Cano", "el_fede@gmail.com", ImageUtil.convertToImage(activity, "fedeCano.jpg")))
        repoContactos.addContactoSiNoExiste(
                Contacto("4", "46050144", "Gisela Decuzzi", "shize_dekuuse@hotmail.com",
                        ImageUtil.convertToImage(activity, "gise.jpg")))
        repoContactos.addContactoSiNoExiste(
                Contacto("5", "42040007", "Estefanía Miguel", "tefffffi@hotmail.com",
                        ImageUtil.convertToImage(activity, "tefi.jpg")))

        var elAleph = Libro(1, "El Aleph", "J.L. Borges")
        elAleph.prestar()
        var laNovelaDePeron = Libro(2, "La novela de Perón", "T.E. Martínez")
        laNovelaDePeron.prestar()
        var cartasMarcadas = Libro(3, "Cartas marcadas", "A. Dolina")
        cartasMarcadas.prestar()

        val repoLibros = PrestamosConfig.repoLibros(activity)

        // Cuando necesitemos generar una lista nueva de libros
        // homeDeLibros.eliminarLibros()
        elAleph = repoLibros.addLibroSiNoExiste(elAleph)
        laNovelaDePeron = repoLibros.addLibroSiNoExiste(laNovelaDePeron)
        cartasMarcadas = repoLibros.addLibroSiNoExiste(cartasMarcadas)
        repoLibros.addLibroSiNoExiste(Libro(4, "Rayuela", "J. Cortázar"))
        repoLibros.addLibroSiNoExiste(Libro(5, "No habrá más penas ni olvido", "O. Soriano"))
        repoLibros.addLibroSiNoExiste(Libro(6, "La invención de Morel", "A. Bioy Casares"))
        repoLibros.addLibroSiNoExiste(Libro(7, "Cuentos de los años felices", "O. Soriano"))
        repoLibros.addLibroSiNoExiste(Libro(8, "Una sombra ya pronto serás", "O. Soriano"))
        repoLibros.addLibroSiNoExiste(Libro(9, "Octaedro", "J. Cortázar"))
        repoLibros.addLibroSiNoExiste(Libro(10, "Ficciones", "J.L. Borges"))

        val gise = Contacto(null, "46050144", null, null, null)
        val fede = Contacto(null, "47067261", null, null, null)
        val orne = Contacto(null, null, "Ornella Bordino", null, null)

        val repoPrestamos = PrestamosConfig.repoPrestamos(activity)
        if (repoPrestamos.getPrestamosPendientes().isEmpty()) {
            Log.w("Librex", "Creando préstamos")
            repoPrestamos.addPrestamo(Prestamo(1L, repoContactos.getContacto(fede)!!, elAleph))
            repoPrestamos.addPrestamo(Prestamo(2L, repoContactos.getContacto(gise)!!, laNovelaDePeron))
            repoPrestamos.addPrestamo(Prestamo(3L, repoContactos.getContacto(orne)!!, cartasMarcadas))
        }
    }
}