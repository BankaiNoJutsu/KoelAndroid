# KoelAndroid
WIP - the final purpose is to have a basic native Android client for phanan/koel (great) app.

## What's working

- Sync between koel app and Android app
- Browse by artist, album, all songs, playlists
- Play songs (quite useful for a music player)
- Queue system
- Remote controls (from headseat, car, ...) and information (not tested yet, but should work)

## What's not working (yet)

- Current queue : the view isn't working (but songs added to queue will play normally)
- Album covers, artists images...
- Ability to choose view between "list mode" and "tiles mode" (artists, albums)
- Not displaying track number (but songs should be ordered in priority by their track number in an album)

## What's to improve

- Choose koel API URL dynamically (or when signing in)
- PlayerService : a bit messy...
- UI
- When there's a lot of artists/albums/songs, UI is too long to respond (when changing fragments, loading of lists should be asynchronous?)
- Add shuffle mode, repetition
- Pre-buffer songs in queue (in case of losing Internet connection, it'll keep the songs playing)
- Add an "save offline" function
- Manage playlists (offline and with sync online)
