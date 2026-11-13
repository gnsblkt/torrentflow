package org.torrentflow.android.catalogue

import kotlinx.serialization.json.Json

class CatalogueDecoder {
    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
    }

    fun decode(payload: String): CatalogueManifest {
        val manifest = json.decodeFromString<CatalogueManifest>(payload)
        require(manifest.schemaVersion == 1) { "Unsupported catalogue schema." }
        require(manifest.entries.size <= 200) { "Catalogue is too large." }
        return manifest
    }
}
