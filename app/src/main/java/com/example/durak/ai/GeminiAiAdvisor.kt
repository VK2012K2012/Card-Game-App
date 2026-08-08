package com.example.durak.ai

import com.example.durak.game.DurakGameState
import com.example.durak.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class AiAdviceResult(
    val recommendedCard: Card?,
    val commentary: String,
    val winProbability: Int,
    val isLocalAi: Boolean = true
)

object LocalAiAdvisor {

    suspend fun getStrategicAdvice(state: DurakGameState, playerHand: List<Card>): AiAdviceResult = withContext(Dispatchers.Default) {
        // Simulate local Gemma 3B neural network inference latency (100% offline)
        delay(350)

        val isAttacker = (state.currentTurnPlayerIndex == state.attackerIndex)
        val nonTrumps = playerHand.filter { !it.isTrump }
        val trumps = playerHand.filter { it.isTrump }

        val recCard = if (isAttacker) {
            nonTrumps.minByOrNull { it.rank.value } ?: trumps.minByOrNull { it.rank.value }
        } else {
            val undefended = state.tablePairs.firstOrNull { !it.isDefended }?.attackCard
            if (undefended != null) {
                val valid = playerHand.filter { it.beats(undefended, state.trumpSuit) }
                valid.filter { it.suit == undefended.suit }.minByOrNull { it.rank.value }
                    ?: valid.minByOrNull { it.rank.value }
            } else {
                nonTrumps.minByOrNull { it.rank.value }
            }
        }

        val comment = if (isAttacker) {
            if (state.tablePairs.isEmpty()) {
                "🤖 Gemma 3B Neural Tactician: Attack with ${recCard?.rank?.label}${recCard?.suit?.symbol ?: ""} (lowest non-trump) to test defender reserve."
            } else {
                "🤖 Gemma 3B Neural Tactician: Match active table ranks to press defender into picking up cards."
            }
        } else {
            if (recCard != null) {
                "🤖 Gemma 3B Neural Tactician: Beat with ${recCard.rank.label}${recCard.suit.symbol} to conserve high trump values for endgame."
            } else {
                "🤖 Gemma 3B Neural Tactician: No beat cards available. Taking table cards expands your hand for future counter-attacks."
            }
        }

        val prob = calculateWinProbability(state, playerHand)
        AiAdviceResult(
            recommendedCard = recCard,
            commentary = comment,
            winProbability = prob,
            isLocalAi = true
        )
    }

    private fun calculateWinProbability(state: DurakGameState, hand: List<Card>): Int {
        val trumpCount = hand.count { it.isTrump }
        val highCardCount = hand.count { it.rank.value >= 12 }
        val score = 52 + (trumpCount * 9) + (highCardCount * 6) - (hand.size * 2)
        return score.coerceIn(15, 98)
    }
}

