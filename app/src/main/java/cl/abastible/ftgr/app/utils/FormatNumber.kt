package cl.abastible.ftgr.app.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object FormatNumber {
    // Símbolos para el formato de números
    private val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    // Formato para números decimales
    private val priceFormat = DecimalFormat("$#,##0.0", symbols)
    private val litersFormat = DecimalFormat("#,##0.#", symbols)

    // Formato para números enteros
    private val integerPriceFormat = DecimalFormat("$#,##0", symbols)
    // Formato para números enteros
    private val integerLitersFormat = DecimalFormat("#,##0", symbols)

    // Función para formatear un número a un String con el formato de precio
    fun formatPrice(value: Float): String {
        // Si el valor es un número entero (la parte decimal es 0), usa el formato para enteros
        return if (value % 1 == 0f)
            integerPriceFormat.format(value)
        else
            priceFormat.format(value)
    }

    // Función para formatear un número a un String con el formato de litros
    fun formatLiters(value: Float): String {
        // Si el valor es un número entero (la parte decimal es 0), usa el formato para enteros
        return if (value % 1 == 0f)
            integerLitersFormat.format(value)
        else
            litersFormat.format(value)
    }
}
