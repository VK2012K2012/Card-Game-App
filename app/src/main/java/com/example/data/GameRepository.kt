package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val stats: Flow<GameStatsEntity?> = gameDao.getGameStats()
    val matchHistory: Flow<List<MatchHistoryEntity>> = gameDao.getMatchHistory()

    suspend fun saveStats(stats: GameStatsEntity) {
        gameDao.saveGameStats(stats)
    }

    suspend fun recordMatch(match: MatchHistoryEntity, isWin: Boolean, isDurak: Boolean) {
        gameDao.insertMatchHistory(match)
        // Update stats
        // We will fetch current or default
    }

    suspend fun clearHistory() {
        gameDao.clearMatchHistory()
    }
}
