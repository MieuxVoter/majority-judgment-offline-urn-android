package com.illiouchine.jm.extensions

infix fun String.getInitials(seed: Int = 0): String {
    val wordsList = this
        // I'd prefer some \L{…} charsets in the regex if we can ; improve at will ; so far so good
        .split(Regex(pattern = "[^\\w\\u0080-\\uFFFF]+", options = setOf(RegexOption.IGNORE_CASE)))
        .filter { it.isNotEmpty() }

    if (wordsList.isEmpty()) {
        return this.take(3 + seed)
    }

    // Shifting bits around, because  It Just Works ®
    val divisor = (0x1 shl wordsList.size)
    val divMod = (seed + divisor).dividedBy(divisor)
    val initialsList = wordsList
        .mapIndexed { index, string ->
            val mask = (0x1 shl index)
            if ((divMod.second and mask) > 0) {
                string.take(divMod.first + 1)
            } else {
                string.first().toString()
            }
        }

    return initialsList.reduce({ acc, s -> acc + s })
}

infix fun Int.dividedBy(divisor: Int): Pair<Int, Int> {
    require(divisor != 0) { "The divisor must not be zero." }
    return this / divisor to this % divisor
}
