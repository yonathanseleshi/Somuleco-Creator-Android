package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.MediaFile
import com.somuleco.creator.data.model.MediaType
import com.somuleco.creator.data.repository.interfaces.MediaRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : MediaRepository {
    override val mediaAssets: StateFlow<List<MediaFile>> = dataSource.mediaAssets
    override fun uploadMedia(name: String, type: MediaType, size: String, channelName: String): MediaFile =
        dataSource.uploadMedia(name, type, size, channelName)
    override fun deleteMedia(id: String) = dataSource.deleteMedia(id)
}
