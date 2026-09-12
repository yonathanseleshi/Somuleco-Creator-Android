package com.somuleco.creator.feature.creator.media

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.MediaFile
import com.somuleco.creator.data.model.MediaType
import com.somuleco.creator.data.repository.interfaces.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope, but still boundary-compliant via DI. */
@HiltViewModel
class MediaLibraryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    val mediaItems: StateFlow<List<MediaFile>> = mediaRepository.mediaAssets

    fun uploadMedia(name: String, typeStr: String, sizeStr: String, channelName: String) {
        val type = when (typeStr.uppercase()) {
            "VIDEO" -> MediaType.VIDEO
            "AUDIO" -> MediaType.AUDIO
            "DOCUMENT" -> MediaType.DOCUMENT
            else -> MediaType.IMAGE
        }
        mediaRepository.uploadMedia(name, type, sizeStr, channelName)
    }
}
