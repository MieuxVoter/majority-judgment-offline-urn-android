package com.illiouchine.jm.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.illiouchine.jm.data.room.entity.BallotEntity
import com.illiouchine.jm.data.room.entity.JudgmentEntity
import com.illiouchine.jm.data.room.entity.PollEntity
import com.illiouchine.jm.data.room.entity.ProposalEntity

@Database(
    entities = [
        PollEntity::class,
        ProposalEntity::class,
        BallotEntity::class,
        JudgmentEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class PollDataBase : RoomDatabase() {
    abstract fun pollDao(): PollDao
}
