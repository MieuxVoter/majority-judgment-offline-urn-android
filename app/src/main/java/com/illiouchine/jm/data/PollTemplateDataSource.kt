package com.illiouchine.jm.data

import android.content.Context
import com.illiouchine.jm.model.PollConfig

interface PollTemplateDataSource {
    suspend fun getBySlug(slug: String, context: Context): PollConfig
    suspend fun getAvailableSlugs(): List<String>
}
