# Card Game Hub — Product and UI Architecture

## Purpose

Card Game Hub is a native Android home for a growing collection of card games. Five titles are currently playable offline: Durak against deterministic bots, plus Crazy Eights, War, Go Fish, and Blackjack as pass-and-play sessions. The library, activity, appearance settings, and navigation belong to the hub; each game's rules and table remain game-specific. Durak match results are persisted; the four additional sessions are currently in-memory only. Online multiplayer and remote AI are not part of this release.

## Material 3 Expressive Direction

The 1.1.5 redesign treats expression as hierarchy rather than a blanket decoration. Official Material guidance describes semantic color roles and theming, expressive component and motion updates, a shared typography scale, and a graduated shape system. The implementation concentrates game-specific identity in the library's featured card and custom card-fan artwork; repetitive navigation, setup, settings, history, and match actions remain familiar and legible.[1] [2] [3] [4]

Material 3 Expressive remains the default. Design Customization also offers a locally persisted One UI-inspired shape treatment implemented with standard Android and Compose APIs; it is not an official Samsung component library. In M3E mode, low-contrast blurred ambient color is decorative and confined to the home backdrop, leaving text and controls sharp.

| Design concern | Implementation decision |
|---|---|
| Identity | A forest-and-ivory fallback palette with warm-gold accent communicates a game-night table; Android dynamic color remains supported on Android 12+. |
| Hierarchy | The game-library screen has one featured playable action, a concise library listing, and a compact local record. Inside a match, contextual controls remain explicit: `Attack`, `Defend`, `Take`, or `Finish round`. |
| Color | Themed controls use matched Material `on*` and container roles. Surface-container levels group content; red remains semantic for errors/taking cards, and gold is a complementary accent, never the only state cue. |
| Typography | Display/headline styles are reserved for screen identity and hero content; title, body, and label roles cover sections, descriptions, and controls consistently across screens. |
| Shape | Shared Material shape tokens form the geometry system; the larger expressive shape is reserved for the featured game and setup sheet. Standard controls keep recognizable component silhouettes. |
| Containment | Match choices live in a native Material bottom sheet; current-turn information and the play area occupy clear regions; library and history use compact, subject-focused surfaces. Not every text block receives its own card. |
| Motion | Material's expressive motion scheme remains in use for key interactions, while custom movement is limited so selection and gameplay remain immediate and readable. |
| Accessibility | Native Material controls retain standard semantics and touch targets; decorative artwork is hidden from assistive technology; visible text and icon cues accompany game states and selections. |
| Adaptivity | The shell uses its current window width: compact windows use a Material navigation bar; expanded windows use a Material navigation rail beside a width-constrained content area. |

## Screen Structure

| Screen | Job | Primary action | Key state |
|---|---|---|---|
| **Home / Game library** | Present the card-game collection and make available local titles easy to start. | `Start a match` or browse games | Featured Durak, four pass-and-play titles, local record. |
| **Durak setup sheet** | Configure a valid local match without overwhelming the home screen. | `Start match` | Player count, rules variant, deck size, difficulty, opponent engine. |
| **Durak table** | Make turn, cards, legal actions, and game progress obvious at a glance. | Contextual `Attack`, `Defend`, `Take`, or `Finish round` | Current turn, table pairs, selected card, deck, trump, player hand. |
| **Local game sessions** | Play Crazy Eights, War, Go Fish, or Blackjack on one device. | Select player count, then pass the device | Per-game engine state; not persisted to Durak history. |
| **Match result** | End the match clearly and offer a next step. | `Play again` | Winner, Durak, rounds, stats save status. |
| **Activity** | Show persistent stats and match history. | `Play a match` | Aggregated statistics and history. |
| **Settings** | Offer appearance preferences, release information, and a clear local-storage note. | Open design options or app details | Theme/palette, navigation labels, typography, local-first statement. |

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

This release ships four additional local game engines and pass-and-play screens alongside Durak. It does not ship Hearts or Spades (still planned), an on-device or remote AI model, or real nearby/online multiplayer. Those would require completed game rules and UI, model packaging or a protected backend, and a secure server-authoritative transport respectively.

## References

[1]: https://developer.android.com/develop/ui/compose/designsystems/material3 "Android Developers — Material 3 in Jetpack Compose"
[2]: https://m3.material.io/styles/color/roles "Material Design — Color roles"
[3]: https://m3.material.io/styles/motion/overview/how-it-works "Material Design — Motion physics system"
[4]: https://developer.android.com/develop/ui/compose/accessibility/api-defaults "Android Developers — Accessibility API defaults in Compose"
[5]: https://developer.android.com/develop/adaptive-apps/guides/support-different-display-sizes "Android Developers — Support different display sizes"
