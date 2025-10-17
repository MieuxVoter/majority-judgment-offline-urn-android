package com.illiouchine.jm.data

import android.content.Context
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.PollConfig

class HardcodedPollTemplateDataSource: PollTemplateDataSource {

    override suspend fun getAvailableSlugs(): List<String> {
        return listOf(
            "meal",
            "holiday",
            "project",
        )
    }

    override suspend fun getBySlug(slug: String, context: Context): PollConfig {
        return when(slug) {
            "meal" -> {
                PollConfig(
                    subject = context.getString(R.string.poll_template_meal_subject),
                    proposals = listOf(
                        context.getString(R.string.poll_template_meal_proposal_pizza),
                        context.getString(R.string.poll_template_meal_proposal_pasta),
                        context.getString(R.string.poll_template_meal_proposal_rice),
                        context.getString(R.string.poll_template_meal_proposal_veggies),
                        context.getString(R.string.poll_template_meal_proposal_bread),
                        context.getString(R.string.poll_template_meal_proposal_soup),
                    ),
                    grading = Grading.Quality5Grading,
                )
            }
            "holiday" -> {
                PollConfig(
                    subject = context.getString(R.string.poll_template_holiday_subject),
                    proposals = listOf(
                        context.getString(R.string.poll_template_holiday_proposal_mountain),
                        context.getString(R.string.poll_template_holiday_proposal_forest),
                        context.getString(R.string.poll_template_holiday_proposal_ocean),
                        context.getString(R.string.poll_template_holiday_proposal_theme_park),
                        context.getString(R.string.poll_template_holiday_proposal_metropolis),
                        context.getString(R.string.poll_template_holiday_proposal_moon),
                    ),
                    grading = Grading.Quality7Grading,
                )
            }
            "project" -> {
                PollConfig(
                    subject = context.getString(R.string.poll_template_project_subject),
                    proposals = listOf(
                        context.getString(R.string.poll_template_project_proposal_raise_awareness),
                        context.getString(R.string.poll_template_project_proposal_levy_funds),
                        context.getString(R.string.poll_template_project_proposal_build_a_toolkit),
                        context.getString(R.string.poll_template_project_proposal_call_for_allies),
                        context.getString(R.string.poll_template_project_proposal_craft_a_solution),
                    ),
                    grading = Grading.PositiveQuality5Grading,
                )
            }
            else -> {
                PollConfig(
                    subject = "Best Bugs",
                    proposals = listOf(
                        "Bugs",
                        "Bohr Bugs",
                        "HeisenBugs",
                    ),
                    grading = Grading.Enthusiasm6Grading,
                )
            }
        }
    }

}