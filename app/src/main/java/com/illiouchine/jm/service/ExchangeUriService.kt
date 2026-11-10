package com.illiouchine.jm.service

import android.net.Uri
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.dto.BallotsDto
import com.illiouchine.jm.ui.utils.compress
import com.illiouchine.jm.ui.utils.decodeBase41
import com.illiouchine.jm.ui.utils.decompress
import com.illiouchine.jm.ui.utils.encodeBase41
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

/**
 * Serialization and deserialization utility for polls and their ballots.
 * Also see the AndroidManifest.xml where the URI intent filter is declared.
 */
@OptIn(ExperimentalSerializationApi::class)
class ExchangeUriService(
    /**
     * Without the :// suffix.
     * Example: https
     */
    private val scheme: String,
    /**
     * You should own this domain.  (it might need verification from Gogole)
     * Example: mju.mieuxvoter.fr
     * Note: this is allowed to be empty, in the case where we use our custom mju:// scheme.
     */
    private val domain: String,
    private val pollRoutePathSegment: String = "p",
    private val ballotsRoutePathSegment: String = "b",
) {

    fun pollToUri(
        poll: Poll,
    ): String {
        // TBD: this is awkward ; we should probably use a PollDto
        val pollToExport = poll.copy(
            id = 0, // this identifier is local to the device, it's meaningless to exchange it
            ballots = emptyList(), // we do not want to send the ballots, only the poll config
        )
        val pollBytes = Cbor.encodeToByteArray(value = pollToExport)
        val compressedPollBytes = compress(input = pollBytes)
        val compressedPollString = encodeBase41(bytes = compressedPollBytes).lowercase()
        return buildString {
            append(scheme)
            append("://")
            if (domain.isNotEmpty()) {
                append(domain)
                append("/")
            }
            append(pollRoutePathSegment)
            append("/")
            append(compressedPollString)
        }
    }

    fun ballotsDtoToUri(
        ballotsDto: BallotsDto,
    ): String {
        val ballotsBytes = Cbor.encodeToByteArray(value = ballotsDto)
        val ballotsCompressedBytes = compress(input = ballotsBytes)
        val ballotsCompressedString = encodeBase41(bytes = ballotsCompressedBytes).lowercase()
        return buildString {
            append(scheme)
            append("://")
            if (domain.isNotEmpty()) {
                append(domain)
                append("/")
            }
            append(ballotsRoutePathSegment)
            append("/")
            append(ballotsCompressedString)
        }
    }

    fun uriPathDatumToPoll(
        /**
         * Not the full path ; only the part after the /p/, like so:
         *     https://mju.mieuxvoter.fr/p/<uriPathDatum>
         * This does not include the query or fragment part of the Uri (if we ever have any).
         */
        uriPathDatum: String,
    ): Poll {
        val compressedString = decodeBase41(string = uriPathDatum.uppercase())
        val decompressedBytes = decompress(input = compressedString)
        return Cbor.decodeFromByteArray<Poll>(bytes = decompressedBytes)
    }

    fun uriPathDatumToBallotsDto(
        /**
         * Not the full path ; only the part after the /b/, like so:
         *     https://mju.mieuxvoter.fr/b/<uriPathDatum>
         * This does not include the query or fragment part of the Uri (if we ever have any).
         */
        uriPathDatum: String,
    ): BallotsDto {
        val compressedString = decodeBase41(string = uriPathDatum.uppercase())
        val decompressedBytes = decompress(input = compressedString)
        return Cbor.decodeFromByteArray<BallotsDto>(bytes = decompressedBytes)
    }

    fun uriMatchesPoll(uri: Uri): Boolean {
        return uriMatchesFormat(uri)
            && uriMatchesRoutePrefix(uri, prefix = pollRoutePathSegment)
    }

    fun uriMatchesBallots(uri: Uri): Boolean {
        return uriMatchesFormat(uri)
            && uriMatchesRoutePrefix(uri, prefix = ballotsRoutePathSegment)
    }

    private fun uriMatchesFormat(uri: Uri): Boolean {
        return uriMatchesHost(uri)
            && uri.pathSegments.isNotEmpty()
    }

    private fun uriMatchesHost(uri: Uri): Boolean {
        return uri.scheme.equals(other = scheme, ignoreCase = true)
            && (domain.isEmpty() || uri.host.equals(other = domain, ignoreCase = true))
    }

    private fun uriMatchesRoutePrefix(uri: Uri, prefix: String): Boolean {
        return (uri.pathSegments.first().equals(other = prefix, ignoreCase = true))
            || (domain.isEmpty() && uri.host.equals(other = prefix, ignoreCase = true))
    }
}
