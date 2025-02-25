package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "proposal",
    foreignKeys = [
        ForeignKey(
            entity = PollConfigEntity::class,
            parentColumns = ["uid"],
            childColumns = ["pollConfigId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProposalEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val pollConfigId: Int = 0,
    val name: String
)