package com.illiouchine.jm.extensions

fun <T> List<T>.reversedIf(shouldReverse: Boolean): List<T> {
    if (shouldReverse) {
        return this.reversed()
    }

    return this
}

fun List<String>.shortenNames(): List<String> {
    val longNamesList = this
    var loopCursor = 0
    var shorts = emptyList<String>()
    var duplicates = this.map { true }
    var unicityValidated = false

    while ((!unicityValidated) && loopCursor < 4096) {
        shorts = buildList {
            for ((i, longName) in longNamesList.withIndex()) {
                if (duplicates[i]) {
                    add(longName.getInitials(seed = loopCursor))
                } else {
                    add(shorts[i])
                }
            }
        }
        duplicates = shorts.mapIndexed { index, short -> // inefficient, but works ; fix at will
            shorts.indexOf(short) != index || shorts.lastIndexOf(short) != index
        }
        // unicityValidated = (shorts.toSet().size == shorts.size) // works, but any() is cheaper
        unicityValidated = !duplicates.any { it }

        // print(loopCursor.toString() + ": " + shorts.toString() + "\n")
        // print(duplicates.toString() + "\n")

        loopCursor++
    }

    return shorts
}
