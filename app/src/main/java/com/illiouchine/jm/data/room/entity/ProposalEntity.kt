package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proposal",
    foreignKeys = [
        ForeignKey(
            entity = PollEntity::class,
            parentColumns = ["uid"],
            childColumns = ["pollId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pollId"])]
)
data class ProposalEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val pollId: Int = 0,
    val name: String
)
