# Card Game Hub — Product and UI Architecture

## Purpose

Card Game Hub is a native Android card-game platform. **Durak is the first fully playable game**; the app structure must make future card games possible without making Durak feel like a temporary demo. The first release is intentionally offline-first: a player can always start a local match against deterministic bots. The app also exposes clear foundations for a future on-device AI opponent and online multiplayer, neither of which will block the core game.

## Material 3 Expressive Direction

The redesign applies Material 3 Expressive selectively, rather than placing every element in a large rounded card. The official guidance identifies **color, shape, size, motion, and containment** as the main expressive tools. It also recommends concentrating this treatment in one or two meaningful hero moments instead of making every surface compete for attention.[1] [2]

| Design concern | Implementation decision |
|---|---|
| Identity | A dynamic-color, game-night interface. A large, editorial home header establishes the game hub as the emotional entry point; the play table stays calmer so card state remains legible. |
| Hierarchy | The primary action is always a large, clearly labelled `Play Durak` / `Play selected card` control. Secondary actions live in a compact toolbar, chips, or a tonal button. |
| Shape | Use an intentional mix of rounded containers and a distinctive spade-shaped mark only in the app identity/hero. Match-critical controls retain familiar, high-contrast Material shapes. |
| Color | Dynamic system color remains supported on Android 12+, with a restrained deep-forest fallback palette. Semantic red is reserved for taking cards and invalid/error states; trump is a warm gold accent. |
| Containment | Group match choices in a setup sheet, current-turn status in a small table-status rail, and cards within a dedicated table field. Do not surround every text block with a card. |
| Motion | Use Material expressive motion only for card selection, table updates, and screen transitions. Interactions must remain readable and must not delay play. |
| Accessibility | Preserve text labels, comfortable target sizes, dynamic colors, clear selected state, and visible turn/status language. |

## Screen Structure

| Screen | Job | Primary action | Key state |
|---|---|---|---|
| **Home / Game library** | Make Durak immediately playable while showing the platform can expand. | `Play Durak` | Local game progress, recent stats, available games. |
| **Durak setup sheet** | Configure a valid local match without overwhelming the home screen. | `Start match` | Player count, rules variant, deck size, difficulty, opponent engine. |
| **Durak table** | Make turn, cards, legal actions, and game progress obvious at a glance. | Contextual `Attack`, `Defend`, `Take`, or `Finish round` | Current turn, table pairs, selected card, deck, trump, player hand. |
| **Match result** | End the match clearly and offer a next step. | `Play again` | Winner, Durak, rounds, stats save status. |
| **Activity** | Show persistent stats and match history. | `Play a match` | Aggregated statistics and history. |
| **Settings** | Describe local-first game modes and provide stable preferences as they are implemented. | Back/navigation | Theme information and future availability. |

## Offline, Smart AI, and Multiplayer Foundation

The local release has a reliable **Classic bot** engine with easy, standard, and expert behaviour. The setup model introduces an opponent engine selection:

| Opponent type | Release behaviour | Extensibility boundary |
|---|---|---|
| Classic bot | Fully playable and offline. | `DurakBotAI` remains the deterministic strategy. |
| Smart on-device bot | Presented as a future on-device model capability with a safe Classic-bot fallback. No remote request is made. | A `BotStrategy`/`OpponentEngine` boundary will accept a bundled model-backed strategy later without rewriting screens or Durak rules. |
| Multiplayer | Clearly separated from local play and marked unavailable until a secure backend, rooms, reconnection, synchronization, and cheat prevention are designed. | A future `MatchTransport` abstraction can connect the existing game reducer to a server-authoritative match. |

## Required Cleanup and Bug Fixes

1. **Prevent stale bot jobs** when a player starts a new game or leaves the match. Bot turns must be tied to a session token and cancelled on reset.
2. **Record a finished match exactly once.** A game-over guard must stop duplicate Room writes and inflated statistics.
3. **Pass the selected difficulty to every bot** in a three- or four-player match, rather than only bot one.
4. **Make match setup valid and explicit.** Support only 2–4 local players, 24/36/52-card decks, and the rule variants that the engine can actually execute.
5. **Remove dead settings presentation.** Settings must be an honest product screen, not controls that imply functionality does not exist.
6. **Reduce accidental gameplay actions.** A selected card must only be playable for the current legal role; defense targets must expose a clear selected/valid state.
7. **Build game state around a reducer-style rule engine.** The UI must not mutate game state directly, and state transitions should be testable without Compose.
8. **Keep game rules separate from visual cards and navigation.** Future game modules and network transports should not require rewriting Durak rendering.

## Implementation Boundaries

This work does not claim to ship an offline AI model or real multiplayer in the first rebuild. Those require model selection/packaging and a secure, server-authoritative multiplayer service respectively. The rebuild makes the product honest and technically ready for both while delivering a polished, fully offline Durak game now.

## References

[1]: https://m3.material.io/blog/building-with-m3-expressive "Material Design — Start building with Material 3 Expressive"
[2]: https://design.google/library/expressive-material-design-google-research "Google Design — Better, Easier, Emotional UX"
[3]: https://m3.material.io/develop/android/jetpack-compose "Material Design — Jetpack Compose"
