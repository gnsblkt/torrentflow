"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { Activity, ArrowDownToLine, Check, CircleHelp, CloudDownload, FileVideo2, FolderOpen, Gauge, HardDrive, Link2, LoaderCircle, Magnet, Pause, Play, Radio, ShieldCheck, Trash2, Upload, Wifi, X } from "lucide-react";

type TorrentInfo = {
  name: string; progress: number; downloaded: number; length: number; downloadSpeed: number;
  uploadSpeed: number; numPeers: number; timeRemaining: number; files: any[]; raw: any;
};

const formatBytes = (n: number) => {
  if (!Number.isFinite(n) || n <= 0) return "0 B";
  const units = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.min(Math.floor(Math.log(n) / Math.log(1024)), units.length - 1);
  return `${(n / Math.pow(1024, i)).toFixed(i === 0 ? 0 : 1)} ${units[i]}`;
};
const formatSpeed = (n: number) => `${formatBytes(n)}/s`;
const formatTime = (n: number) => {
  if (!Number.isFinite(n) || n < 0) return "—";
  if (n < 60) return `${Math.ceil(n)} sec`;
  if (n < 3600) return `${Math.floor(n / 60)} min`;
  return `${Math.floor(n / 3600)} hr ${Math.floor((n % 3600) / 60)} min`;
};

