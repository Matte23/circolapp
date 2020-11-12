package net.underdesk.circolapp.shared.utils

object SqlUtils {
    fun Boolean.toLong() = if (this) 1L else 0L

    fun Long.toBoolean() = this == 1L

    fun String?.toList(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()

        if (this != null) {
            for (attachment in this.split("˜")) {
                list.add(attachment)
            }
        }

        return list.dropLast(1).toMutableList()
    }

    fun List<String>.joinToString(): String {
        var string = ""

        for (attachment in this) {
            string += "$attachment˜"
        }

        return string
    }
}
