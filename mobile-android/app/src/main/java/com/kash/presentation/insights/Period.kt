package com.kash.presentation.insights

import java.util.Calendar

enum class Period {
    TODAY, THIS_WEEK, THIS_MONTH;

    fun toRange(): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        return when (this) {
            TODAY      -> { cal.startOfDay();  cal.timeInMillis to now }
            THIS_WEEK  -> { cal.startOfWeek(); cal.timeInMillis to now }
            THIS_MONTH -> { cal.startOfMonth();cal.timeInMillis to now }
        }
    }

    fun label() = when (this) {
        TODAY      -> "Hoje"
        THIS_WEEK  -> "Semana"
        THIS_MONTH -> "Mês"
    }
}

private fun Calendar.startOfDay() {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}
private fun Calendar.startOfWeek()  { startOfDay(); set(Calendar.DAY_OF_WEEK, firstDayOfWeek) }
private fun Calendar.startOfMonth() { startOfDay(); set(Calendar.DAY_OF_MONTH, 1) }
