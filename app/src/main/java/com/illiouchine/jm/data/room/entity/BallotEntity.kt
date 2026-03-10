package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "ballot",
    foreignKeys = [
        ForeignKey(
            entity = PollEntity::class,
            parentColumns = ["uid"],
            childColumns = ["pollId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["pollId"])],
)
data class BallotEntity(
    // Locally Unique Identifier, ignored when daisy-chaining (different on each chained device).
    // TBD: In the long run we should aim to drop this property altogether and use uuid everywhere?
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    // Universally Unique Identifier, used for daisy-chaining (same on all chained devices).
    // Not actually 100% guaranteed to be unique ; collisions may happen, but are unlikely.
    val uuid: UUID? = UUID.randomUUID(),
    val pollId: Int = 0,
)
