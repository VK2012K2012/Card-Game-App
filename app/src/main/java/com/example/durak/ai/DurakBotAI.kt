package com.example.durak.ai

import com.example.durak.game.DurakEngine
import com.example.durak.game.DurakGameState
import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.GameMode
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
            if (state.gamePhase == com.example.durak.game.GamePhase.WAITING_FOR_THROW_IN && botIndex in state.throwInPasses) {
                return BotMoveDecision.PassOrDone
            }
            val candidateCards = bot.hand.filter { card ->
                engine.canAttackWith(card, state.tablePairs, state.defenderHandSizeAtRoundStart)
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

            if (state.gameMode == GameMode.PEREVODNOY) {
                val transferCard = selectTransferCard(bot.hand, state, engine, botIndex)
                if (transferCard != null && shouldTransfer(transferCard, validDefenders, bot.difficulty)) {
                    return BotMoveDecision.Transfer(transferCard)
                }
            }

            if (validDefenders.isEmpty() || shouldTakeTable(validDefenders, attackCard, state, bot.difficulty)) {
                return BotMoveDecision.TakeTable
            }

            val chosenDefCard = selectDefendCard(validDefenders, attackCard, state.trumpSuit, bot.difficulty)
            return BotMoveDecision.Defend(chosenDefCard, undefendedIndex)
        }
    }

    private fun selectTransferCard(
        hand: List<Card>,
        state: DurakGameState,
        engine: DurakEngine,
        defenderIndex: Int,
    ): Card? {
        if (state.tablePairs.isEmpty() || state.tablePairs.any { it.isDefended }) return null
        val nextDefenderIndex = nextTransferDefenderIndex(state, defenderIndex) ?: return null
        if (state.players[nextDefenderIndex].hand.size < state.tablePairs.size + 1) return null
        val candidates = hand.filter { engine.canTransferWith(it, state.tablePairs) }
        return candidates.filterNot { it.isTrump }.minByOrNull { it.rank.value }
            ?: candidates.minByOrNull { it.rank.value }
    }

    private fun nextTransferDefenderIndex(state: DurakGameState, defenderIndex: Int): Int? {
        if (state.players.size < 3 || defenderIndex !in state.players.indices) return null
        for (offset in 1 until state.players.size) {
            val candidate = (defenderIndex + offset) % state.players.size
            if (!state.players[candidate].isOut && candidate != state.attackerIndex) return candidate
        }
        return null
    }

    private fun shouldTransfer(transferCard: Card, validDefenders: List<Card>, difficulty: BotDifficulty): Boolean {
        if (difficulty == BotDifficulty.EASY) return false
        if (validDefenders.isEmpty()) return true
        val cheapestDefend = validDefenders.minByOrNull { it.rank.value }
        return cheapestDefend != null && cheapestDefend.isTrump && !transferCard.isTrump
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
            BotDifficulty.HARD -> {
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
            BotDifficulty.MEDIUM, BotDifficulty.HARD -> {
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
        // Keep premium trumps out of low-value throw-ins. Expert starts conserving
        // from a Jack; Standard only holds back Kings and Aces.
        val remainingTrumps = candidates.filter { it.isTrump }
        val deckThreshold = if (difficulty == BotDifficulty.HARD) 8 else 10
        val rankThreshold = if (difficulty == BotDifficulty.HARD) 11 else 13
        if (remainingTrumps.size == candidates.size && state.deck.size > deckThreshold) {
            val lowestTrumpRank = remainingTrumps.minOfOrNull { it.rank.value } ?: 0
            if (lowestTrumpRank >= rankThreshold) return true
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
    data class Transfer(val card: Card) : BotMoveDecision()
    object PassOrDone : BotMoveDecision()
    object TakeTable : BotMoveDecision()
}
