package cl.abastible.ftgr.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cl.abastible.ftgr.databinding.ActivityMainBusinessBinding
import dagger.hilt.android.AndroidEntryPoint

// Esta clase representa la actividad principal del negocio después del proceso de bienvenida.
@AndroidEntryPoint
class MainBusinessActivity : AppCompatActivity() {

    // Variable para mantener la referencia del binding de la actividad.
    private lateinit var binding: ActivityMainBusinessBinding

    // Este método se ejecuta al crear la actividad. Inicializa la vista y configura cualquier componente necesario.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding.
        binding = ActivityMainBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Método para iniciar la actividad 'EntitySetList'.
    private fun startEntitySetListActivity() {

        // Crea un intent para iniciar la actividad 'SimpleActivity'.
        startActivity(Intent(this, MainActivity::class.java).apply {
            // Añade flags para que esta actividad sea la única en la pila y comience una nueva tarea.
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
    }

    // Método que se ejecuta cuando la actividad vuelve a estar en primer plano.
    override fun onResume() {
        super.onResume()
        // Inicia la actividad 'EntitySetList' cada vez que 'MainBusinessActivity' se reanuda.
        startEntitySetListActivity()
    }

    // Un objeto compañero, actualmente vacío, que puede ser utilizado para definir constantes o métodos estáticos relacionados con esta actividad.
    companion object {

    }
}
