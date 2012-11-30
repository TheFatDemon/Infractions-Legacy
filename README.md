![Infractions](http://i.imgur.com/Bo4OW.png)

**Infractions** is a policing plugin for Bukkit based on a number of small warnings given to a player before they are automatically banned.

The idea was originally implemented on my server with a php application hooking into another plugin, but after it kept breaking with every release, I started to work on a java version of the system.

## Statistics: ##

![Plugin Metrics](http://mcstats.org/signature/infractions.png)

Versions 0.3c and up will provide the option to contribute to these statistics.

Find more statistics [here](http://mcstats.org/plugin/Infractions).

## Features ##

**Levels**: Infractions currently has 5 'levels' for warnings.  Different types of infractions are sorted into these levels, 5 being the worst, 1 being hardly a problem at all.  When an infraction of any level is given, it's level number is added to a players total score.

**Score**: When a player gets an infraction, it adds to their score. After a player reaches a configurable max score they are automatically banned.

**Proof**: Infractions currently accepts proof in the form of a URL (something uploaded to a website).  It shortens the URL with bit.ly and saves it along with the infraction.  As long as the infraction exists, the proof will be there for a moderator to find and use in any sort of dispute.

## Commands ##
Use the following commands in-game, or in the console.
* **/cite <player> <infraction> [proof]** - Gives an infraction.
* **/uncite <player> <key>** - Removes an infraction, key is found with /history <player>.
* **/infractions** - Help page.
* **/infractions types** - Shows every valid type of infraction.
* **/history [player]** - View a players infraction history.

## Permissions ##
This node gives permission to use /cite and /uncite, as well as being able to use /history on every player:

    - infractions.mod
    
This node sets a player's max score. Replace # with a max score of 1 to 20.  If no permission like this is found, it defaults to the max score you set in your config (ban_at_score).

    - infractions.maxscore.#

If you have the '*' permission, as given from PermissionsEx, you will be given the default max score provided in the config file.

## Development ##

Infractions is still in active development.  New ideas are welcome to the plugin. Do you have a clever idea? Let me know!

Want to help with development?  Fork the code on Github show us what you can do!

Ideas actually finished because someone asked:
* Max score is found from permission nodes before the config.
* Option to kick or not on /cite.
* Send a message to offline players notifying them, when they rejoin, of any infractions they may have gotten.
* Make /uncite not kick a player.

Ideas already in development:
* More permission nodes; one for each command.
* Better removal of infractions.
* Option to have a certain score set a player to a certain rank.
* MySQL option for saves.

You can always find the latest stable build here: http://www.clashnia.com/plugins/infractions/Infractions.jar