package org.uqbar.prestamosapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.uqbar.prestamos.adapter.PrestamoAdapter
import org.uqbar.prestamos.config.PrestamosAppBootstrap
import org.uqbar.prestamos.config.PrestamosConfig
import org.uqbar.prestamos.model.Prestamo
import org.uqbar.prestamosapp.R.id.action_nuevo_prestamo
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class MainActivity: Activity() , ActionMode.Callback {
    private var mActionMode: ActionMode? = null

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private val PERMISSIONS_REQUEST_WRITE_CONTACTS = 200
    private val PERMISSIONS_REQUEST_CALL_PHONE = 300

    private val repoPrestamos = PrestamosConfig.repoPrestamos(this)
    private val repoLibros = PrestamosConfig.repoLibros(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestContactPermission()

        PrestamosAppBootstrap(this)
        setContentView(R.layout.activity_main)

        // lv.multiChoiceModeListener = new PrestamoModeListener(this)
        val listPrestamos = lvPrestamos as ListView
        listPrestamos.isLongClickable = true
        listPrestamos.setOnItemLongClickListener { _, view, position, _ ->
            if (mActionMode != null) {
                return@setOnItemLongClickListener false // return de la lambda
            }
            mActionMode = this.startActionMode(this)
            mActionMode?.tag = position
            view.setSelected(true)
            return@setOnItemLongClickListener true // return de la lambda
        }
        registerForContextMenu(listPrestamos)
    }

    /***
     * A partir de la SDK 23 los permisos no se pueden manejar con
     * configuraciones en el AndroidManifest.xml, entonces hay que hacerlo programaticamente,
     * pidiendo acceso desde la app al usuario
     *
     * https://developer.android.com/training/permissions/requesting.html (el ejemplo usa App.Compat)
     * https://github.com/nilsorathiya/RuntimePermissionForAndroidMPlus
     */
    private fun requestContactPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_CONTACTS), PERMISSIONS_REQUEST_WRITE_CONTACTS)
        }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), PERMISSIONS_REQUEST_CALL_PHONE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.llenarPrestamosPendientes()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.llenarPrestamosPendientes()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun <T : Activity> navigate(classActivity: Class<T>) {
        val intent = Intent(this, classActivity)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.prestamo_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val prestamosPendientes = repoPrestamos.getPrestamosPendientes()
        if (prestamosPendientes.isEmpty()) {
            Toast.makeText(this, "No hay préstamos para trabajar", Toast.LENGTH_SHORT).show()
            return false
        }
        val posicion = Integer.parseInt(mActionMode?.tag.toString())
        val prestamo = prestamosPendientes[posicion]
        when (item.itemId) {
            R.id.action_call_contact -> llamar(prestamo.telefono())
            R.id.action_email_contact -> enviarMail(prestamo)
            R.id.action_return -> devolver(prestamo)
            else -> {}
        }
        return false
    }

    private fun devolver(prestamo: Prestamo) {
        prestamo.devolver()
        repoLibros.updateLibro(prestamo.libro!!)
        repoPrestamos.updatePrestamo(prestamo)
        this.llenarPrestamosPendientes()
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        mActionMode = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == action_nuevo_prestamo) {
            navigate(NuevoPrestamoActivity::class.java)
        }
        return true
    }

    private fun llamar(telefono: String): Boolean {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$telefono")
        try {
            startActivity(callIntent)
        } catch (e: Exception) {
            Log.e("ERROR al llamar ", e.message)
            Toast.makeText(this.applicationContext, "Hubo error al llamar al numero " + telefono, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun enviarMail(prestamo: Prestamo): Boolean {
        val uriText: String
        try {
            uriText = "mailto:" + prestamo.contactoMail() + "?subject=" +
                    URLEncoder.encode("Libro " + prestamo.libro?.titulo, StandardCharsets.UTF_8.name()) + "&body=" +
                    URLEncoder.encode("Por favor te pido que me devuelvas el libro", StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            Log.e("Librex", e.message)
            throw RuntimeException("Hubo un error al generar el mail", e)
        }
        val uri = Uri.parse(uriText)
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = uri
        // Necesitas configurar en el emulador el mail
        startActivity(Intent.createChooser(sendIntent, "Enviar mail"))
        return true
    }

    private fun llenarPrestamosPendientes() {
        val prestamoAdapter = PrestamoAdapter(this, ArrayList(repoPrestamos.getPrestamosPendientes()))
        (lvPrestamos as ListView).adapter = prestamoAdapter
    }

}