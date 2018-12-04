# Lista de préstamos

La primera versión de nuestra pantalla es simple

* Layout lineal
* ListView de préstamos como un content: en las versiones más nuevas de Android se reemplaza por un [**Recycler View**](https://developer.android.com/guide/topics/ui/layout/recyclerview)

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/TableLayout1"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <ListView
        android:id="@+id/lvPrestamos"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:choiceMode="singleChoice"
        ></ListView>

</LinearLayout>
```

### Layout

Al definir el layout en el ancho (width) y alto (height):

* para el ancho del textview que muestra la información de un libro consideraremos el tamaño de la pantalla: match_parent
* para el alto, nos interesa que aparezca toda la información del libro sin truncar, por eso usamos wrap_content

## Controller

El juego de datos se inicializa a partir de una lista de préstamos, que creamos en el singleton PrestamosAppBootstrap y que pueden ver en caso de interés.

### Adapter entre ListView y la lista de préstamos

```kt
private fun llenarPrestamosPendientes() {
    val prestamoAdapter = PrestamoAdapter(this, ArrayList(repoPrestamos.getPrestamosPendientes()))
    (lvPrestamos as ListView).adapter = prestamoAdapter
}
```

Esto permite asociar la lista de elementos de la ListView con un conjunto de datos:

![image](./images/arrayAdapter.png)

El PrestamoAdapter permite que por cada préstamo visualicemos:

* la imagen del contacto al que le prestamos el libro
* el título del libro
* la fecha y el nombre del contacto

TODO: Seguir en base a Mejorando el layout de la lista de préstamos

https://algo3.uqbar-project.org/temario/06-mobile/mejorando-el-layout-de-la-lista-de-prstamos
