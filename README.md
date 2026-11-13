# TorrentFlow

A browser-first BitTorrent client for public-domain, openly licensed, and otherwise authorized content.

## Stack
- Next.js App Router + TypeScript
- WebTorrent in the browser
- Responsive custom UI

## Run locally
```bash
npm install
npm run dev
```

## Browser transport limitations
WebTorrent in a browser uses WebRTC peer connections. It cannot connect to arbitrary conventional TCP/µTP BitTorrent peers, so a torrent needs WebRTC-capable peers and compatible tracker signaling. Downloads run in the active browser session; closing the tab ends the session. Browser storage and memory limits apply.

## Responsible use
Use only torrents for content you own, public-domain material, openly licensed works, or content you are otherwise authorized to access and share. TorrentFlow does not host or index torrent content.
