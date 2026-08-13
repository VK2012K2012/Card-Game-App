# Card Game Hub — Interaction Refinement v3

## Goal

This refinement responds directly to hands-on usability feedback. It replaces decorative density with a compact app shell, unmistakable primary actions, adaptive icon assets, and durable multi-player controls.

## Root navigation

The root shell keeps the three required destinations — **Play**, **Stats**, and **Settings** — but uses a compact, edge-to-edge navigation dock. It has a maximum 64 dp visual height, avoids labels and oversized containers, and relies on 32 dp icons with semantic descriptions. The selected destination receives a soft adaptive-color capsule; the other icons stay monochrome. The dock surface is transparent-to-surface, which lets it blend with the Android gesture/navigation area rather than creating a white band.

## Main menu

The Play destination becomes a calm, two-action landing screen.

| Action | Purpose | Behaviour |
|---|---|---|
| **Play Durak** | Starts the default local 2-player match immediately. | One large, full-width primary button. |
| **Match setup** | Opens player-count, deck, rules, and bot settings. | A second large outlined button, separated clearly from the primary action. |

The duplicate facts panel, tiny chips, percentage tag, and theme messaging are removed from the first view. Match setup retains accessible configuration but uses larger labelled buttons with clear selected-state copy rather than stock filter-chip affordances.

## Material 3 interaction

Pressing an action uses a scale-down-and-return animation with consistent expressive corners. No action changes unexpectedly to a square surface. All primary match controls carry an icon, a verb-first label, and are placed above the hand tray.

## In-match controls

Cards remain selectable in the hand. Playing never requires tapping a table target. A selected valid card exposes one explicit full-width action: **Play attack**, **Throw in**, or **Defend selected card**. During defense, the selected card is applied automatically to the first unmatched attack; the choice is clear in the button description. The table is informational only.

## Multi-player Durak turn model

Every non-defending player has a controlled chance to throw in after the defender answers an attack. The turn sequence cycles across active non-defenders rather than allowing a sequence of bots to resolve the round without offering the human player a turn. A bot that cannot or chooses not to throw in passes to the next participant; only when all eligible attackers pass does the original attacker receive the Clear/Bito action. This makes 3–4 player games playable instead of a bot-only sequence.

## Adaptive launcher icon

The regular adaptive foreground is black over a white background. Android 13+ themed-icon metadata remains monochrome, allowing Material You to recolor it when the launcher supports themed icons. App content still uses the system Material color scheme; it no longer depends on a permanent green brand seed.
