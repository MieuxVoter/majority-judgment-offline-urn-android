package com.illiouchine.jm.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
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
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = PollDataBase.AutoMigration1To2::class,
        ),
    ]
)
abstract class PollDataBase : RoomDatabase() {
    @RenameColumn.Entries(
        RenameColumn(
            tableName = "poll",
            fromColumnName = "nbGrading",
            toColumnName = "gradingUid",
        ),
    )
    class AutoMigration1To2 : AutoMigrationSpec

    abstract fun pollDao(): PollDao
}
