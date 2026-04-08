package com.illiouchine.jm.ui.preview

import com.illiouchine.jm.config.DEFAULT_GRADING_QUALITY_VALUE
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

// How to enable this here and use faker in @Preview with faker as debug dep only ?
// import io.github.serpro69.kfaker.faker
// val faker = faker { }

// TBD: Look into Fakers for Kotlin, and conform
object PreviewDataFaker {

    fun judgments(
        size: Int,
        grading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
    ): ImmutableList<Judgment> {
        val judgments = mutableListOf<Judgment>()
        0.rangeUntil(size).forEach { i ->
            val judgment = Judgment(
                proposal = i,
                grade = floor(Math.random() * grading.getAmountOfGrades()).toInt(),
            )
            judgments.add(judgment)
        }
        return judgments.toImmutableList()
    }

    fun pollConfig(
        index: Int? = null,
        amountOfProposals: Int = 5,
        grading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
    ): PollConfig {
        // Note: The Elvis operator in Kotlin is actually a nil-coalescing, not a true Elvis
        val currentIndex = index ?: floor(Math.random() * subjectsWithProposals.size).toInt()
        return PollConfig(
            subject = subjectsWithProposals[currentIndex].first,
            // proposals = subjectsWithProposals[currentIndex].second,
            // subject = faker.quote.yoda(),
            proposals = 0.rangeUntil(amountOfProposals).map {
                // faker.coffee.blendName()
                "Proposal Number #${it + 1}" // hotfix 'til we manage to use faker
            },
            grading = grading,
        )
    }

    fun ballots(
        size: Int,
        pollConfig: PollConfig = pollConfig(),
    ): ImmutableList<Ballot> {
        val ballots = mutableListOf<Ballot>()
        0.rangeUntil(size).forEach { _ ->
            val ballot = Ballot(
                judgments = judgments(pollConfig.proposals.size, pollConfig.grading),
            )
            ballots.add(ballot)
        }
        return ballots.toImmutableList()
    }

    fun poll(
        id: Int = 0,
        amountOfProposals: Int = 9,
        amountOfBallots: Int = 5,
        grading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
    ): Poll {
        val config = pollConfig(
            grading = grading,
            amountOfProposals = amountOfProposals,
        )
        return Poll(
            id = id,
            pollConfig = config,
            ballots = ballots(
                size = amountOfBallots,
                pollConfig = config,
            )
        )
    }
}
