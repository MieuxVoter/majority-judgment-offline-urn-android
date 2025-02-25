package com.illiouchine.jm.data.room

import com.illiouchine.jm.data.room.entity.BallotWithJudgment
import com.illiouchine.jm.data.room.entity.JudgmentEntity
import com.illiouchine.jm.data.room.entity.PollEntity
import com.illiouchine.jm.data.room.entity.PollWithProposals
import com.illiouchine.jm.data.room.entity.ProposalEntity
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Quality7Grading

fun Poll.toPollEntity(): PollEntity = PollEntity(
    subject = this.pollConfig.subject,
    nbGrading = this.pollConfig.grading.getAmountOfGrades(),
)

fun Poll.toProposalsEntity(): List<ProposalEntity> {
    return this.pollConfig.proposals.map { proposal ->
        ProposalEntity(
            name = proposal
        )
    }
}

fun List<Ballot>.toListOfJudgments(): List<List<JudgmentEntity>> {
    return this.map { ballot: Ballot ->
        ballot.judgments.map {
            JudgmentEntity(
                proposalIndex = it.proposal,
                gradeIndex = it.grade
            )
        }
    }
}

fun List<BallotWithJudgment>.toDomainObject(): List<Ballot> {
    return this.map { ballot ->
        Ballot(
            ballot.judgments.map { judgment ->
                Judgment(
                    proposal = judgment.proposalIndex,
                    grade = judgment.gradeIndex
                )
            }
        )
    }
}

suspend fun List<PollWithProposals>.toDomainObject(
    getBallots: suspend (pollId: Int) -> List<Ballot>
): List<Poll> {
    return this.map { poll ->
        val ballots: List<Ballot> = getBallots(poll.poll.uid)
        Poll(
            id = poll.poll.uid,
            pollConfig = PollConfig(
                subject = poll.poll.subject,
                proposals = poll.proposals.map { it.name },
                grading = qualityGrading(nbGrading = poll.poll.nbGrading),
            ),
            ballots = ballots
        )
    }
}

fun qualityGrading(nbGrading: Int): Grading {
    return Quality7Grading()
}

