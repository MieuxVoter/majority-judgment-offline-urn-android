package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Judgment",
    foreignKeys = [
        ForeignKey(
            entity = BallotEntity::class,
            parentColumns = ["uid"],
            childColumns = ["ballotId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ballotId"])]
)
data class JudgmentEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val ballotId: Int = 0,
    val proposalIndex: Int,
    val gradeIndex: Int
)