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
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.navigator.NavigationAction
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.utils.compress
import com.illiouchine.jm.ui.utils.encode
import com.illiouchine.jm.ui.utils.imageBitmapFromPngBytes
import com.illiouchine.jm.ui.utils.renderQrCodePngBytes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

class PollQrExportViewModel(
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    @Stable
    data class PollQrExportViewState(
        val poll: Poll, // TBD: perhaps we should just use Poll? here
        // val pollQrBytes: ByteArray,
        val pollQrContent: String,
        val pollQrBitmap: ImageBitmap?,
    ) {
        fun hasPoll(): Boolean {
            return poll.id != 0
        }
    }

    private val _viewState = MutableStateFlow(
        PollQrExportViewState(
            poll = Poll( // this is awkward
                id = 0,
                pollConfig = PollConfig(),
            ),
            pollQrContent = "",
            pollQrBitmap = null,
        )
    )
    val viewState: StateFlow<PollQrExportViewState> = _viewState

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
                    // context = context,
                    poll = poll,
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun initializeFromPoll(
        // context: Context,
        poll: Poll,
    ) {
        val pollToExport = poll.copy(id = 0, ballots = emptyList())
        val pollBytes = Cbor.encodeToByteArray(pollToExport)
        val compressedPollBytes = compress(pollBytes)
        val compressedPollString = encode(compressedPollBytes)
        val qrContent = "mju://p/$compressedPollString"

        // Up to 2,953 bytes (so the spec says ; we need to check, seems less)
        val qrPngBytes = renderQrCodePngBytes(qrContent)
        val qrBitmap = imageBitmapFromPngBytes(qrPngBytes)

        _viewState.update {
            it.copy(
                poll = poll,
                pollQrContent = qrContent,
                pollQrBitmap = qrBitmap,
            )
        }
    }

    fun onBack() {
        viewModelScope.launch {
            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
