package org.uqbar.prestamos.persistence

import android.app.Activity
import android.content.ContentProviderOperation
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import org.uqbar.prestamos.model.Contacto
import org.uqbar.prestamos.util.ImageUtil
import java.util.*

/**
 * Created by fernando on 10/28/16.
 */

/**
 * actividad (página) madre que permite hacer consultas sobre los contactos
 */
class PhoneBasedContactos(var parentActivity: Activity) : RepoContactos {

    override fun addContactoSiNoExiste(contacto: Contacto) {
        if (this.getContacto(contacto) == null) {
            this.addContacto(contacto)
        }
    }

    override fun addContacto(contacto: Contacto) {
        val tipoCuenta : String? = null
        val nombreCuenta : String? = null

        /** CON BUILDERS */
        val comandosAgregar : ArrayList<ContentProviderOperation> = ArrayList()
        // Fuerza a usar ArrayList!
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, tipoCuenta)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, nombreCuenta)
            .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contacto.nombre)
            .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contacto.numero)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
            .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contacto.email)
            .build()
        );
        comandosAgregar.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, contacto.foto)
            .build()
        )
        parentActivity.contentResolver.applyBatch(ContactsContract.AUTHORITY, comandosAgregar)
    }

    override fun contactos() : MutableList<Contacto>? {
        var cursorContactos : Cursor? = parentActivity.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursorContactos == null || cursorContactos.count < 1) {
            return null
        }

        val contactos : MutableList<Contacto> = mutableListOf()
        cursorContactos.moveToFirst()
        while (!cursorContactos.isAfterLast()) {
            contactos.add(this.crearContacto(cursorContactos))
            cursorContactos.moveToNext()
        }

        cursorContactos.close()

        return contactos
    }

    override fun getContacto(contactoOrigen: Contacto): Contacto? {
        // si queremos buscar por nombre
        //var cursorContactos = parentActivity.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Data.DISPLAY_NAME + " = ?", #[contactoOrigen.nombre], null)
        val lookupUri : Uri
        if (contactoOrigen.numero != null) {
            lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactoOrigen.numero))
        } else {
            lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, contactoOrigen.nombre)
        }

        var cursorContactos : Cursor? = parentActivity.getContentResolver().query(lookupUri, null, null, null, null)
        if (cursorContactos == null || cursorContactos.getCount() < 1) {
            return null
        }

        cursorContactos.moveToFirst()
        val contacto : Contacto = this.crearContacto(cursorContactos)
        cursorContactos.close()
        return contacto
    }

    override fun eliminarContactos() {
        val contentResolver = parentActivity.contentResolver
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        while (cursor !== null && cursor.moveToNext()) {
            val clave = getDataAsString(cursor, ContactsContract.Contacts.LOOKUP_KEY)
            val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, clave)
            contentResolver.delete(uri, null, null)
        }
    }


    /**
     * ***********************************************************************
     *     					METODOS INTERNOS
     * ***********************************************************************
     */
    /**
     * Extension method
     *
     * Facilita traer el dato de un cursor como un String
     */
    private fun getDataAsString(cursor: Cursor, index: String): String? {
        return cursor.getString(cursor.getColumnIndex(index))
    }

    /**
     * Método de uso interno.
     * Permite generar un objeto de dominio Contacto a partir de un cursor de ContactsContract.Contacts,
     * la API estándar de Android para manejar contactos del dispositivo.
     */
    private fun crearContacto(cursorContactos: Cursor): Contacto {
        val contactId = getDataAsString(cursorContactos, ContactsContract.Contacts._ID)
        val contactName = getDataAsString(cursorContactos, ContactsContract.Contacts.DISPLAY_NAME)
        var contactNumber: String? = null
        val foto : ByteArray
        var email = "" // TODO: Agregarlo

        val contentResolver = parentActivity.contentResolver
        val cursorTelefono = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(contactId), null)
        if (getDataAsString(cursorContactos, ContactsContract.Contacts.HAS_PHONE_NUMBER).equals("1")) {
            if (cursorTelefono !== null && cursorTelefono.moveToNext()) {
                contactNumber = getDataAsString(cursorTelefono, ContactsContract.CommonDataKinds.Phone.NUMBER) ?: "44441212"
            }
        }
        val cursorMail = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(contactId), null)
        if (cursorMail !== null && cursorMail.moveToNext()) {
            email = getDataAsString(cursorMail, ContactsContract.CommonDataKinds.Email.DATA) ?: "nomail@gmail.com"
        }
        val uriContacto = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId!!.toLong())
        foto = ImageUtil.convertToImage(parentActivity, uriContacto)
        return Contacto(contactId, contactNumber, contactName, email, foto)
    }

}