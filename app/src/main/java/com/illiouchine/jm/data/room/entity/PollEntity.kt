package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("poll")
data class PollEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val subject: String,
    val gradingUid: Int, // the Grading's uid is hardcoded — see com/illiouchine/jm/model/Grading.kt
)
