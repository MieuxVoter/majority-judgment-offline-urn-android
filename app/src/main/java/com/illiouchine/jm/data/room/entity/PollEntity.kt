package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("poll")
data class PollEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val subject: String,
    val nbGrading: Int, // this is the Grading's uid now — see com/illiouchine/jm/model/Grading.kt
)
