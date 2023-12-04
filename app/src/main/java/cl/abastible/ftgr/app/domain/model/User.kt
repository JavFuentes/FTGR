package cl.abastible.ftgr.app.domain.model

/**
 * La clase `User` representa un modelo de datos para un usuario en la aplicación.
 *
 * @property id Identificador único de un usuario, utilizado para diferenciar entre varios usuarios y realizar operaciones específicas de usuario.
 * @property name Nombre completo del usuario, que podría usarse para mostrar un saludo personalizado o para identificar al usuario en otras partes de la aplicación.
 * @property username Nombre de usuario único utilizado para el inicio de sesión o identificación del usuario en la aplicación.
 * @property password Contraseña del usuario. NOTA: ESTO NO DEBE ALMACENARSE AQUÍ.
 */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val password: String
)