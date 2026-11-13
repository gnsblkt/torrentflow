package org.torrentflow.android.catalogue

import org.junit.Assert.assertNull
import org.junit.Test

class CataloguePolicyTest {
    private val policy = CataloguePolicy()

    @Test
    fun rejectsUnknownPublisher() {
        val entry = CatalogueEntry(
            id = "sample", title = "Sample", creator = "Creator",
            description = "Openly licensed sample", publisherId = "untrusted",
            sourcePage = "https://example.org/item", contentHash = "a".repeat(40),
            magnetUri = "magnet:?xt=urn:btih:" + "a".repeat(40),
            licenseId = "CC0-1.0", licenseUrl = "https://creativecommons.org/publicdomain/zero/1.0/",
            attribution = "Creator", signatureBase64 = "AA=="
        )
        assertNull(policy.verify(entry))
    }
}
