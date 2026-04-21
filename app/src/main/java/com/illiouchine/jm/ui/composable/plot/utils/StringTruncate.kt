package com.illiouchine.jm.ui.composable.plot.utils

fun String.truncate(
    maxLength: Int,
    // maxLines: Int = -1, // perhaps later
    ellipsis: String = "",
): String {
    if (maxLength < 0) return this
    if (this.length <= maxLength) return this

    val ellipsisLength = ellipsis.length
    val contentLength = maxLength - ellipsisLength

    return if (contentLength <= 0) {
        this.substring(0, maxLength)
    } else {
        this.substring(0, contentLength) + ellipsis
    }
}
