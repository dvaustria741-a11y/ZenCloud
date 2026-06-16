# ZenCloud

ZenCloud is a Minecraft 1.8.9 Forge client mod built on [CloudClient](https://github.com/DupliCAT/cloudclient) with additional features ported from ZenClient.

## Features

### From CloudClient
- FPS Display, CPS Counter, Armor HUD, Keystrokes, Coordinates, Ping, Potion HUD, Speed Indicator
- Toggle Sprint/Sneak, Fullbright, Zoom, Freelook
- Crosshair Customizer, Block Overlay, Block Info, Hit Color, Animations
- Scoreboard, Bossbar, Nametag, Nick Hider, Day Counter, Time Changer
- GUI Tweaks, Scroll Tooltips, Particle Multiplier, Reach Display

### Added by ZenCloud
- **Kill Aura** — Auto-attacks nearby entities (Players/Mobs/All), configurable range/CPS/focus/rotations
- **Auto Soup** — Automatically eats mushroom stew when health drops below threshold
- **Auto Scaffold** — Places blocks under your feet while walking
- **Anti AFK** — Prevents AFK kick by periodically rotating
- **FPS Booster** — Reduces particle/graphics settings for better performance

### Removed
- Motion Blur (removed intentionally)

## Building

```
./gradlew build
```

Output: `build/libs/zencloud-1.0.0.jar`

## License
GNU Lesser General Public License v3.0
