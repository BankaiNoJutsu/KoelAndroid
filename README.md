# KoelAndroid
WIP - the final purpose is to have a basic native Android client for phanan/koel (great) app.

## What's working

- Sync between koel app and Android app
- Browse by artist, album, all songs, playlists
- Play songs (quite useful for a music player)
- Queue system
- Remote controls (from headseat, car, ...) and information on current song
- Album covers, artists images...

## What's not working (yet)

- Current queue : changes to queue are not shown while "current queue" is the current frame
- Ability to choose view between "list mode" and "tiles mode" (artists, albums)
- Choose koel API URL dynamically (or when signing in) --> Database SQLite depending on this url ?
- Add shuffle mode, repetition

## What's to improve

- Not displaying track number (but songs should be ordered in priority by their track number in an album)
- PlayerService : a bit messy...
- UI
- When there's a lot of artists/albums/songs, UI is too long to respond (when changing fragments, loading of lists should be asynchronous?)
- Pre-buffer songs in queue (in case of losing Internet connection, it'll keep the songs playing)
    - at first, I'll add a feature to (manually) download some songs
    - then, I'll add a feature that automatically saves current song (and next songs in queue) in a cache folder (but I can't figure out a way to simultaneously download a song and stream it to media 
player, apart from a local http server not easy to do)
- Add an "save offline" function
- Manage playlists (offline and with sync online)
