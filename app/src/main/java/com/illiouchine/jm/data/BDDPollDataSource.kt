package com.illiouchine.jm.data

import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.data.room.entity.BallotWithJudgment
import com.illiouchine.jm.data.room.entity.JudgmentEntity
import com.illiouchine.jm.data.room.entity.PollEntity
import com.illiouchine.jm.data.room.entity.PollWithProposals
import com.illiouchine.jm.data.room.entity.ProposalEntity
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Quality7Grading

class BDDPollDataSource(
    private val pollDao: PollDao
): PollDataSource {
    
    override suspend fun savePolls(poll: Poll) {
        pollDao.insertPoll(
                poll = PollEntity(
                    subject = poll.pollConfig.subject,
                    nbGrading = poll.pollConfig.grading.getAmountOfGrades(),
                ),
            proposals = poll.pollConfig.proposals.map { proposal ->
                ProposalEntity(
                    name = proposal
                )
            },
            ballots = poll.ballots.toEntity()
        )
    }

    // TODO : Extract into Mapper to manage exception
    private fun List<Ballot>.toEntity(): List<List<JudgmentEntity>> {
        return this.map { ballot: Ballot ->
            ballot.judgments.map { JudgmentEntity(proposalIndex = it.proposal, gradeIndex = it.grade) }
        }
    }

    override suspend fun getAllPoll(): List<Poll> {
        val polls: MutableList<Poll> = mutableListOf()
        val pollWithProposals: List<PollWithProposals> = pollDao.loadPolls()
        pollWithProposals.forEach { poll ->
            val ballots: List<BallotWithJudgment> = pollDao.loadBallots(pollId = poll.poll.uid)
            val ballotsObject: MutableList<Ballot> = mutableListOf()
            ballots.forEach { ballot ->
                ballotsObject.add(
                    Ballot(
                        ballot.judgments.map { judgment ->
                            Judgment(
                                proposal = judgment.proposalIndex,
                                grade = judgment.gradeIndex
                            )
                        }
                    )
                )
            }
            polls.add(
                Poll(
                    id = poll.poll.uid,
                    pollConfig = PollConfig(
                        subject = poll.poll.subject,
                        proposals = poll.proposals.map { it.name },
                        grading = Quality7Grading(), // Todo Create proper mapper
                    ),
                    ballots = ballotsObject
                )
            )
        }
        return polls
    }

    override suspend fun deletePoll(poll: Poll) {
        val pollEntity = pollDao.loadPoll(poll.id)
        pollDao.deletePoll(pollEntity.poll)
    }
}