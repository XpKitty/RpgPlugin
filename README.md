# AnotherRpgPlugin

Minecraft java edition plugin. Made for spigot, compatible with spigot forks.
Tested with spigot 1.19.3 and PaperMc 1.19.3

//TODO: 
attach resource pack, required for certain functionality\n
fix profile command\n
add practical functionality for /party and /guild commands\n
make easier to use\n
add sword blocking\n

Build with Maven.

HOW TO USE:

Commands:

/rpg
Perm: [OP]
rpgpl.rpg

/getitem
Perm: [OP]
rpgpl.give
Used to get items

/experience
Alias: /exp
Perm: [OP]
rpgpl.exp.modify
Used to modify EXPERIENCE amount

/menu
Perm: [ALL]
Use to open character menu

/achievements
Perm: [ALL/OP]
Use to view achievement menu. Currently unfinished

/housepoints
Aliases: /hp
Perms: [ALL]
rpgpl.points.*
rpgpl.points.give.*
rpgpl.points.take.*
rpgpl.points.give.own
rpgpl.points.take.own
rpgpl.points.set
Requires op for certail functionality

/party
Aliases: /p /group
Perms: [ALL]

/setskill
Perms: [OP]
rpgpl.skill.set

/guild
Alias: /g
Perms: [ALL]

/profile
Alias: /char /character /profiles
Perms: [ALL]
Used to add, remove, modify profiles
WARNING: Does not fully work. Use at your own risk

/spell
Perms: [OP]
Used to create yml spell baseplate, does not fully work.
Baseplates may cause errors.


Other Perms:

rpgpl.*
Default: [OP]
Used to get most perms

rpgpl.debug
Used to show debug
Default: [NONE]

rpgpl.xpbar
Currently unused
