package com.illiouchine.jm.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
// ADR: wanted to use Kotlin's Uuid but there's no type converter for it in room yet
// import kotlin.uuid.Uuid
// So we use java.util.UUID and it's going to be just fine.  (ou pas)

@Entity("poll")
data class PollEntity(
    // Locally Unique Identifier, ignored when daisy-chaining (different on each chained device).
    // TBD: In the long run we should aim to drop this property altogether and use uuid everywhere?
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    // Universally Unique Identifier, used for daisy-chaining (same on all chained devices).
    // Not actually guaranteed to be unique ; collisions may happen, but are unlikely.
    // Note: Nullable for now (necessity), with the long-term goal of making it not nullable.
    val uuid: UUID? = UUID.randomUUID(),
    val subject: String,
    val gradingUid: Int, // the Grading's uid is hardcoded — see com/illiouchine/jm/model/Grading.kt
)
