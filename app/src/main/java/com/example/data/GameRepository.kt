package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val stats: Flow<GameStatsEntity?> = gameDao.getGameStats()
    val matchHistory: Flow<List<MatchHistoryEntity>> = gameDao.getMatchHistory()

    suspend fun saveStats(stats: GameStatsEntity) = gameDao.saveGameStats(stats)

    suspend fun recordFinishedMatch(
        match: MatchHistoryEntity,
        humanWon: Boolean,
        humanWasDurak: Boolean
    ) = gameDao.recordFinishedMatch(match, humanWon, humanWasDurak)

    suspend fun clearHistory() = gameDao.clearMatchHistory()

    suspend fun getActiveMatch(): ActiveMatchEntity? = gameDao.getActiveMatch()

    suspend fun saveActiveMatch(match: ActiveMatchEntity) = gameDao.saveActiveMatch(match)

    suspend fun clearActiveMatch() = gameDao.clearActiveMatch()
}
