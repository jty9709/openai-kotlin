package com.aallam.openai.api.misc

import com.aallam.openai.api.webhook.hmacSha256
import com.aallam.openai.api.webhook.isWithinTolerance
import com.aallam.openai.api.webhook.sha256
import com.aallam.openai.api.webhook.toHex
import com.aallam.openai.api.webhook.unwrapWebhookEvent
import com.aallam.openai.api.webhook.verifySignature
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalEncodingApi::class)
class TestWebhookSignature {

    @Test
    fun sha256MatchesKnownVector() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            sha256("abc".encodeToByteArray()).toHex(),
        )
    }

    @Test
    fun sha256HandlesMultiBlockInput() {
        // The padding path for inputs that span more than one 64-byte block.
        assertEquals(
            "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
            sha256(
                "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray()
            ).toHex(),
        )
    }

    @Test
    fun hmacSha256MatchesRfc4231TestCase1() {
        val key = ByteArray(20) { 0x0b }
        assertEquals(
            "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7",
            hmacSha256(key, "Hi There".encodeToByteArray()).toHex(),
        )
    }

    @Test
    fun hmacSha256MatchesRfc4231TestCase2() {
        assertEquals(
            "5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843",
            hmacSha256("Jefe".encodeToByteArray(), "what do ya want for nothing?".encodeToByteArray()).toHex(),
        )
    }

    @Test
    fun hmacSha256MatchesRfc4231TestCase3() {
        assertEquals(
            "773ea91e36800e46854db8ebd09181a72959098b3ef8c122d9635514ced565fe",
            hmacSha256(ByteArray(20) { 0xaa.toByte() }, ByteArray(50) { 0xdd.toByte() }).toHex(),
        )
    }

    @Test
    fun hmacSha256MatchesRfc4231TestCase6() {
        // Key longer than one block, exercising the key-hashing branch.
        val key = ByteArray(131) { 0xaa.toByte() }
        val data = "Test Using Larger Than Block-Size Key - Hash Key First".encodeToByteArray()
        assertEquals(
            "60e431591ee0b67f0d8a26aacbf5b77f8e0bc6213728c5140546040f0ee37f54",
            hmacSha256(key, data).toHex(),
        )
    }

    @Test
    fun verifySignatureAcceptsValidSignature() {
        val secret = "whsec_dGVzdC1zZWNyZXQ="
        val id = "msg_123"
        val timestamp = "1700000000"
        val body = """{"type":"response.completed"}"""
        val signature = signatureOf(secret, id, timestamp, body)

        assertTrue(verifySignature(id, timestamp, body, "v1,$signature", secret))
    }

    @Test
    fun verifySignatureAcceptsAnyOfSeveralCandidates() {
        val secret = "whsec_dGVzdC1zZWNyZXQ="
        val id = "msg_123"
        val timestamp = "1700000000"
        val body = "{}"
        val signature = signatureOf(secret, id, timestamp, body)

        assertTrue(verifySignature(id, timestamp, body, "v1,deadbeef v1,$signature", secret))
    }

    @Test
    fun verifySignatureRejectsTamperedBody() {
        val secret = "whsec_dGVzdC1zZWNyZXQ="
        val signature = signatureOf(secret, "msg_123", "1700000000", """{"amount":1}""")

        assertFalse(verifySignature("msg_123", "1700000000", """{"amount":2}""", "v1,$signature", secret))
    }

    @Test
    fun verifySignatureRejectsSignatureFromAnotherSecret() {
        val signature = signatureOf("whsec_b3RoZXI=", "msg_123", "1700000000", "{}")

        assertFalse(verifySignature("msg_123", "1700000000", "{}", "v1,$signature", "whsec_dGVzdC1zZWNyZXQ="))
    }

    @Test
    fun isWithinToleranceChecksAge() {
        assertTrue(isWithinTolerance("1000", 300, 1200))
        assertTrue(isWithinTolerance("1000", 300, 1000))
        // The boundary itself is inclusive.
        assertTrue(isWithinTolerance("1000", 300, 700))
        assertTrue(isWithinTolerance("1000", 300, 1300))
        // One second beyond the boundary is rejected, in either direction.
        assertFalse(isWithinTolerance("1000", 300, 699))
        assertFalse(isWithinTolerance("1000", 300, 1301))
        assertFalse(isWithinTolerance("not-a-number", 300, 1000))
    }

    @Test
    fun unwrapWebhookEventParsesPayload() {
        val body = """
            {"id":"evt_1","created_at":1741900000,"type":"response.completed","data":{"id":"resp_1"}}
        """.trimIndent()

        val event = unwrapWebhookEvent(body)
        assertEquals("evt_1", event.id)
        assertEquals("response.completed", event.type)
        assertEquals(1741900000L, event.createdAt)
        assertEquals("resp_1", event.data?.get("id")?.jsonPrimitive?.content)
    }

    @Test
    fun unwrapWebhookEventKeepsUnknownFieldsAccessible() {
        val body = """{"id":"evt_2","type":"future.event","data":{"brand_new_field":42}}"""

        val event = unwrapWebhookEvent(body)
        assertEquals("future.event", event.type)
        assertEquals("42", event.data?.get("brand_new_field")?.jsonPrimitive?.content)
    }

    @Test
    fun acceptsOfficialSdkSignatureVector() {
        val body = """{"id": "evt_685c059ae3a481909bdc86819b066fb6", "object": "event", "created_at": 1750861210, "type": "response.completed", "data": {"id": "resp_123"}}"""
        assertTrue(verifySignature(
            "wh_685c059ae39c8190af8c71ed1022a24d", "1750861210", body,
            "v1,gUAg4R2hWouRZqRQG4uJypNS8YK885G838+EHb4nKBY=",
            "whsec_RdvaYFYUXuIFuEbvZHwMfYFhUf7aMYjYcmM24+Aj40c=",
        ))
    }

    @Test
    fun supportsRawSecretsAndRejectsWrongVersionOrMalformedSecret() {
        // Independently computed HMAC fixture, using a raw UTF-8 signing key.
        val signature = "RDQG37U7rAiU/Wt81AVuT6+KaDSdGz2IEcFfUt+A4v8="
        assertTrue(verifySignature("msg_123", "1700000000", "{}", "v1,$signature", "test-secret"))
        assertTrue(verifySignature("msg_123", "1700000000", "{}", "v1,invalid\t$signature", "test-secret"))
        assertFalse(verifySignature("msg_123", "1700000000", "{}", "v2,$signature", "test-secret"))
        assertFalse(verifySignature("msg_123", "1700000000", "{}", "v1,$signature", "whsec_%%%"))
        assertFalse(verifySignature("msg_123", "1700000000", "{}", "v1,${signature.lowercase()}", "test-secret"))
    }

    private fun signatureOf(secret: String, id: String, timestamp: String, body: String): String =
        Base64.encode(hmacSha256(
            Base64.decode(secret.removePrefix("whsec_")),
            "$id.$timestamp.$body".encodeToByteArray(),
        ))
}
