package org.uqbar.prestamos.config


import android.app.Activity
import org.uqbar.prestamos.persistence.*

/**
 * Created by fernando on 10/27/16.
 */

object PrestamosConfig {

    fun repoLibros(activity: Activity): RepoLibros {
        // PERSISTENTE
        //return new SQLLiteRepoLibros(activity);
        // NO PERSISTENTE
        return CollectionBasedLibros
    }

    fun repoPrestamos(activity: Activity): RepoPrestamos {
        // PERSISTENTE
        //return new SQLLiteRepoPrestamos(activity);
        // NO PERSISTENTE
        return CollectionBasedPrestamos
    }

    fun repoContactos(activity: Activity): RepoContactos {
        return PhoneBasedContactos(activity)
    }

}