package cl.abastible.ftgr.app.ui.customviews

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import cl.abastible.ftgr.R

import kotlin.math.abs

class DrawingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    // Constructores adicionales
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    // Definición de Paint para configurar las propiedades del trazo
    private val paint = Paint()

    // Path que almacena el recorrido del trazo
    private val path = Path()

    // Variables para mantener un registro de las últimas coordenadas de toque
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f

    init {
        // Configuración inicial del Paint
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 3f
    }

    // Función para dibujar el trazo en el canvas
    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }

    // Función que detecta los eventos de toque en la pantalla
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        // Obtención del ScrollView si está presente en la actividad que usa este View
        val scrollView = (context as? Activity)?.findViewById<ScrollView>(R.id.truck_checklist_scrollview)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Inicia un nuevo trazo al tocar la pantalla
                path.moveTo(x, y)
                lastX = x
                lastY = y
                scrollView?.requestDisallowInterceptTouchEvent(true)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Agrega al trazo conforme se mueve el dedo por la pantalla
                val dx = abs(x - lastX)
                val dy = abs(y - lastY)
                if (dx >= 3 || dy >= 3) {
                    path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                    lastX = x
                    lastY = y
                }
                // Solicita redibujar el trazo
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Al levantar el dedo, permite nuevamente la intercepción de eventos de toque en el scrollView
                scrollView?.requestDisallowInterceptTouchEvent(false)
            }
        }
        if(event.action == MotionEvent.ACTION_UP) {
            onCanvasDrawnListener?.onCanvasDrawn(isCanvasDrawn())
        }
        return false
    }

    fun isCanvasDrawn(): Boolean {
        return !path.isEmpty
    }

    interface OnCanvasDrawnListener {
        fun onCanvasDrawn(isDrawn: Boolean)
    }

    //Variable para mantener una referencia al callback
    private var onCanvasDrawnListener: OnCanvasDrawnListener? = null

    //Método para setear el callback
    fun setOnCanvasDrawnListener(listener: OnCanvasDrawnListener?) {
        onCanvasDrawnListener = listener
    }

    // Función para limpiar el lienzo
    fun clearCanvas() {
        path.reset()
        invalidate()
        onCanvasDrawnListener?.onCanvasDrawn(isCanvasDrawn())
    }
}
