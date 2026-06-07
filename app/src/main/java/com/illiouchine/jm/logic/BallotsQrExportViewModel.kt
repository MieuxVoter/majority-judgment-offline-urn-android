package com.illiouchine.jm.logic

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.serializer.UUIDSerializer
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.util.UUID

@Stable
@Serializable
data class BallotsDto(
    @Serializable(UUIDSerializer::class)
    val pollUuid: UUID,
    val ballots: List<Ballot>,
)

class BallotsQrExportViewModel(
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    @Stable
    data class ViewState(
        /**
         * The poll whose ballots we want to export.
         */
        val poll: Poll? = null,
        /**
         * The Data Transfer Object that's actually going to transit via QR Code.
         */
        val ballotsDto: BallotsDto? = null,
        //val offset: Int = 0,
        //val limit: Int = 100,
        val qrContent: String? = null,
        val qrBitmap: ImageBitmap? = null,
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
                )
            }
            return
        }

        val ballotsDto = BallotsDto(
            pollUuid = poll.uuid,
            ballots = poll.ballots,
        )

        val ballotsBytes = Cbor.encodeToByteArray(value = ballotsDto)
        val ballotsCompressedBytes = compress(input = ballotsBytes)
        val ballotsCompressedString = encode(bytes = ballotsCompressedBytes)
        val qrContent = "mju://b/$ballotsCompressedString"
        val qrPngBytes = renderQrCodePngBytes(qrContent)
        val qrBitmap = imageBitmapFromPngBytes(qrPngBytes)

        _viewState.update {
            it.copy(
                poll = poll,
                ballotsDto = ballotsDto,
                qrContent = qrContent,
                qrBitmap = qrBitmap,
            )
        }
    }

    fun onBack() {
        viewModelScope.launch {
            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
