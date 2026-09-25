# Wolf Control

Summon wolves that stand perfectly still, and carry a detector that wraps every
wolf nearby in a giant orange box.

This file is read automatically whenever Claude Code is opened in this folder.
Everything below is specific to this one mod. The general rules about how to
work with Elduin live in `~/.claude/CLAUDE.md`.

## Facts about this mod

    mod id            wolf_control          (underscores — never change this)
    slug              wolf-control          (repo name and Modrinth slug)
    package           com.elduin.wolf_control
    loader            fabric                (only fabric — see below)
    minecraft         1.19.4
    primary version   1.19.4                (the one he plays)
    java              17 — Gradle picks this per version

## Why 1.19.4 and nothing newer

He plays this on a **Vivecraft (VR) server**, on PC through Meta Horizon.
Vivecraft supports Minecraft 1.7.10 and 1.10 through **1.20.1** — there is no VR
build for 1.21 or later. So 1.19.4 is the target and bumping it would take the
mod out of VR. Do not "helpfully" update this to a newer Minecraft.

The Fabric Loader floor in `stonecutter.properties.toml` is deliberately an old
`0.14.24`, because a Vivecraft-era Fabric install will have a 0.14.x loader and a
higher floor would stop the jar loading.

Two consequences for the code:

- `FabricLoader.getRawGameVersion()` does not exist on 0.14.x. Read the version
  off the `minecraft` mod container instead (see `FabricPlatform`).
- 1.19.4 is Mojang mappings of that era: `ResourceLocation` (not `Identifier`),
  `new ResourceLocation(ns, path)` (not `fromNamespaceAndPath`), `entity.level`
  as a field (not `level()`), `ServerPlayer.getLevel()` (not `serverLevel()`),
  and `CreativeModeTabs.TOOLS_AND_UTILITIES` is a `CreativeModeTab`, not a
  `ResourceKey`.

Because there is only one Minecraft version, the `replacements` block in
`build.fabric.gradle.kts` was removed — there is nothing to translate between.

## What the mod actually does

    WolfDetectorItem   right-click  -> creative mode + elytra in an empty chest slot
                       sneak-right  -> back to survival, detector still works
    WolfCallerItem     right-click a block  -> spawn a tamed wolf with setNoAi(true)
                       sneak-right a block  -> teleport your still wolves there
    WolfBoxRenderer    client: orange line box round every wolf within 64 blocks,
                       drawn only while a detector is in either hand
    WolfGlow           server: vanilla Glowing + an orange (GOLD) scoreboard team
                       on those wolves, so the outline shows through walls and
                       works for players who have not installed the mod
    StarterChest       on join with no detector, place a filled chest in front

The mod id is baked into save files. Once a world has been played with this mod,
**changing the mod id breaks that world.** Rename the display name freely;
never rename the mod id.

## Layout

Multi-version is handled by [Stonecutter](https://plugins.gradle.org/plugin/dev.kikugie.stonecutter):
one source tree, version-conditional comments, many outputs.

    src/main/java/<package>/                the mod
    src/main/resources/                     assets, textures, mixins, lang
    versions/<mcversion>-fabric/build/libs/ built jars land here
    stonecutter.properties.toml             mod id, name, version, dependencies
    settings.gradle.kts                     the Minecraft version list
    .github/workflows/release.yml           builds and publishes on a version tag

There is **no `fabric.mod.json` file** — it is generated at build time from
`stonecutter.properties.toml` by the code in `build-logic/`. Editing mod
metadata means editing the `.toml`, not a json file. Same for `mod.version`:
there is no `mod_version` in `gradle.properties`.

Stonecutter subprojects are named `<mcversion>-fabric`, so the jar is in
`versions/1.19.4-fabric/build/libs/`. That `-fabric` suffix is easy to forget.

Do **not** add a branch or a repo for a new Minecraft version. Add it to the
list in `settings.gradle.kts`, add a matching `[fabric."<version>"]` block in
`stonecutter.properties.toml`, add it to the matrix in
`.github/workflows/release.yml`, and fix whatever stops compiling.

## Fabric only

This template builds Fabric and nothing else. NeoForge and Forge were removed on
purpose: each extra loader is another full copy of Minecraft to decompile, and
this is an 8 GB machine. Do not add them back.

For the same reason `gradle.properties` sets `org.gradle.parallel=false`.
Leave it off. Turning it on with more than one Minecraft version in the list
will exhaust memory and take the whole machine down.

## Commands

    ./gradlew "Set active project to 1.19.4-fabric"    switch versions first
    ./gradlew "1.19.4-fabric:build"                    build just that version
    ./gradlew build                                    build every version
    ./gradlew runActiveClient                          launch a dev client

Switching rewrites the shared source tree into that version's form. It is **not**
required before building — each version subproject regenerates its own sources,
so the jars are correct either way. Switch to keep the working tree in the
version you're reading, not because the build needs it.

Never hand-edit `.sc_active_version`. Stonecutter records what form the shared
sources are currently in, and editing that file behind its back desyncs the
bookkeeping — you get `cannot find symbol` errors on classes that plainly exist.
Use the task above and nothing else.

## Access wideners

Optional and absent by default. If you need one, create
`src/main/resources/aw/<mcversion>.accesswidener` and Loom picks it up
automatically; without the file the step is skipped entirely. An *empty*
placeholder file does not work — it fails the build on newer Minecraft.

## Conventions for this repo

- Textures are 16x16 unless there's a reason. Keep the pixel-art style consistent
  with the rest of the mod.
- Every new block, item and mob needs an entry in the language file
  (`assets/<mod_id>/lang/en_us.json`) or it shows up in-game as a raw id, which
  reads to him as "broken".
- Anything a player can tune goes in the config, not hardcoded.
- Keep it dependency-free where possible. If a library is genuinely needed, it
  has to be one that's available for every Minecraft version in the list above.

## Releasing

Handled by the **share-it** skill. Short version: bump `mod.version` in
`stonecutter.properties.toml`, update `CHANGELOG.md` in plain words, push a
`v<version>` tag, and the workflow publishes to Modrinth using the org's
`MODRINTH_TOKEN`.