export default function Home() {
  const [input, setInput] = useState("");
  const [torrent, setTorrent] = useState<TorrentInfo | null>(null);
  const [status, setStatus] = useState("idle");
  const [error, setError] = useState("");
  const [activeFile, setActiveFile] = useState<any>(null);
  const [videoUrl, setVideoUrl] = useState("");
  const [paused, setPaused] = useState(false);
  const [showHelp, setShowHelp] = useState(false);
  const clientRef = useRef<any>(null);
  const intervalRef = useRef<any>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const videoRef = useRef<HTMLVideoElement>(null);

  const stopClient = useCallback(() => {
    if (intervalRef.current) clearInterval(intervalRef.current);
    intervalRef.current = null;
    if (clientRef.current) {
      try { clientRef.current.destroy(() => {}); } catch {}
      clientRef.current = null;
    }
    setTorrent(null); setActiveFile(null); setVideoUrl(""); setStatus("idle"); setPaused(false);
  }, []);

  useEffect(() => () => {
    if (intervalRef.current) clearInterval(intervalRef.current);
    if (clientRef.current) { try { clientRef.current.destroy(() => {}); } catch {} }
  }, []);

  const addTorrent = async (source: string | File) => {
    if (clientRef.current) stopClient();
    setError(""); setStatus("connecting");
    try {
      const mod: any = await import("webtorrent");
      const WebTorrent = mod.default || mod;
      const client = new WebTorrent();
      clientRef.current = client;
      client.on("error", (e: Error) => { setError(e.message || "Torrent client error"); setStatus("error"); });
      client.add(source as any, (t: any) => {
        const update = () => {
          setTorrent({
            name: t.name || "Unnamed torrent", progress: t.progress || 0, downloaded: t.downloaded || 0,
            length: t.length || 0, downloadSpeed: t.downloadSpeed || 0, uploadSpeed: t.uploadSpeed || 0,
            numPeers: t.numPeers || 0, timeRemaining: t.timeRemaining, files: t.files || [], raw: t
          });
          if (t.progress >= 1) setStatus("complete");
          else if (!paused) setStatus("downloading");
        };
        update();
        intervalRef.current = setInterval(update, 700);
        t.on("done", () => { update(); setStatus("complete"); });
        setStatus("downloading");
      });
    } catch (e: any) {
      setError(e?.message || "Could not start the browser torrent client.");
      setStatus("error");
    }
  };

  const playFile = (file: any) => {
    setActiveFile(file); setVideoUrl("");
    file.getBlobURL((err: Error | null, url: string) => {
      if (err) { setError("Preview could not be created. Try downloading the file instead."); return; }
      setVideoUrl(url);
      window.setTimeout(() => videoRef.current?.play().catch(() => {}), 100);
    });
  };

  const downloadFile = (file: any) => {
    file.getBlob((err: Error | null, blob: Blob) => {
      if (err) { setError("The browser could not prepare this file for saving."); return; }
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a"); a.href = url; a.download = file.name; a.click();
      window.setTimeout(() => URL.revokeObjectURL(url), 60_000);
    });
  };

  const togglePause = () => {
    const t = torrent?.raw;
    if (!t) return;
    if (paused) { t.resume(); setPaused(false); setStatus("downloading"); }
    else { t.pause(); setPaused(true); setStatus("paused"); }
  };

  const percent = Math.round((torrent?.progress || 0) * 100);
  const isVideo = (name: string) => /\.(mp4|webm|m4v|ogv|mov)$/i.test(name);
  const isAudio = (name: string) => /\.(mp3|m4a|ogg|wav|flac)$/i.test(name);

  return (
    <main className="shell">
      <header className="topbar">
        <a className="brand" href="#"><span className="brand-mark"><Activity size={20}/></span><span>Torrent<span className="brand-accent">Flow</span></span></a>
        <div className="top-actions"><span className="privacy"><ShieldCheck size={14}/> Browser-first · No account</span><button className="icon-btn" onClick={() => setShowHelp(!showHelp)} aria-label="Help"><CircleHelp size={19}/></button></div>
      </header>

      <section className="hero">
        <div className="eyebrow"><span className="live-dot"/> YOUR MEDIA, IN MOTION</div>
        <h1>Stream at the<br/><span>speed of peers.</span></h1>
        <p className="hero-copy">A clean, private-feeling torrent workspace that runs in your browser. Add a magnet link or open a torrent file to get started.</p>
      </section>

      <section className="workspace">
        <div className="add-card">
          <div className="section-kicker"><Magnet size={15}/> ADD A TORRENT</div>
          <label className="input-label" htmlFor="magnet">Magnet link or info hash</label>
          <div className="input-row">
            <input id="magnet" value={input} onChange={e => setInput(e.target.value)} onKeyDown={e => e.key === "Enter" && input.trim() && addTorrent(input.trim())} placeholder="magnet:?xt=urn:btih:..." spellCheck={false}/>
            <button className="primary-btn" disabled={!input.trim() || status === "connecting"} onClick={() => addTorrent(input.trim())}><Link2 size={16}/> Add link</button>
          </div>
          <div className="add-footer"><span>or choose a .torrent file from your device</span><button className="upload-btn" onClick={() => fileInputRef.current?.click()}><Upload size={15}/> Browse files</button><input ref={fileInputRef} type="file" accept=".torrent,application/x-bittorrent" hidden onChange={e => { const f = e.target.files?.[0]; if (f) addTorrent(f); e.currentTarget.value = ""; }}/></div>
        </div>

        {showHelp && <div className="help-panel"><button className="close-help" onClick={() => setShowHelp(false)}><X size={16}/></button><strong>How TorrentFlow works</strong><p>Paste a magnet URI or select a .torrent file. Your browser connects to compatible WebRTC peers. Select a media file to preview it, or save completed files to your device.</p><p><b>Browser limitation:</b> only torrents with WebRTC-capable peers are reachable from this browser client. Closing the tab stops the session. Use this only for public-domain, openly licensed, or otherwise authorized content.</p></div>}

        {error && <div className="error-banner"><X size={16}/>{error}<button onClick={() => setError("")}><X size={15}/></button></div>}

        {status === "connecting" && <div className="loading-card"><LoaderCircle className="spin" size={22}/> Connecting to the torrent network…</div>}

        {torrent && <div className="torrent-card">
          <div className="torrent-heading"><div className="file-icon"><FileVideo2 size={21}/></div><div className="torrent-title"><span className="section-kicker">ACTIVE TRANSFER</span><h2>{torrent.name}</h2><span className="muted">{formatBytes(torrent.length)} total · {torrent.files.length} files</span></div><button className="icon-btn danger-hover" onClick={stopClient} title="Remove torrent"><Trash2 size={18}/></button></div>
          <div className="progress-meta"><span>{status === "complete" ? "Download complete" : status === "paused" ? "Paused" : "Downloading"}</span><strong>{percent}%</strong></div>
          <div className="progress-track"><div className="progress-fill" style={{width: `${percent}%`}}/></div>
          <div className="stats-grid">
            <div className="stat"><ArrowDownToLine size={16}/><span>DOWNLOAD</span><strong>{formatSpeed(torrent.downloadSpeed)}</strong></div>
            <div className="stat"><ArrowDownToLine className="upload-icon" size={16}/><span>UPLOAD</span><strong>{formatSpeed(torrent.uploadSpeed)}</strong></div>
            <div className="stat"><Wifi size={16}/><span>PEERS</span><strong>{torrent.numPeers}</strong></div>
            <div className="stat"><Gauge size={16}/><span>TIME LEFT</span><strong>{status === "complete" ? "Done" : formatTime(torrent.timeRemaining / 1000)}</strong></div>
          </div>
          <div className="transfer-actions"><button className="secondary-btn" onClick={togglePause} disabled={status === "complete"}>{paused ? <Play size={15}/> : <Pause size={15}/>} {paused ? "Resume" : "Pause"}</button><span className="status-note"><span className="live-dot"/>{status === "complete" ? "Ready to save" : status === "paused" ? "Transfer paused" : "Peer discovery active"}</span></div>
          <div className="files-header"><span>TORRENT CONTENTS</span><span>{torrent.files.length} ITEMS</span></div>
          <div className="file-list">{torrent.files.map((file, i) => <div className={`file-row ${activeFile === file ? "selected" : ""}`} key={i}><div className="file-name"><FileVideo2 size={17}/><span title={file.name}>{file.name}</span></div><span className="file-size">{formatBytes(file.length)}</span><div className="file-actions">{(isVideo(file.name) || isAudio(file.name)) && <button className="mini-btn" onClick={() => playFile(file)}><Play size={14}/> Preview</button>}<button className="mini-btn" onClick={() => downloadFile(file)}><ArrowDownToLine size={14}/> Save</button></div></div>)}</div>
        </div>}

        {videoUrl && <div className="player-card"><div className="player-head"><div><span className="section-kicker">NOW PREVIEWING</span><h3>{activeFile?.name}</h3></div><button className="icon-btn" onClick={() => { setVideoUrl(""); setActiveFile(null); }}><X size={18}/></button></div><video ref={videoRef} src={videoUrl} controls playsInline className="video-player"/></div>}

        {!torrent && status !== "connecting" && <div className="empty-state"><div className="empty-orbit"><CloudDownload size={28}/></div><strong>Your next stream starts here</strong><span>Add a torrent to see peers, transfer speed, files, and media previews.</span></div>}
      </section>

      <footer><div className="footer-brand">Torrent<span className="brand-accent">Flow</span></div><span>Built for open content and authorized sharing.</span><span className="footer-status"><span className="live-dot"/> READY</span></footer>
    </main>
  );
}
