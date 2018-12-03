package org.uqbar.prestamos.model

/**
 * Representa un contacto dentro de la aplicación de préstamos de libros
 * Adapta el Contact propio de la API de ContactProvider de Android
 * Created by fernando on 10/27/16.
 */

class Contacto(var id: String?, var numero: String?, var nombre: String?, var email: String?, var foto: ByteArray?) {

    /*****************************************************
     * Negocio
     */
    override fun toString(): String {
        return nombre ?: "Contacto sin nombre"
    }

}