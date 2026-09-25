# Wolf Control

Summon wolves that stand perfectly still, and carry a detector that wraps every
wolf nearby in a giant orange box.

Built for a **Vivecraft (VR) server**. Vivecraft only goes up to Minecraft
1.20.1, so this mod targets **1.19.4** — nothing newer would run in VR.

## What's in it

**Wolf Detector.** Right-click it and you flip into creative mode and get a set
of wings. Sneak + right-click and you drop back to survival — the detector keeps
working either way. While you're holding one, every wolf within 64 blocks gets a
giant orange box drawn round it, plus an orange glow that shines through walls.
The glow is plain vanilla, so other players on the server see it even without
the mod installed.

**Wolf Caller.** Right-click a block and a wolf appears there with its brain
switched off — it stands exactly where you put it and never wanders. Sneak +
right-click a block and every still wolf you own jumps to that spot. That's the
control part: they only move when you say so.

**A chest to start with.** Join the server with no detector on you and a chest
full of detectors and callers appears in the block in front of you. If there's
nowhere to put a chest, the items go straight into your bag instead.

## Minecraft versions

| version | Java |
|---|---|
| 1.19.4 | 17 |

Gradle downloads the JDK it needs, so nothing has to be installed by hand.

## Building

```bash
./gradlew "Set active project to 1.19.4-fabric"
./gradlew "1.19.4-fabric:build"
```

The jar lands in `versions/1.19.4-fabric/build/libs/`.

## Licence

MIT.
