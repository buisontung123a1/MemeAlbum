package com.memealbum.app.data.repository

import com.memealbum.app.data.api.RetrofitClient
import com.memealbum.app.data.model.MemeItem

class MemeRepository {

    private val api = RetrofitClient.redditApi

    suspend fun getHotMemes(after: String? = null): Pair<List<MemeItem>, String?> {
        val response = api.getHotMemes(after = after)
        return parseResponse(response.data.children.map { it.data }, response.data.after)
    }

    suspend fun getNewMemes(after: String? = null): Pair<List<MemeItem>, String?> {
        val response = api.getNewMemes(after = after)
        return parseResponse(response.data.children.map { it.data }, response.data.after)
    }

    suspend fun getTopMemes(after: String? = null): Pair<List<MemeItem>, String?> {
        val response = api.getTopMemes(after = after)
        return parseResponse(response.data.children.map { it.data }, response.data.after)
    }

    private fun parseResponse(
        posts: List<com.memealbum.app.data.model.RedditPost>,
        after: String?
    ): Pair<List<MemeItem>, String?> {
        val memes = posts
            .filter { !it.isVideo }
            .mapNotNull { post ->
                val imageUrl = post.getImageUrl() ?: return@mapNotNull null
                MemeItem(
                    id = post.id,
                    title = post.title,
                    imageUrl = imageUrl,
                    author = post.author,
                    score = post.score,
                    createdUtc = post.createdUtc,
                    permalink = "https://reddit.com${post.permalink}"
                )
            }
        return Pair(memes, after)
    }
}
