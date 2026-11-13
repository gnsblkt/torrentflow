package org.torrentflow.android.catalogue

import android.util.Base64
import java.net.URI
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Fail-closed publisher verification.
 *
 * Before release, replace the empty key registry with reviewed Ed25519
 * publisher public keys. Do not load trusted keys from the catalogue response.
 */
class CataloguePolicy(
    private val publisherKeys: Map<String, ByteArray> = emptyMap()
) {
    private val acceptedLicenseIds = setOf(
        "CC0-1.0",
        "CC-BY-4.0",
        "CC-BY-SA-4.0",
        "PUBLIC-DOMAIN",
        "AUTHORIZED-WORK"
    )

    fun verify(entry: CatalogueEntry): VerifiedCatalogueEntry? {
        if (entry.id.isBlank() || entry.title.isBlank() || entry.creator.isBlank()) return null
        if (entry.description.length > 4_000 || entry.attribution.isBlank()) return null
        if (!isHttps(entry.sourcePage) || !isHttps(entry.licenseUrl)) return null
        if (!acceptedLicenseIds.contains(entry.licenseId)) return null
        if (!isValidInfoHash(entry.contentHash, entry.magnetUri)) return null

        val publicKeyBytes = publisherKeys[entry.publisherId] ?: return null
        return try {
            val key = KeyFactory.getInstance("Ed25519")
                .generatePublic(X509EncodedKeySpec(publicKeyBytes))
            val verifier = Signature.getInstance("Ed25519")
            verifier.initVerify(key)
            verifier.update(entry.signingPayload())
            val signatureBytes = Base64.decode(entry.signatureBase64, Base64.DEFAULT)
            if (!verifier.verify(signatureBytes)) return null

            VerifiedCatalogueEntry(
                id = entry.id,
                title = entry.title,
                contentHash = entry.contentHash,
                magnetUri = entry.magnetUri,
                licenseId = entry.licenseId,
                sourcePage = entry.sourcePage
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun isHttps(value: String): Boolean = try {
        val uri = URI(value)
        uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank()
    } catch (_: Exception) {
        false
    }

    private fun isValidInfoHash(hash: String, magnet: String): Boolean {
        val normalized = hash.lowercase()
        val validHash = normalized.matches(Regex("[a-f0-9]{40}|[a-f0-9]{64}"))
        if (!validHash || !magnet.startsWith("magnet:?")) return false
        val expected = if (normalized.length == 40) "urn:btih:$normalized" else "urn:btmh:1220$normalized"
        return magnet.lowercase().contains(expected)
    }
}
