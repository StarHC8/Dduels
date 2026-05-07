# Dduels

A simple plugin to handle duels between players on a small servers.

## Features

- Multi-Map support
- Customizable kits 
- Messages config (Unfortunately this plugin doesn't use adventure)
- Stats (leaderboard not available yet)
- Party duels support (with [PartyManager](https://github.com/StarHC8/PartyManager))

## Commands

- /duel \<player\> - Send a duel request with adjustable settings.
- /duelaccept \<player\> - Accept a duel request you received.
- /spectate \<player\> - Start/stop spectating a duel.
- /leave - Leave a duel.
- /stats \<player\> - See yours or another player's stats
- /partyduel - Start a party duel

### Warnings
Version: Paper 1.20+

This plugin require a MySQL database to work properly; use the settings.yml file to configure it.
You also have to configure the duel worlds from the file maps.yml

## Next Steps

- [ ] Bug fixes
- [ ] Improve world loader
- [ ] Improve UIs
- [ ] Implement leaderboard
- [ ] /rematch command
- [ ] Adventure support?
