# Persistencia a un medio local

Entre los recursos disponibles de los dispositivos contamos con una API que permite persistir la información localmente, gracias a un motor que soporta el modelo relacional llamado [SQLite](https://www.sqlite.org/index.html).

Algunas características de este motor son:

* es liviano, sólo necesita 250K de memoria para ejecutarse
* no sólo funciona sino que además viene embebido en la VM de Android (ART)
* es open-source
* su distribución es gratuita
* como dijimos antes es un motor relacional, que 
* soporta transaccionalidad
* permite definir PRIMARY KEYs
* también claves subrogadas (ID autoincrementales)
* tiene un acotado sistema de tipos, apenas TEXT (String), INTEGER (int o Long), y REAL (double)

Para más detalles recomendamos la lectura de [esta página](http://www.vogella.com/tutorials/AndroidSQLite/article.html).

## Definición de estructuras de las tablas

La aplicación corre en el dispositivo, justamente donde necesitamos generar las tablas en el caso en que no existan. Entonces nuestro primer trabajo es definir un objeto que genere la estructura de las tablas Libros y Préstamos (los contactos ya se persisten cuando usamos el ContentProvider de Contacts):

TODO : PrestamosAppSQLiteHelper

Los métodos que definimos son:

* **onCreate:** el evento que se dispara la primera vez que se crea la base de datos
* **onUpgrade:** cuando se sube la versión de la base de datos, la estrategia (discutible) es eliminar las tablas libros y préstamos y volverlos a recrear. Tendríamos que analizar otras variantes si la información es sensible. Teniendo en cuenta que la app tiene fines didácticos no nos detenemos en este punto.

# Un nuevo hogar para los libros

Hasta el momento teníamos:

* una interfaz RepoLibros
* y una implementación concreta MemoryBaseHomeLibros

lo cual parecía una solución un tanto sobrediseñada. No obstante aquí vamos a generar una nueva clase Home, que va a terminar enviando mensajes a la base de datos local
