package org.torrentflow.android.catalogue

import kotlinx.serialization.Serializable

@Serializable
data class CatalogueEntry(
    val id: String,
    val title: String,
    val creator: String,
    val description: String,
    val publisherId: String,
    val sourcePage: String,
    val contentHash: String,
    val magnetUri: String,
    val licenseId: String,
    val licenseUrl: String,
    val attribution: String,
    val signatureBase64: String
) {
    /**
     * Stable, newline-delimited signing payload. Publisher tooling must use
     * this exact field order and UTF-8 encoding when signing.
     */
    fun signingPayload(): ByteArray = listOf(
        id, title, creator, description, publisherId, sourcePage,
        contentHash, magnetUri, licenseId, licenseUrl, attribution
    ).joinToString("\n").toByteArray(Charsets.UTF_8)
}

@Serializable
data class CatalogueManifest(
    val schemaVersion: Int,
    val entries: List<CatalogueEntry>
)

/** Only this verified type may cross into the native transfer layer. */
data class VerifiedCatalogueEntry internal constructor(
    val id: String,
    val title: String,
    val contentHash: String,
    val magnetUri: String,
    val licenseId: String,
    val sourcePage: String
)
