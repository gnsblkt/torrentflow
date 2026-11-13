import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "TorrentFlow — Your media, in motion",
  description: "A browser-based BitTorrent client for content you have the right to access."
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body>{children}</body></html>;
}
