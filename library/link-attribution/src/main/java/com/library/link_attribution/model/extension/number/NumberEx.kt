package com.library.link_attribution.model.extension.number

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val mDecimalFormatSymbols by lazy {
    val decimalSymbols = DecimalFormatSymbols.getInstance()
    decimalSymbols.decimalSeparator = '.'
    decimalSymbols
}
private val mDecimalMax2DecimalPartFormat =
    DecimalFormat("#,###,###,###.##", mDecimalFormatSymbols)
private val mDecimalMax1DecimalPartFormat =
    DecimalFormat("#,###,###,###.#", mDecimalFormatSymbols)
private val mDecimalAlways2DecimalPartFormat =
    DecimalFormat("#,###,###,##0.00", mDecimalFormatSymbols)


fun Number?.toPriceAmount(): String {
    val dec = DecimalFormat("###,###,###.00")
    return dec.format(this)
}

fun Number?.toDynamicDecimalPart(): String? {
    if (this == null) return null
    val decimalSymbols = DecimalFormatSymbols.getInstance()
    decimalSymbols.decimalSeparator = '.'
    return DecimalFormat("#,###,###,###.##", decimalSymbols).format(this)
}

fun Number?.doubleToStringMax2DecimalPart(): String? {
    if (this == null) return null
    return mDecimalMax2DecimalPartFormat.format(this)
}

fun Number?.doubleToStringMax1DecimalPart(): String? {
    if (this == null) return null
    return mDecimalMax1DecimalPartFormat.format(this)
}


fun Number?.removeTrailingZeros(decimal: Int = 1): String {
    if (!toString().contains('.')) return toString()
    return String.format(Locale.US, "%.${decimal}f", this)
        .dropLastWhile { it == '0' }
        .dropLastWhile { it == '.' }
}

fun Number?.dpToPx(): Float {
    if (this == null) return 0f
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}

fun Number.spToPx(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}

fun Number.pxToDp(context: Context): Float {
    return this.toFloat() / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Int.toCelsius(): String {
    return String.format("%.1f", (this - 32) / 1.8)
}

