# Card Game Hub — Fourth Refinement Specification

## Navigation

The default navigation will use a short, labeled bottom bar inspired by the supplied Play Store reference. It will have three destinations—Play, Stats, and Settings—distributed evenly across the width. Each destination will retain its icon and label. Only the active destination will receive a low, wide, highly rounded pill indicator, with the app’s Material dynamic color used for the active container and content. The bottom surface will extend behind the gesture navigation area, while the content portion of the bar remains deliberately shorter than the earlier oversized implementation.

The current icon-only compact dock will remain as an alternative presentation rather than being discarded. It will be selectable under a new Design Customization destination. The application will persist the selection locally and immediately apply it across the root destinations.

## Motion and touch feedback

Press and selection morphs will use an intermediate expressive rounded shape rather than a square. Containers will animate between soft rounded values, preserving the pill-like family of the component while making the press response feel more tactile. This applies to primary buttons, navigation indicators, and settings-choice previews.

## Settings structure

The Settings destination will become a short directory. It will contain only two entry points: Design Customization and About app. The former controls the two bottom-navigation appearances. The latter contains the app name, version, GitHub/source link, and the developer credit. The broken memory diagnostic feature and the redundant Durak/about content on the top-level Settings page will be removed.

## Match turns

A legal selected card must be accepted during both the opening attack and a human throw-in. The ViewModel will no longer incorrectly restrict a human throw-in to the opening attacker. In a multi-player round, bots will execute all consecutive legal bot turns as a short batch before the human is asked to act. A human is returned control only when it is genuinely the human’s turn to throw in, defend, attack, take, or clear Bito. The round will not clear until every active non-defender has had a turn and passed or played.

