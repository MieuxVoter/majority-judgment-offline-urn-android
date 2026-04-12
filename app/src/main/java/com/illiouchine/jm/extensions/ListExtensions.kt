package com.illiouchine.jm.extensions

fun <T> List<T>.reversedIf(shouldReverse: Boolean): List<T> {
    if (shouldReverse) {
        return this.reversed()
    }

    return this
}

fun List<String>.shortenNames(): List<String> {
    val longNamesList = this // hoisted because of buildList
    var shorts = emptyList<String>()
    var duplicates = this.map { true }
    var unicityValidated = false
    var loopCursor = 0

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
        duplicates = shorts.mapIndexed { index, short -> // inefficient, but works ; optimize maybe?
            shorts.indexOf(short) != index || shorts.lastIndexOf(short) != index
        }
        unicityValidated = !duplicates.any { it }

        // print(loopCursor.toString() + ": " + shorts.toString() + "\n")
        // print(duplicates.toString() + "\n")

        loopCursor++
    }

    return shorts
}
