package com.klyuzhevigor.ChatApp.ChatsList

import com.klyuzhevigor.ChatApp.Model.MessageModel
import com.klyuzhevigor.ChatApp.Services.ChatsRepository
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException

class MessagesPagingSource(
    val repo: ChatsRepository,
    val chat: String
) : PagingSource<Int, MessageModel>() {
    private var lastLoadedId: Long = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageModel> {
        return try {
            val pageNumber = params.key ?: 0

            val response = repo.getMessages(chat, lastLoadedId)
            if (response.isNotEmpty()) {
                lastLoadedId = response[response.size - 1].id
            }

            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MessageModel>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}