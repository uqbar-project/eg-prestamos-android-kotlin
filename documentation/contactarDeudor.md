# Contactar a un deudor de un libro

La aplicación no solo nos muestra la lista de préstamos, podemos aprovechar las capacidades del celular para contactar a quien nos debe el libro de diferentes formas:

- podemos llamarlo
- o escribirle un mail

## Definición de un menú contextual

Cuando un usuario haga un "click largo" sobre un préstamo, activaremos el menú contextual. Definiremos en principio la vista de ese menú, que son las opciones disponibles, en un archivo `menu/prestamo_menu.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto" xmlns:android="http://schemas.android.com/apk/res/android" >

    <item
            android:id="@+id/action_call_contact"
            android:icon="@drawable/ic_action_call"
            android:orderInCategory="100"
            android:title="@string/action_call_contact" app:showAsAction="always"/>
    
    <item
            android:id="@+id/action_email_contact"
            android:icon="@drawable/ic_action_email"
            android:orderInCategory="200"
            android:title="@string/action_email_contact" app:showAsAction="always"/>

    <item
            android:id="@+id/action_return"
            android:icon="@drawable/ic_undo"
            android:orderInCategory="200"
            android:title="@string/action_return" app:showAsAction="always"/>

</menu>
```

Cada opción define el orden en el que debe aparecer (100, 200, 300), el title externalizado en el archivo `values/strings.xml` como de costumbre y una imagen para que no ocupe tanto espacio.

## Controller Parte I : Activando el menú

En `ActivityMain.xt` tenemos que bindear el menú. Para eso asociamos el _menuInflater_ con nuestro archivo `prestamo_menu.xml` en el método `onCreateActionMode`. Y después generamos el listener del _onItemLongClick_ para activar el menú contextual:

```kt
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.prestamo_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle) {
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
```

Lo vemos en acción:

![image](../images/menuActivado.png)

## Controller Parte II: Opción seleccionada

Ahora necesitamos que cuando el usuario haga click en alguna opción se dispare la acción correspondiente. Esto ocurre en el método `onActionItemClicked`:

```kt
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        ...
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
```

Sí, hay un enigmático `when` de Kotlin que [es un switch con más capacidades](https://antonioleiva.com/when-expression-kotlin/), podríamos reemplazarlo por un mapa asociado a funciones pero nuestro foco está en ver cómo resolver el envío de mails y la llamada desde el dispositivo.

## Configuración de permisos

Para llamar a un contacto, primero debemos configurar los permisos de la aplicación. A partir de la SDK 23, esto debemos hacerlo en forma programática:

```kt
class MainActivity: Activity() , ActionMode.Callback {
    ...

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private val PERMISSIONS_REQUEST_WRITE_CONTACTS = 200
    private val PERMISSIONS_REQUEST_CALL_PHONE = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestContactPermission()
        ...
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
```

## Llamar a un contacto

Al igual que la navegación, para llamar a un contacto utilizamos un **Intent**, en el método llamar(). La ventaja es que Android provee APIs de diferentes niveles, en este caso uno de alto nivel, que se comunica con componentes de más bajo nivel, como el hardware que maneja el teléfono:

```kt
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
```

Lo vemos en acción:

![image](../images/llamandoAOrnella.png)

## Enviar un mail

Para enviar un mail, tenemos otro **Intent** que Android nos provee:

```kt
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
```

Otra cosa interesante es que si configuramos nuestro correo en el emulador, tendremos acceso a los contactos (y podremos prestarle a ellos nuestros libros).
