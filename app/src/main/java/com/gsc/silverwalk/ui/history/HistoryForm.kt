package com.gsc.silverwalk.ui.history

data class HistoryForm(
    val totalEasyCount: Int? = null,
    val totalModerateCount: Int? = null,
    val totalHardCount: Int? = null,
    val totalMenCount: Int? = null,
    val totalWomenCount: Int? = null,
    val total50sCount: Int? = null,
    val total60sCount: Int? = null,
    val total70sCount: Int? = null,
    val totalHumanCount: Int? = null,
) {
    fun getMenPercentString(): String {
        return (totalMenCount!!.toDouble() /
                (totalMenCount!! + totalWomenCount!!).toDouble()
                * 100.0).toInt().toString() + "%"
    }

    fun getWomenPercentString(): String {
        return (totalWomenCount!!.toDouble() /
                (totalMenCount!! + totalWomenCount!!).toDouble()
                * 100.0).toInt().toString() + "%"
    }

    fun get50sPercentString(): String {
        return (total50sCount!! / getTotalAgeCount() * 100.0).toInt().toString() + "%"
    }

    fun get60sPercentString(): String {
        return (total60sCount!! / getTotalAgeCount() * 100.0).toInt().toString() + "%"
    }

    fun get70sPercentString(): String {
        return (total70sCount!! / getTotalAgeCount() * 100.0).toInt().toString() + "%"
    }

    fun getTotalAgeCount(): Int {
        return total50sCount!! + total60sCount!! + total70sCount!!
    }
}