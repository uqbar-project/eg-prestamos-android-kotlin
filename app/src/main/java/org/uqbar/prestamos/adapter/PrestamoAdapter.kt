package org.uqbar.prestamos.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.uqbar.prestamos.model.Prestamo
import org.uqbar.prestamos.util.ImageUtil
import org.uqbar.prestamosapp.R
import org.uqbar.prestamosapp.R.id.txtLibro
import org.uqbar.prestamosapp.R.id.txtPrestamo

/**
 * Created by fernando on 11/1/16.
 */
class PrestamoAdapter(_mainActivity: Activity, _prestamosPendientes: MutableList<Prestamo>) : BaseAdapter() {

    val prestamosPendientes : MutableList<Prestamo> = _prestamosPendientes
    val mainActivity : Activity = _mainActivity

    override fun getCount(): Int {
        return prestamosPendientes.size
    }

    override fun getItem(position: Int): Any {
        return prestamosPendientes.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val prestamo = getItem(position) as Prestamo
        val inflater = mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.prestamo_row, parent, false)
        val lblLibro = row.findViewById(R.id.txtLibro) as TextView
        val lblPrestamo = row.findViewById(R.id.txtPrestamo) as TextView
        val imgContacto = row.findViewById(R.id.imgContacto) as ImageView
        lblLibro.text = prestamo.libro.toString()
        lblPrestamo.text = prestamo.datosPrestamo
        ImageUtil.assignImage(prestamo.contacto!!, imgContacto)
        return row
    }

}