package com.illiouchine.jm.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Test

open class Base41Test {

    data class Base41TestDatum(
        val rule: String = "",
        val input: String,
    )

    @Test
    fun encodeDecode() {
        val testData = listOf(
            Base41TestDatum(
                rule = "Emptiness is not an issue",
                input = "",
            ),
            Base41TestDatum(
                input = "a",
            ),
            Base41TestDatum(
                input = "abc",
            ),
            Base41TestDatum(
                input = "Lorem ipsum dolor sit amet...",
            ),
            Base41TestDatum(
                input = "Le Père Noël est un Œuf.",
            ),
            Base41TestDatum(
                input = "Black sphinx of quartz, judge my vow!",
            ),
            Base41TestDatum(
                input = "https://mieuxvoter.fr?hl=fr&s=Some+subject+for+the+poll",
            ),
            Base41TestDatum(
                rule = "",
                input = """
Accepte le pouvoir suprême
Quiconque enivré de soi-même Peut se flatter, émule de Titus,
Que le poison du diadème
N'altérera point ses vertus.

Je n'ai pas cette confiance, dont l'intrépide orgueil ne s'étonne de rien.
J'ai connu, par l'expérience, que celui qui peut tout, rarement veut le bien.
Éclairé par ma conscience sur mon peu de valeur, je l'en crois; et je crains
Que le fatal dépôt de la toute-puissance, par le sort ou le choix remis entre mes mains,
D'un mortel plein de bienfaisance, ne fît peut-être un fléau des humains. 
                """.trimIndent(),
            ),

            // Note: not testing with huge streams — the current impl does not handle them
            // If it could, with some refacto, it'd be worthy of its own lib.

        )

        testData.forEachIndexed { testIndex, testDatum ->
            val codec = Base41()
            val encoded = codec.encode(testDatum.input.encodeToByteArray())
            val decoded = codec.decode(encoded).decodeToString()
            assertEquals(
                "#$testIndex ${testDatum.rule} (${testDatum.input})",
                testDatum.input,
                decoded,
            )
        }
    }
}
