package org.uqbar.prestamosapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.content.CursorLoader
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_nuevo_prestamo.*
import kotlinx.android.synthetic.main.prestamo_row.*
import org.uqbar.prestamos.config.PrestamosConfig
import org.uqbar.prestamos.model.BusinessException
import org.uqbar.prestamos.model.Contacto
import org.uqbar.prestamos.model.Libro
import org.uqbar.prestamos.model.Prestamo
import org.uqbar.prestamos.util.ImageUtil

class NuevoPrestamoActivity : Activity(), TextWatcher {

    val PICK_CONTACT = 1

    val mapaLibros = HashMap<String, Libro>()
    var libroSeleccionado: Libro? = null
    var contacto: Contacto? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.nuevo_prestamo, menu)
        val libros = PrestamosConfig.repoLibros(this).librosPrestables()
        for (libro in libros) {
            mapaLibros.put(libro.toString(), libro)
        }
        txtLibroTituloAutocomplete.setAdapter(ArrayAdapter<Libro>(this, android.R.layout.simple_dropdown_item_1line, libros))
        txtLibroTituloAutocomplete.addTextChangedListener(this)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_prestamo)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent) {
        when (reqCode) {
            PICK_CONTACT -> {
                if (resultCode === Activity.RESULT_OK) {
                    seleccionarContacto(data)
                }
            }
        }
    }

    /************************************************************************
     * Metodos requeridos por TextWatcher
     * ***********************************************************************
     */

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        libroSeleccionado = mapaLibros.get(txtLibroTituloAutocomplete.text.toString())
    }

    override fun afterTextChanged(s: Editable) { }

    /**
     * Click del boton Buscar contacto
     */
    fun buscarContacto(view: View) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT)
    }

    /**
     * Click del boton Prestar
     */
    fun prestar(view: View) {
        try {
            val prestamo = Prestamo(null,  contacto, libroSeleccionado)
            prestamo.prestar()
            PrestamosConfig.repoPrestamos(this).addPrestamo(prestamo)
            PrestamosConfig.repoLibros(this).updateLibro(libroSeleccionado!!)
            this.finish()
        } catch (be: BusinessException) {
            Toast.makeText(this, be.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Crear prestamo", e.message)
            e.printStackTrace()
            Toast.makeText(this, "Ocurrió un error. Consulte con el administrador del sistema.", Toast.LENGTH_SHORT).show()
        }
    }

    /************************************************************************
     * METODOS PRIVADOS
     * ***********************************************************************
     */
    private fun seleccionarContacto(data: Intent) {
        val loader = CursorLoader(this, data.getData(), null, null, null, null)
        val cursor = loader.loadInBackground()!!
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            val contactoBuscar = Contacto(null, null, name, null, null)
            // http://developer.android.com/reference/android/os/StrictMode.html
            contacto = PrestamosConfig.repoContactos(this).getContacto(contactoBuscar)!!
            txtContacto.text = contacto!!.nombre
            ImageUtil.assignImage(contacto!!, imgContactoAPrestar)
        }
    }

}
