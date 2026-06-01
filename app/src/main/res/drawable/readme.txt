Guía completa para una defensa práctica de Android Studio (20 minutos)

La mayoría de defensas prácticas de Android no buscan que desarrolles una funcionalidad compleja desde cero. Lo habitual es que el profesor compruebe si entiendes la estructura del proyecto, sabes localizar los archivos correctos y eres capaz de realizar modificaciones rápidas sin romper la aplicación.

1. Lo primero que debes saber localizar

Antes de tocar una sola línea de código debes saber dónde está cada cosa.

Layouts (interfaz)

Carpeta:

app
 └─ res
     └─ layout

Aquí están las pantallas XML.

Ejemplos:

activity_main.xml
activity_login.xml
fragment_home.xml
item_usuario.xml

Normalmente cualquier cambio visual se realiza aquí.

Textos

Archivo:

res/values/strings.xml

Ejemplo:

<string name="titulo">Mi aplicación</string>

Uso:

android:text="@string/titulo"
Colores

Archivo:

res/values/colors.xml

Ejemplo:

<color name="azul">#2196F3</color>
Imágenes e iconos

Carpeta:

res/drawable
Menús

Carpeta:

res/menu
Lógica

Archivos:

MainActivity.kt
LoginActivity.kt
HomeFragment.kt
2. Nivel fácil

Son las modificaciones más habituales.

Cambiar un texto

Antes:

<TextView
    android:text="Bienvenido"/>

Después:

<TextView
    android:text="Bienvenido al sistema"/>

Si usa strings:

android:text="@string/bienvenida"

Ir a:

strings.xml

Modificar:

<string name="bienvenida">
    Bienvenido al sistema
</string>
Cambiar tamaño de letra

Antes:

android:textSize="16sp"

Después:

android:textSize="24sp"

Valores frecuentes:

12sp
14sp
16sp
18sp
20sp
24sp
32sp
Cambiar color

Texto:

android:textColor="#FF0000"

Fondo:

android:background="#2196F3"

Usando colors.xml:

android:textColor="@color/rojo"
Cambiar márgenes

Antes:

android:layout_margin="8dp"

Después:

android:layout_margin="16dp"

Individuales:

android:layout_marginTop="16dp"
android:layout_marginBottom="16dp"
Cambiar padding
android:padding="16dp"
Centrar componentes

LinearLayout:

android:gravity="center"

ConstraintLayout:

app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
Cambiar hint

Antes:

android:hint="Nombre"

Después:

android:hint="Introduce tu nombre completo"
3. Nivel fácil-medio
Añadir un botón
<Button
    android:id="@+id/btnGuardar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Guardar"/>
Detectar pulsación

Kotlin:

val btnGuardar = findViewById<Button>(R.id.btnGuardar)

btnGuardar.setOnClickListener {

}
Mostrar un Toast
Toast.makeText(
    this,
    "Guardado correctamente",
    Toast.LENGTH_SHORT
).show()

Explicación:

this → contexto.
texto → mensaje.
LENGTH_SHORT → duración.
Añadir EditText
<EditText
    android:id="@+id/etNombre"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Introduce tu nombre"/>
Obtener texto del EditText
val nombre = etNombre.text.toString()
Añadir CheckBox
<CheckBox
    android:id="@+id/chkAcepto"
    android:text="Acepto condiciones"/>

Comprobar:

if(chkAcepto.isChecked){

}
Añadir Switch
<Switch
    android:id="@+id/swModoOscuro"
    android:text="Modo oscuro"/>
Añadir ImageView
<ImageView
    android:layout_width="150dp"
    android:layout_height="150dp"
    android:src="@drawable/logo"/>
4. Nivel medio
Crear una nueva Activity

Android Studio:

New
→ Activity
→ Empty Views Activity

Generará:

PerfilActivity.kt
activity_perfil.xml
Abrir una Activity
val intent = Intent(
    this,
    PerfilActivity::class.java
)

