package com.example.durak.ai

import com.example.durak.game.DurakEngine
import com.example.durak.game.DurakGameState
import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.Suit
import com.example.durak.model.TablePair

object DurakBotAI {

    fun decideBotMove(
        state: DurakGameState,
        botIndex: Int,
        engine: DurakEngine
    ): BotMoveDecision {
        val bot = state.players[botIndex]
        val isAttacker = (botIndex != state.defenderIndex)
        val defender = state.players[state.defenderIndex]

        if (isAttacker) {
            val candidateCards = bot.hand.filter { card ->
                engine.canAttackWith(card, state.tablePairs, defender.hand.size)
            }

            if (candidateCards.isEmpty() || (state.tablePairs.isNotEmpty() && shouldPassAttack(candidateCards, state, bot.difficulty))) {
                return BotMoveDecision.PassOrDone
            }

            val chosenCard = selectAttackCard(candidateCards, state.trumpSuit, bot.difficulty)
            return BotMoveDecision.Attack(chosenCard)
        } else {
            // Defender turn: find undecended pairs
            val undefendedIndex = state.tablePairs.indexOfFirst { !it.isDefended }
            if (undefendedIndex == -1) return BotMoveDecision.PassOrDone

            val attackCard = state.tablePairs[undefendedIndex].attackCard
            val validDefenders = bot.hand.filter { card ->
                engine.canDefendWith(card, attackCard, state.trumpSuit)
            }

            if (validDefenders.isEmpty() || shouldTakeTable(validDefenders, attackCard, state, bot.difficulty)) {
                return BotMoveDecision.TakeTable
            }

            val chosenDefCard = selectDefendCard(validDefenders, attackCard, state.trumpSuit, bot.difficulty)
            return BotMoveDecision.Defend(chosenDefCard, undefendedIndex)
        }
    }

    private fun selectAttackCard(candidates: List<Card>, trumpSuit: Suit, difficulty: BotDifficulty): Card {
        return when (difficulty) {
            BotDifficulty.EASY -> candidates.random()
            BotDifficulty.MEDIUM -> {
                // Prefer lowest non-trump, then lowest trump
                val nonTrumps = candidates.filter { !it.isTrump }
                if (nonTrumps.isNotEmpty()) {
                    nonTrumps.minByOrNull { it.rank.value }!!
                } else {
                    candidates.minByOrNull { it.rank.value }!!
                }
            }
            BotDifficulty.HARD, BotDifficulty.LOCAL_NEURAL_AI -> {
                // Save trumps for endgame, pick non-trump with lowest value
                val nonTrumps = candidates.filter { !it.isTrump }
                if (nonTrumps.isNotEmpty()) {
                    nonTrumps.minByOrNull { it.rank.value }!!
                } else {
                    candidates.minByOrNull { it.rank.value }!!
                }
            }
        }
    }

    private fun selectDefendCard(candidates: List<Card>, attackCard: Card, trumpSuit: Suit, difficulty: BotDifficulty): Card {
        return when (difficulty) {
            BotDifficulty.EASY -> candidates.random()
            BotDifficulty.MEDIUM, BotDifficulty.HARD, BotDifficulty.LOCAL_NEURAL_AI -> {
                // Same suit defenders first, then lowest trump
                val sameSuit = candidates.filter { it.suit == attackCard.suit }
                if (sameSuit.isNotEmpty()) {
                    sameSuit.minByOrNull { it.rank.value }!!
                } else {
                    val trumps = candidates.filter { it.isTrump }
                    if (trumps.isNotEmpty()) {
                        trumps.minByOrNull { it.rank.value }!!
                    } else {
                        candidates.minByOrNull { it.rank.value }!!
                    }
                }
            }
        }
    }

    private fun shouldPassAttack(candidates: List<Card>, state: DurakGameState, difficulty: BotDifficulty): Boolean {
        if (difficulty == BotDifficulty.EASY) return false
        // If remaining candidates are high trumps early in game, consider passing
        val remainingTrumps = candidates.filter { it.isTrump }
        if (remainingTrumps.size == candidates.size && state.deck.size > 10) {
            val lowestTrumpRank = remainingTrumps.minOfOrNull { it.rank.value } ?: 0
            if (lowestTrumpRank >= 12) return true // Save Q, K, A trumps
        }
        return false
    }

    private fun shouldTakeTable(candidates: List<Card>, attackCard: Card, state: DurakGameState, difficulty: BotDifficulty): Boolean {
        if (candidates.isEmpty()) return true
        if (difficulty == BotDifficulty.EASY) return false

        // If beating requires sacrificing a high trump Ace when table has many cards, take
        if (candidates.size == 1 && candidates[0].isTrump && candidates[0].rank.value >= 13 && state.tablePairs.size > 3 && state.deck.size > 12) {
            return true
        }
        return false
    }
}

sealed class BotMoveDecision {
    data class Attack(val card: Card) : BotMoveDecision()
    data class Defend(val card: Card, val pairIndex: Int) : BotMoveDecision()
    object PassOrDone : BotMoveDecision()
    object TakeTable : BotMoveDecision()
}
