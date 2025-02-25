package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ballot",
    foreignKeys = [
        ForeignKey(
            entity = PollEntity::class,
            parentColumns = ["uid"],
            childColumns = ["pollId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BallotEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val pollId: Int = 0,
)