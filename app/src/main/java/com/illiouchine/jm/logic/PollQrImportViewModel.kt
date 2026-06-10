package com.illiouchine.jm.logic

import android.content.Context
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

class PollQrImportViewModel(
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    @Stable
    data class PollQrImportViewState(
        val qrUriPath: String? = null,
        val importedPoll: Poll? = null,
        val existingPoll: Poll? = null,
        val error: Exception? = null,
        val errorMessage: String? = null,
    )

    private val _viewState = MutableStateFlow(PollQrImportViewState())
    val viewState: StateFlow<PollQrImportViewState> = _viewState

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    @OptIn(ExperimentalSerializationApi::class)
    fun initialize(
        context: Context,
        qrUriPath: String,
    ) {
//        viewModelScope.launch {
        _viewState.update {
            PollQrImportViewState(
                qrUriPath = qrUriPath,
            )
        }

        try {
            val compressedPollString = decode(qrUriPath)
            val decompressedQrBytes = decompress(compressedPollString)
            val poll = Cbor.decodeFromByteArray<Poll>(decompressedQrBytes)
            initializeFromPoll(context, poll)
        } catch (e: DataFormatException) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while decompressing the imported poll.",
                )
            }
        } catch (e: SerializationException) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while deserializing the imported poll.",
                )
            }
        } catch (e: IllegalArgumentException) {
            _viewState.update {
                it.copy(
                    error = e,
                    errorMessage = "There was an issue while decoding the imported poll.",
                )
            }
        }

//        }
    }

    fun initializeFromPoll(
        context: Context,
        pollToImport: Poll,
    ) {
        viewModelScope.launch {
            // Rule: don't trust imported data to have set the id to zero
            // (we would abort the SQL transaction later on if the poll already exists, but still…)
            val importedPoll = pollToImport.copy(id = 0)

            val existingPoll = if (importedPoll.uuid != null) {
                pollDataSource.getPollByUuid(importedPoll.uuid)
            } else {
                null
            }

            _viewState.update {
                it.copy(
                    importedPoll = importedPoll,
                    existingPoll = existingPoll,
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
            if (viewState.value.importedPoll != null) {
                pollDataSource.savePoll(viewState.value.importedPoll!!)
            } else {
                // TBD: Raise, but only during dev ?
            }
            _navEvents.emit(NavigationAction.Clear)
        }
    }
}
