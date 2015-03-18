

# PVP.bot - Client automation for lazy players
**Auto call, auto accept and other automatic features for League of Legends (LoL) PVP.net Client launcher**
 - Autocall your lane
 - Autopick - automatically pick a champion, summoner spells runes and masteries
 - Autoaccept - teambuilder games automatically accept players when captain
 - Autoready - automatically press ready in teambuilder
 - **Works when client is behind another window**
 - **Doesn't move your mouse**

##[Download](https://github.com/Darker/auto-client/raw/master/release/AutoClient_v2.zip)
 
 - [Latest version](https://github.com/Darker/auto-client/raw/master/release/AutoClient_v2.zip)
 - [All versions](https://github.com/Darker/auto-client/tree/master/release)

For detailed information, [please refer to wiki](https://github.com/Darker/auto-client/wiki). On this page I'll
rather post some technical info and SEO meaningless texts.

## What is this?
**This text partially exists for search engines, you can skip it if you have the general idea.**
This repository presents sourcecode for League of Legends PVP.net client bot that automatically accepts games, 
auto calls your lane and automaticaly accepts players in team-builder

## Known problems
**Issues about anything bellow will be closed without longer reply**

 - For setting to take effect, you must leave the text field (focus another text field)
 - The program doesn't work when the window is minimized (**won't fix**)
 - Sometimes, the text isn't called properly
 - Teambuilder captain mode is not 100% reliable at this moment
 - Images on the main page of the client can confuse the automation
 
## Planned features
**Ordered by descending probability of being ever implemented**

 - ~~Disable/enable start button depending on whether the launcher is available or not~~ **- done**
 - Auto-requeue
 - ~~Automatically pick runes, masteries and summoner spells for given champion and map~~ **- done**
 - Automatically fetch runes and masteries names and replace the indexes with them
 - [*Technical*] cache screenshot before multiple pixels are being queried
 - automatically kick certain champions in team-builder
 - Automatically accept game invite
   - have a blacklist/whitelist for accepts
 - Teambuilder templates that can be auto-filled to save you some clicking.
 - Possibility to edit pixel coordinates externally
 - Version with auto updates
 - Converting the automation into scriptable language (Javascript)
