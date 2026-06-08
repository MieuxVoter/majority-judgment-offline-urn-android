package com.illiouchine.jm.logic

import android.content.Context
import android.database.SQLException
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.navigator.NavigationAction
import com.illiouchine.jm.ui.utils.decode
import com.illiouchine.jm.ui.utils.decompress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.util.zip.DataFormatException

class BallotsQrImportViewModel(
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    @Stable
    data class ViewState(
        val qrUriPath: String? = null,
        val ballotsDto: BallotsDto? = null,
        val poll: Poll? = null,
        val error: Exception? = null,
        val errorMessage: String? = null,
    )

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    @OptIn(ExperimentalSerializationApi::class)
    fun initialize(
        context: Context,
        qrUriPath: String,
    ) {
//        viewModelScope.launch {
        _viewState.update {
            ViewState(
                qrUriPath = qrUriPath,
            )
        }

        try {
            val compressedPollString = decode(qrUriPath)
            val decompressedQrBytes = decompress(compressedPollString)
            val ballotsDto = Cbor.decodeFromByteArray<BallotsDto>(decompressedQrBytes)
            initializeFromBallotsDto(
                // context = context,
                ballotsDto = ballotsDto,
            )
        } catch (e: DataFormatException) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while decompressing the imported ballots.",
                )
            }
        } catch (e: SerializationException) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while deserializing the imported ballots.",
                )
            }
        } catch (e: IllegalArgumentException) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while decoding the imported ballots.",
                )
            }
        } catch (e: Exception) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while processing the imported ballots.",
                )
            }
        }

//        }
    }

    fun initializeFromBallotsDto(
        // context: Context,
        ballotsDto: BallotsDto,
    ) {
        viewModelScope.launch {
            val existingPoll = pollDataSource.getPollByUuid(ballotsDto.pollUuid)

            _viewState.update {
                it.copy(
                    poll = existingPoll,
                    ballotsDto = ballotsDto,
                )
            }
        }
    }

    fun onCancel() {
        viewModelScope.launch {
            _navEvents.emit(NavigationAction.Clear)
        }
    }

    fun onConfirm() {
        viewModelScope.launch {
            if (viewState.value.ballotsDto != null && viewState.value.poll != null) {
                val ballotsImported = viewState.value.ballotsDto!!.ballots.filterNot {
                    viewState.value.poll!!.ballots.map { it.uuid }.contains(it.uuid)
                }
                for (ballot in ballotsImported) {
                    // FIXME: check the consistency of the ballot first
                    try {
                        pollDataSource.saveBallot(
                            ballot = ballot,
                            pollId = viewState.value.poll!!.id,
                        )
                    } catch (_: SQLException) {
                        // TBD: what should we do with these errors ?
                    }
                }
            }

            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
