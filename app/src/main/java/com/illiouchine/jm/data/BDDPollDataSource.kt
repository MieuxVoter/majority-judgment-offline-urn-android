package com.illiouchine.jm.data

import com.illiouchine.jm.data.room.PollDao
import com.illiouchine.jm.model.Poll

class BDDPollDataSource(
    private val pollDao: PollDao
): PollDataSource {

    override fun savePolls(poll: Poll) {
//        pollDao.insertPollFull(
//            pollFull = PollWithConfigAndBallots(
//                poll = PollEntity(),
//                pollConfigWithProposals = PollConfigWithProposals(
//                    pollConfig = PollConfigEntity(
//                        subject = poll.pollConfig.subject,
//                        nbGrading = poll.pollConfig.grading.getAmountOfGrades(),
//                    ),
//                    proposals = poll.pollConfig.proposals.map { proposal ->
//                        ProposalEntity(
//                            name = proposal
//                        )
//                    }
//                ),
//                ballotsWithJudgment = poll.ballots.map { ballot ->
//                    BallotWithJudgment(
//                        ballot = BallotEntity(),
//                        judgments = ballot.judgments.map { judgment ->
//                            JudgmentEntity(
//                                proposalIndex = judgment.proposal,
//                                gradeIndex = judgment.grade
//                            )
//                        }
//                    )
//                }
//            )
//        )
    }

    override fun getAllPoll(): List<Poll> {
//        val pollsWithConfigAndBallots = pollDao.loadPolls()
//        val polls = pollsWithConfigAndBallots.map { pollWithConfigAndBallots ->
//            Poll(
//                pollConfig = PollConfig(
//                    subject = pollWithConfigAndBallots.pollConfigWithProposals.pollConfig.subject,
//                    grading = Quality7Grading(), // TODO
//                    proposals = pollWithConfigAndBallots.pollConfigWithProposals.proposals.map {
//                        it.name
//                    }
//                ),
//                ballots = pollWithConfigAndBallots.ballotsWithJudgment.map { ballot ->
//                    Ballot(
//                        judgments = ballot.judgments.map { judgment ->
//                            Judgment(
//                                proposal = judgment.proposalIndex,
//                                grade = judgment.gradeIndex
//                            )
//                        }
//                    )
//                }
//            )
//        }
        return emptyList()
    }

    override fun deletePoll(poll: Poll) {
        TODO("Not yet implemented")
    }
}