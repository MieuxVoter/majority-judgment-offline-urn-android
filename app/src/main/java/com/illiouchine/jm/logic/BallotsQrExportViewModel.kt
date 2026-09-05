package com.illiouchine.jm.logic

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.dto.BallotsDto
import com.illiouchine.jm.service.ExchangeUriService
import com.illiouchine.jm.ui.navigator.NavigationAction
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.utils.imageBitmapFromPngBytes
import com.illiouchine.jm.ui.utils.renderQrCodePngBytes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException

class BallotsQrExportViewModel(
    private val pollDataSource: PollDataSource,
    private val exchangeUriService: ExchangeUriService,
) : ViewModel() {

    @Stable
    data class BallotsQrExport(
        /**
         * The Data Transfer Object that's actually going to transit via QR Code.
         */
        val ballotsDto: BallotsDto? = null,
        /**
         * Full content of the QR Code, including the URL prefix (scheme+domain+path).
         */
        val qrContent: String? = null,
        val qrBitmap: ImageBitmap? = null,
    ) {
        companion object { // cheap factories
            @OptIn(ExperimentalSerializationApi::class)
            fun createFromBallotsDto(
                exchangeUriService: ExchangeUriService,
                ballotsDto: BallotsDto,
            ): BallotsQrExport? {
                try {
                    val qrContent = exchangeUriService.ballotsDtoToUri(ballotsDto)
                    val qrPngBytes = renderQrCodePngBytes(qrContent)
                    val qrBitmap = imageBitmapFromPngBytes(qrPngBytes)
                    return BallotsQrExport(
                        ballotsDto = ballotsDto,
                        qrContent = qrContent,
                        qrBitmap = qrBitmap,
                    )
                } catch (_: SerializationException) {
                    // TBD: log the error?
                } catch (_: IllegalArgumentException) {
                    // TBD: log the error?
                }
                return null
            }
        }

        fun splitIfNecessary(
            exchangeUriService: ExchangeUriService,
            maxAmountOfBytes: Int = 650,
        ): List<BallotsQrExport> {
            if (this.qrContent == null) {
                return listOf(this)
            }
            if (this.ballotsDto == null) {
                return listOf(this)
            }
            val totalAmountOfBytes = this.qrContent.length // true because of our limited charset
            val amountOfQrCodes = 1 + totalAmountOfBytes / maxAmountOfBytes // Euclid wuz hir
            val ballotsDtos = this.ballotsDto.splitInto(amountOfQrCodes)
            return buildList {
                for (i in 0..<amountOfQrCodes) {
                    val ballotsQrExport = createFromBallotsDto(
                        exchangeUriService = exchangeUriService,
                        ballotsDto = ballotsDtos[i],
                    )
                    if (ballotsQrExport != null) {
                        add(ballotsQrExport)
                    }
                }
            }
        }
    }

    @Stable
    data class ViewState(
        /**
         * The poll whose ballots we want to export.
         */
        val poll: Poll? = null,
        val qrExports: List<BallotsQrExport> = emptyList(),
        val errorMessage: String? = null,
    )

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    fun initializeFromPollId(
        context: Context,
        pollId: Int,
    ) {
        viewModelScope.launch {
            val poll = pollDataSource.getPollById(pollId)

            if (poll == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_that_poll_does_not_exist),
                    Toast.LENGTH_LONG,
                ).show()
                _navEvents.emit(NavigationAction.To(Screens.Home))
            } else {
                initializeFromPoll(
                    context = context,
                    poll = poll,
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun initializeFromPoll(
        context: Context,
        poll: Poll,
    ) {
        if (poll.uuid == null) {
            _viewState.update {
                it.copy(
                    poll = poll,
                    errorMessage = "The poll is too ancient.",
                    qrExports = emptyList(),
                )
            }
            return
        }

        val ballotsDto = BallotsDto(
            pollUuid = poll.uuid,
            ballots = poll.ballots,
        )

        val qrExport = BallotsQrExport.createFromBallotsDto(
            exchangeUriService = exchangeUriService,
            ballotsDto = ballotsDto,
        )

        val qrExports = qrExport?.splitIfNecessary(
            exchangeUriService = exchangeUriService,
            maxAmountOfBytes = 650,
        )

        _viewState.update {
            it.copy(
                poll = poll,
                errorMessage = null,
                qrExports = qrExports ?: emptyList(),
            )
        }
    }

    fun onBack() {
        viewModelScope.launch {
            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
