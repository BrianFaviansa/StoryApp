package com.faviansa.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.faviansa.storyapp.R
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private var widgetViewModel: WidgetViewModel,
) : RemoteViewsService.RemoteViewsFactory {

    private var storiesList: MutableList<ListStoryItem> = mutableListOf()
    private var page: Int = 1
    private val pageSize: Int = 10
    private val location: Int = 0

    override fun onCreate() {
        loadStories()
    }

    override fun onDataSetChanged() {
        loadStories()
    }

    private fun loadStories() {
        runBlocking {
            widgetViewModel.getStories(page, pageSize, location).collect { result ->
                when (result) {
                    is Result.Success -> {
                        storiesList.clear()
                        result.data.listStory?.let { storiesList.addAll(it.filterNotNull()) }
                    }
                    is Result.Error -> {
                        //
                    }
                    is Result.Loading -> {
                        //
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        storiesList.clear()
    }

    override fun getCount(): Int = storiesList.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.item_widget)

        try {
            val story = storiesList[position]

            val bitmap: Bitmap = runBlocking {
                Glide.with(mContext)
                    .asBitmap()
                    .load(story.photoUrl)
                    .submit()
                    .get()
            }

            rv.setImageViewBitmap(R.id.story_image, bitmap)
            rv.setTextViewText(R.id.story_user_name, story.name)

            val extras = bundleOf(
                StoryWidget.EXTRA_ITEM to story.id
            )
            val fillInIntent = Intent().apply {
                putExtras(extras)
            }
            rv.setOnClickFillInIntent(R.id.item_widget, fillInIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}