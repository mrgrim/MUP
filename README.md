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

## Features

Currently the mod provides fixes for the following bugs in 1.12.2:

* [MC-4](https://bugs.mojang.com/browse/MC-4) - Item drops sometimes appear at the wrong location
* [MC-2025](https://bugs.mojang.com/browse/MC-2025) - Mobs going out of fenced areas/suffocate in blocks when loading
  chunks
* [MC-5694](https://bugs.mojang.com/browse/MC-5694) - High efficiency tools / fast mining destroys some blocks
  client-side only
* [MC-9568](https://bugs.mojang.com/browse/MC-9568) - Mobs suffocate / go through blocks when growing up near a solid
  block
* [MC-54026](https://bugs.mojang.com/browse/MC-54026) - Blocks attached to slime blocks can create ghost blocks
* [MC-118710](https://bugs.mojang.com/browse/MC-118710) - Blocks take multiple attempts to mine
* [MC-119971](https://bugs.mojang.com/browse/MC-119971) - Various duplications, deletions, and data corruption at chunk
  boundaries, caused by loading outdated chunks — includes duping and deletion of entities/mobs, items in hoppers, and
  blocks moved by pistons, among other problems

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

In addition, to prevent confusion the client will not allow the user to open the configuration GUI while connected to
a server.