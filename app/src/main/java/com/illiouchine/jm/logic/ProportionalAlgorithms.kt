package com.illiouchine.jm.logic

import android.content.Context
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.service.OsmosisRepartitor
import fr.mieuxvoter.mj.ResultInterface


// NOTE: we could use a sealed class instead of an enum
enum class ProportionalAlgorithms {

    NONE {
        override fun getName(context: Context): String {
            return context.getString(R.string.proportional_algorithm_none)
        }

        override fun getDescription(context: Context): String {
            return ""
        }

        override fun getFeatures(context: Context): String {
            return ""
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun compute(poll: Poll, result: ResultInterface): List<Double> {
            // It does not really matter what we compute here ; it should never be shown.
            return List(
                size = poll.pollConfig.proposals.size,
                init = {
                    if (poll.pollConfig.proposals.isNotEmpty()) {
                        1.0 / poll.pollConfig.proposals.size
                    } else {
                        0.0
                    }
                }
            )
        }
    },

    MJ_SCORE {
        override fun getName(context: Context): String {
            return context.getString(R.string.proportional_algorithm_mj_score)
        }

        override fun getDescription(context: Context): String {
            return context.getString(R.string.proportional_algorithm_mj_score_description)
        }

        override fun getFeatures(context: Context): String {
            return context.getString(R.string.proportional_algorithm_mj_score_features)
        }

        override fun isAvailable(): Boolean {
            return false
        }

        override fun compute(poll: Poll, result: ResultInterface): List<Double> {
            throw NotImplementedError() // TODO
        }
    },

    OSMOTIC_FAVORITISM {
        override fun getName(context: Context): String {
            return context.getString(R.string.proportional_algorithm_osmotic_favoritism)
        }

        override fun getDescription(context: Context): String {
            return context.getString(R.string.proportional_algorithm_osmotic_favoritism_description)
        }

        override fun getFeatures(context: Context): String {
            return context.getString(R.string.proportional_algorithm_osmotic_favoritism_features)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun compute(poll: Poll, result: ResultInterface): List<Double> {
            return OsmosisRepartitor().computeProportionalRepresentation(poll)
        }
    },

    ;

    /**
     * The short name of the proportional algorithm.
     */
    abstract fun getName(context: Context): String

    /**
     * Long description for the algorithm, shown on the help screen.
     */
    abstract fun getDescription(context: Context): String

    abstract fun getFeatures(context: Context): String

    /**
     * Whether or not this algorithm is available.
     */
    abstract fun isAvailable(): Boolean

    /**
     * The output list is sorted in order of the input proposals.
     * It ought to be normalized (the sum of its elements must be 1.0).
     * Except for NONE, where the list might be empty (or full of zeros).
     */
    abstract fun compute(poll: Poll, result: ResultInterface): List<Double>
}

// Can't figure out how to make this work
//abstract class Proportions : List<Double>
