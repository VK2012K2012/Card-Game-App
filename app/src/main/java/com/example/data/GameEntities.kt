package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalGamesPlayed: Int = 0,
    val totalWins: Int = 0,
    val totalLossesDurak: Int = 0,
    val totalBitoClears: Int = 0,
    val totalTrumpsPlayed: Int = 0,
    val selectedThemePalette: String = "EMERALD",
    val selectedFeltStyle: String = "CLASSIC_FELT",
    val selectedCardBack: String = "RED_SCROLL",
    val selectedDeckSize: Int = 36,
    val isSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val useAiBotByDefault: Boolean = false
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val matchId: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val gameType: String = "Durak",
    val playerPosition: String, // "1st Place (Winner)", "Durak (Fool)", etc.
    val opponentCount: Int,
    val botDifficulty: String,
    val isWin: Boolean,
    val roundsPlayed: Int
)
