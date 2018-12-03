package org.uqbar.prestamos.persistence

import org.uqbar.prestamos.model.Contacto


/**
 * Created by fernando on 10/28/16.
 */

interface RepoContactos {
    fun contactos(): MutableList<Contacto>?
    fun addContactoSiNoExiste(contacto: Contacto)
    fun addContacto(contacto: Contacto)
    fun getContacto(contactoOrigen: Contacto): Contacto?
    fun eliminarContactos()

}