startActivity(intent)
Pasar datos entre pantallas

Enviar:

intent.putExtra(
    "nombre",
    "Carlos"
)

Recibir:

val nombre =
    intent.getStringExtra("nombre")
Snackbar
Snackbar.make(
    findViewById(android.R.id.content),
    "Elemento añadido",
    Snackbar.LENGTH_SHORT
).show()
Crear menú superior

Archivo:

res/menu/menu_principal.xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/configuracion"
        android:title="Configuración"/>

</menu>
Mostrar menú
override fun onCreateOptionsMenu(
    menu: Menu?
): Boolean {

    menuInflater.inflate(
        R.menu.menu_principal,
        menu
    )

    return true
}
Gestionar clic
override fun onOptionsItemSelected(
    item: MenuItem
): Boolean {

    when(item.itemId){

        R.id.configuracion -> {

            return true
        }
    }

    return super.onOptionsItemSelected(item)
}
5. Nivel medio-alto

Aquí empiezan las preguntas que diferencian a quien entiende el proyecto de quien solo modifica XML.

RecyclerView

Sirve para mostrar listas.

Necesita:

RecyclerView
Adapter
ViewHolder
Lista de datos
Añadir elemento

Lista:

val usuarios =
    mutableListOf<String>()

Añadir:

usuarios.add("Juan")

Actualizar:

adapter.notifyDataSetChanged()
Eliminar elemento
usuarios.removeAt(0)
adapter.notifyDataSetChanged()
Añadir un nuevo campo visual

Si existe:

<TextView
    android:id="@+id/txtNombre"/>

Añadir:

<TextView
    android:id="@+id/txtEdad"/>

Luego actualizar Adapter.

6. Nivel difícil

Estas modificaciones suelen aparecer cuando el profesor quiere comprobar que conoces realmente la arquitectura.

Fragments

Un Fragment es una parte reutilizable de pantalla.

Ejemplo:

HomeFragment
ProfileFragment
SettingsFragment

Preguntas típicas:

¿Por qué usar Fragment y no Activity?
¿Cómo navegas entre Fragments?
¿Dónde se infla el layout?
Navigation Component

Archivo:

navigation/nav_graph.xml

Aquí se definen rutas.

Ejemplo:

<fragment
    android:id="@+id/homeFragment"/>

<fragment
    android:id="@+id/profileFragment"/>
ViewBinding

Evita usar:

findViewById()

Utiliza:

binding.btnGuardar

Inicialización:

binding =
    ActivityMainBinding.inflate(
        layoutInflater
    )

setContentView(binding.root)
Base de datos Room

Preguntas típicas:

¿Dónde se guardan los datos?
¿Qué es una Entity?
¿Qué es un DAO?

Ejemplo:

@Entity
data class Usuario(
    @PrimaryKey
    val id:Int,
    val nombre:String
)
MVVM

Arquitectura muy común.

Capas:

View
ViewModel
Repository
Room/API

Explicación rápida:

View:

Muestra datos.

ViewModel:

Gestiona lógica.

Repository:

Obtiene datos.

Room/API:

Almacenamiento.
Preguntas teóricas que debes saber responder
¿Qué es una Activity?

Una pantalla completa de la aplicación.

¿Qué es un Fragment?

Un componente reutilizable que vive dentro de una Activity.

¿Qué es un Intent?

Un mecanismo para comunicarse entre componentes Android.

¿Qué es RecyclerView?

Un componente optimizado para mostrar listas.

¿Qué es un Adapter?

La clase que conecta los datos con el RecyclerView.

¿Qué es AndroidManifest?

Archivo donde se registran Activities, permisos y configuración general.

¿Qué es strings.xml?

Archivo donde se almacenan textos reutilizables.

¿Qué es colors.xml?

Archivo donde se centralizan colores de la aplicación.

¿Qué es ViewBinding?

Sistema para acceder a vistas sin usar findViewById.

¿Qué es Room?

Biblioteca oficial para gestionar bases de datos SQLite.