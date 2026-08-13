package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_stats WHERE id = 1")
    fun getGameStats(): Flow<GameStatsEntity?>

    @Query("SELECT * FROM game_stats WHERE id = 1")
    suspend fun getGameStatsOnce(): GameStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameStats(stats: GameStatsEntity)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 50")
    fun getMatchHistory(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchHistory(match: MatchHistoryEntity)

    @Query("DELETE FROM match_history")
    suspend fun clearMatchHistory()

    @Transaction
    suspend fun recordFinishedMatch(match: MatchHistoryEntity, humanWon: Boolean, humanWasDurak: Boolean) {
        val current = getGameStatsOnce() ?: GameStatsEntity()
        saveGameStats(
            current.copy(
                totalGamesPlayed = current.totalGamesPlayed + 1,
                totalWins = current.totalWins + if (humanWon) 1 else 0,
                totalLossesDurak = current.totalLossesDurak + if (humanWasDurak) 1 else 0
            )
        )
        insertMatchHistory(match)
    }
}
