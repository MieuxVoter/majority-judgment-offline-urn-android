package com.illiouchine.jm.ui.preview

import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.floor

val subjectsWithProposals: List<Pair<String, List<String>>> = listOf(
    "Emperor ?" to listOf(
        "Dominus The Anarchist With a Long Name",
        "Tronald Dump",
        "Augustinus",
    ),
    "Epic Plumbers" to listOf(
        "Luigi the green plumber with a mustache and a long name, mamma mia !",
        "Uncle Bob",
        "Mario",
    ),
    "Best Prezidan ?" to listOf(
        "That candidate with quite a long name-san",
        "Luigi",
        "Bob",
        "JLM"
    ),
    "De combien de megawatts la couleur bleu est-elle plus poilue que sous la mer ?" to listOf(
        "42",
        "Rose",
        "Un sac de rats",
        "Presque",
        "Mercure",
        "Un Sayan",
        "Merde !"
    ),
    "Repas de ce soir, le Banquet Républicain de l'avènement du Jugement Majoritaire" to listOf(
        "Des nouilles aux champignons forestiers sur leur lit de purée de carottes urticantes",
        "Du riz",
        "Du riche",
        "Des Spaghetti Carbonara",
        "Poulet Tikka Masala",
        "Sushis Vegans",
        "Tacos au Bœuf",
    ),
    "Who let the dogs out in the cold of winter ?" to listOf(
        "Me",
        "You",
        "The ninja behind you",
        "idk"
    ),
    "Oh my gawd this poll has a long title !" to listOf(
        "the Amazing first proposal",
        "the underrated proposal",
        "the last one"
    )
)

object PreviewDataBuilder {

    fun judgments(size: Int = 3): ImmutableList<Judgment> {
        val judgments = mutableListOf<Judgment>()
        0.rangeUntil(size).forEach { i ->
            val judgment = Judgment(proposal = i, grade = floor(Math.random() * 7).toInt())
            judgments.add(judgment)
        }
        return judgments.toImmutableList()
    }

    fun pollConfig(index: Int? = null, grading: Grading = DEFAULT_GRADING_QUALITY_VALUE): PollConfig {
        val currentIndex: Int = index ?: floor(Math.random() * subjectsWithProposals.size).toInt()
        return PollConfig(
            subject = subjectsWithProposals[currentIndex].first,
            proposals = subjectsWithProposals[currentIndex].second,
            grading = grading
        )
    }

    fun ballots(size: Int = 3): ImmutableList<Ballot> {
        val ballots = mutableListOf<Ballot>()
        0.rangeUntil(size).forEach { i ->
            val ballot = Ballot(
                judgments = judgments(3)
            )
            ballots.add(ballot)
        }
        return ballots.toImmutableList()
    }

    fun poll(i: Int = 0, amountOfBallots: Int = 3): Poll {
        return Poll(
            id = i,
            pollConfig = pollConfig(),
            ballots = ballots(amountOfBallots)
        )
    }
}
