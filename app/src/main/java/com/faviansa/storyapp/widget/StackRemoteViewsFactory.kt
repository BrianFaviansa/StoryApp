package com.faviansa.storyapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.ViewModelProvider
import com.faviansa.storyapp.R
import com.faviansa.storyapp.data.di.Injection
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import com.faviansa.storyapp.utils.loadWidgetImage
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private var widgetViewModel: WidgetViewModel,
) : RemoteViewsService.RemoteViewsFactory {

    private var storiesList = mutableListOf<ListStoryItem>()
    private var page: Int = 1
    private val pageSize: Int = 10
    private val location: Int = 0
    private lateinit var userToken: String


    override fun onCreate() {
        widgetViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(mContext.applicationContext as android.app.Application)
                .create(WidgetViewModel::class.java)

        userToken = Injection.provideUserToken(mContext)
    }

    override fun onDataSetChanged() {
        runBlocking {
            val stories = widgetViewModel.getStories(userToken, page, pageSize, location)
            if (stories != null) {
                storiesList.clear()
                storiesList.addAll(stories)
            }
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = storiesList.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position == storiesList.size - 1) {
            page++
            onDataSetChanged()
        }
        val sortedStories = storiesList.sortedByDescending { it.createdAt }
        val story = sortedStories[position]
        val views = RemoteViews(mContext.packageName, R.layout.item_widget).apply {
            setTextViewText(R.id.story_user_name, story.name)
            val imageBitmap = story.photoUrl?.let { loadWidgetImage(it) }
            imageBitmap?.let { setImageViewBitmap(R.id.story_image, it) }
        }

        val intent = Intent().apply { putExtra(StoryWidget.EXTRA_ITEM, story.id) }
        views.setOnClickFillInIntent(R.id.item_widget, intent)
        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = i.toLong()

    override fun hasStableIds(): Boolean = true
}