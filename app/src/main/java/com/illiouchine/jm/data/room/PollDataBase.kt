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
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = PollDataBase.AutoMigration1To2::class,
        ),
        AutoMigration(
            from = 2,
            to = 3,
            spec = PollDataBase.AutoMigration2To3::class,
        ),
    ],
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

    // Add Uuids to Polls and Ballots
    class AutoMigration2To3 : AutoMigrationSpec

    abstract fun pollDao(): PollDao
}

// Next migration, not an AutoMigration this time
// val MIGRATION_3_4 = object : Migration(3, 4) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        // Fill up the NULL uuids with random values
//        db.execSQL("UPDATE …")
//    }
// }
