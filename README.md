# EigenCraft Unofficial Patch

An unofficial collection of patches, optimizations, and tweaks for Minecraft.

This is a Forge mod with the aim of collecting as many community developed fixes, optimizations, and vanilla style
tweaks for Minecraft as possible under one comprehensive mod. It is designed with flexibility and compatibility in mind.

To help achieve that goal it uses the [Mixin API](https://github.com/SpongePowered/Mixin) for the CoreMod, and every
patch can be individually turned off so that it does not modify the game. This way, if a single patch conflicts with
another mod, it can be completely disabled without losing any other patch. Changes to which patches are loaded do
require the game to be restarted.

In addition, most patches can be toggled while the game is running. While the patch remains installed in this case, it
will restore vanilla behavior. This can help determine if a patch is causing a problem, and it allows servers to toggle
patches on and off for clients.

![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)
[Java profiler](https://www.ej-technologies.com/products/jprofiler/overview.html) open source license generously
provided by ej-technologies. Thanks!

## Fixed Bugs

### Vanilla Bugs

Currently the mod provides fixes for the following bugs in 1.12.2:

* [MC-4](https://bugs.mojang.com/browse/MC-4) - Item drops sometimes appear at the wrong location
* [MC-1133](https://bugs.mojang.com/browse/MC-1133) - Whether or not a player experiences some effect is calculated
  based on 
* [MC-2025](https://bugs.mojang.com/browse/MC-2025) - Mobs going out of fenced areas/suffocate in blocks when loading
  chunks
* [MC-2399](https://bugs.mojang.com/browse/MC-2399) - Transparent blocks visually use the brighter light level that they
  are next to
* [MC-5694](https://bugs.mojang.com/browse/MC-5694) - High efficiency tools / fast mining destroys some blocks
  client-side only
* [MC-9568](https://bugs.mojang.com/browse/MC-9568) - Mobs suffocate / go through blocks when growing up near a solid
  block
* [MC-14826](https://bugs.mojang.com/browse/MC-14826) - Leads in unloaded chunks break, become invisible or connect to
  an invisible target far away
* [MC-54026](https://bugs.mojang.com/browse/MC-54026) - Blocks attached to slime blocks can create ghost blocks
* [MC-73051](https://bugs.mojang.com/browse/MC-73051) - Witch Hut structure data do not account for height the witch hut
  is generated at
* [MC-80032](https://bugs.mojang.com/browse/MC-80032) - Horse suffocate when going through nether portals
* [MC-92916](https://bugs.mojang.com/browse/MC-92916) - Player is removed from the EntityTracker when teleporting to
  unloaded chunks or changing dimensions, resulting in client side desync
* [MC-98153](https://bugs.mojang.com/browse/MC-98153) - Portals generate far-away chunks & set player on fire
* [MC-108469](https://bugs.mojang.com/browse/MC-108469) - Chunk-wise entity lists often don't get updated correctly
  (Entities disappear)
* [MC-111444](https://bugs.mojang.com/browse/MC-111444) - Elytras can't open in laggy game
* [MC-111978](https://bugs.mojang.com/browse/MC-111978) - Item entities turn invisible when dropped on the ground
* [MC-118710](https://bugs.mojang.com/browse/MC-118710) - Blocks take multiple attempts to mine
* [MC-119971](https://bugs.mojang.com/browse/MC-119971) - Various duplications, deletions, and data corruption at chunk
  boundaries, caused by loading outdated chunks - includes duping and deletion of entities/mobs, items in hoppers, and
  blocks moved by pistons, among other problems
* [MC-123320](https://bugs.mojang.com/browse/MC-123320) - Items do not move through blocks smoothly
* [MC-134989](https://bugs.mojang.com/browse/MC-134989) - AbstractMap::hashCode accounts for substantial CPU overhead
  (from profiling)

### Mod Compatibility

* MC-134989 will not load when FomFix is installed. It fixes the same bug.
* MC-111444 will not load when Tweakeroo Forge is installed. It fixes the same bug.

### Mod Patches

The goal of mod patches are both to address patching other mods to work with features of this one as well as to address
long standing bugs or interoperability issues between other mods. The goal is to provide fixes to abandoned mods or
for issues that have remained unresolved for large amounts of time. 

* Redstone++ 1.2d and 1.3 BETA-2 pistons can be patched to work with the MC-54026 vanilla patch.
* If VanillaFix and FoamFix are both loaded and FoamFix has the "patchChunkSerialization" setting on, VanillaFix's
  mixin will be disabled to prevent a crash. It is recommended to simply disable the FoamFix setting to avoid any
  future problems.

## Optimizations

### Newlight

This is a complete drop in replacement for the vanilla Block and Sky lighting engine. It provides
considerable performance improvements to light updates and fixes many vanilla lighting bugs such as
[MC-3329](https://bugs.mojang.com/browse/MC-3329), [MC-3961](https://bugs.mojang.com/browse/MC-3961),
[MC-9188](https://bugs.mojang.com/browse/MC-9188), [MC-11571](https://bugs.mojang.com/browse/MC-11571),
[MC-80966](https://bugs.mojang.com/browse/MC-80966), [MC-91136](https://bugs.mojang.com/browse/MC-91136),
[MC-93132](https://bugs.mojang.com/browse/MC-93132), [MC-102162](https://bugs.mojang.com/browse/MC-102162), and
likely others. This engine was developed by the Overengineered Coding Duo, PhiPro and Mathe172, who have graciously
allowed its redistribution. The initial conversion to Mixins was completed by nessie for Liteloader, and is available
at [his GitHub page](https://github.com/Nessiesson/Newlight/releases) (this is not required for this mod).

### Redstone Wire Turbo

A set of replacement routines for redstone dust block update and power calculations aimed at 
high compatibility developed by theosib. Builds utilizing redstone dust can see 2x to 10x performance improvements
using this optimization. It also eliminates the directionality and locationality of most designs by making update
propagation predictable. It always flows outward from the source.

This optimization was tested heavily by many members of the technical Minecraft player community. While it is extremely
compatible, it is does not perfectly replicate vanilla behavior. So far, only two known contraptions have broken.
In cases where it is not compatible, its more predictable nature will likely result in a simpler alternative.

This optimization fixes [MC-81098](https://bugs.mojang.com/browse/MC-81098) and
[MC-11193](https://bugs.mojang.com/browse/MC-11193). Feel free to report any discovered differences against vanilla
redstone on the issues board.

## Tweaks

* Performance HUD - With this tweak enabled the server sends the client server performance data in the form of a 5
  second average MSPT (Milliseconds per Tick) and TPS (Ticks per Second). It is available in the player overlay
  activated with the TAB key. For convenience, this tweak also enables the overlay in single player mode.
* Profiler - A simple server side profiler accessible via the `/tickhealth` command. This is ported from the same
  feature found in CarpetMod. It's a relatively naive and simple profiler that makes some tasks simpler, but due to how
  invasive the patch is it is recommend to only be enabled when it is needed.
* Disable Movement Anti Cheat - Disables movement based anti cheat functionality. This helps with rubber banding from
  "Player moved too quickly" or "Player moved wrongly" when the server is lagging. However, hackers could very easily
  abuse this. Only enable if you trust all your players!
* Entity Tracking Editor - Allows you to control the range at which a server tells a client about an entity and how
  often a server sends updates based on the entity type.
* View Distance Editor - Control view distance of entities and tile entities (e.g. chests) on the client
  
## What is EigenCraft?

EigenCraft is a Discord server focused on Minecraft development and bug fixing. Many of the fixes in this mod were
generously contributed by developers there. The credit list for each patch is viewable in the tooltip for each one in
the configuration menu.

## Configuration

A configuration file will be generated the first time the game is run with the mod installed. It will be in the standard
configuration folder under your profile home folder and called `mup.cfg`.

### Single Player

For single player games a full GUI is provided:

![Image of Configuration GUI](https://i.imgur.com/m9CywaG.png)

It is based on the typical mod configuration GUI provided by Forge but with some enhancements. Each patch has a checkbox
that is used to determine if the CoreMod will load the patch when the game is started. Any patch that is not loaded
will, naturally, not be possible to enable. Next to the loading checkbox is the toggle for each patch. If a patch does
not support being disabled, then the button will be disabled, and the "enabled" text will be in blue.

Hover over the name of each patch to see a brief description, any side effects, the authors of the patch, and the
default configuration.

### Servers

The configuration GUI cannot be used to alter server configuration. You can either copy the configuration file created
by a client to the server, or you can directly edit the `mup.cfg` file. Each patch will have a section like the
following in the file:

    # Mobs going out of fenced areas/suffocate in blocks when loading chunks
    B:mc2025 <
        true
        true
     >

The top value controls whether or not the CoreMod will load the patch, and the bottom value controls if the patch is
enabled. You can set either to `true` or `false`.

### Client/Server Synchronization

Every effort is made to allow a client and a server to work when they have different configurations. The server will
inform the client which patches are enabled and disabled, and the client will try to match the server as best it can.
However, if the server wants to enable a patch that the client hasn't loaded, this will not be possible. In this case,
the client will let the server know, and in _most cases_ the server will be able to accommodate the client. If, for
any reason, the mismatch will cause problems, the server will kick the client and provide instructions on how to
resolve the situation.

When viewing the configuration menu while connected to a server most options will be disabled and a notification that
the configuration is being managed by the server will be displayed. Differences between the local saved configuration
and the servers configuration that have been detected will be displayed in yellow text. This is not an error! It's
only to illustrate what settings the server requested to be changed. Additionally, some settings have client side
behavior that can be altered while connected to a server. These will remain enabled and can be adjusted as needed.
