package org.torrentflow.android.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.libtorrent4j.SessionManager
import org.libtorrent4j.swig.torrent_flags_t
import org.torrentflow.android.catalogue.VerifiedCatalogueEntry
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Thin native-engine boundary. It deliberately accepts only the policy's
 * VerifiedCatalogueEntry, never a raw URI from UI or an external intent.
 */
class NativeTorrentEngine {
    private val session = SessionManager()
    private val started = AtomicBoolean(false)

    suspend fun start() = withContext(Dispatchers.IO) {
        if (started.compareAndSet(false, true)) {
            try {
                session.start()
            } catch (error: Throwable) {
                started.set(false)
                throw error
            }
        }
    }

    suspend fun enqueue(
        entry: VerifiedCatalogueEntry,
        destination: File
    ) = withContext(Dispatchers.IO) {
        check(started.get() && session.isRunning) { "Torrent session is not running." }
        require(destination.exists() || destination.mkdirs()) { "Cannot create destination folder." }
        // This call is reachable only after catalogue signature and policy checks.
        session.download(entry.magnetUri, destination, torrent_flags_t())
    }

    suspend fun pauseAll() = withContext(Dispatchers.IO) {
        if (started.get()) session.pause()
    }

    suspend fun resumeAll() = withContext(Dispatchers.IO) {
        if (started.get()) session.resume()
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
        if (started.compareAndSet(true, false)) session.stop()
    }
}
