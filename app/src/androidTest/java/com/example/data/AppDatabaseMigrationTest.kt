package com.example.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    @Test
    fun migrationPreservesExistingStatisticsAndHistoryAndCreatesActiveMatchTable() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "migration-test.db"
        context.deleteDatabase(databaseName)
        val databaseFile: File = context.getDatabasePath(databaseName)
        databaseFile.parentFile?.mkdirs()

        SQLiteDatabase.openOrCreateDatabase(databaseFile, null).use { oldDatabase ->
            oldDatabase.execSQL(
                """CREATE TABLE IF NOT EXISTS `game_stats` (
                    `id` INTEGER NOT NULL,
                    `totalGamesPlayed` INTEGER NOT NULL,
                    `totalWins` INTEGER NOT NULL,
                    `totalLossesDurak` INTEGER NOT NULL,
                    `totalBitoClears` INTEGER NOT NULL,
                    `totalTrumpsPlayed` INTEGER NOT NULL,
                    `selectedDeckSize` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )""".trimIndent()
            )
            oldDatabase.execSQL(
                """CREATE TABLE IF NOT EXISTS `match_history` (
                    `matchId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `gameType` TEXT NOT NULL,
                    `playerPosition` TEXT NOT NULL,
                    `opponentCount` INTEGER NOT NULL,
                    `botDifficulty` TEXT NOT NULL,
                    `isWin` INTEGER NOT NULL,
                    `roundsPlayed` INTEGER NOT NULL
                )""".trimIndent()
            )
            oldDatabase.execSQL("INSERT INTO game_stats VALUES (1, 14, 8, 2, 30, 4, 36)")
            oldDatabase.execSQL(
                "INSERT INTO match_history VALUES (7, 1700000000000, 'Durak', 'Winner', 1, 'MEDIUM', 1, 5)"
            )
            oldDatabase.version = 1
        }

        val migrated = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
        try {
            migrated.openHelper.writableDatabase
            assertEquals(14, migrated.gameDao().getGameStatsOnce()?.totalGamesPlayed)
            assertEquals(8, migrated.gameDao().getGameStatsOnce()?.totalWins)
            assertEquals("Winner", migrated.gameDao().getMatchHistory().first().single().playerPosition)
            assertNull(migrated.gameDao().getActiveMatch())
        } finally {
            migrated.close()
            context.deleteDatabase(databaseName)
        }
    }
}
