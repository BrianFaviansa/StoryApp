package com.faviansa.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.faviansa.storyapp.data.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val storyRepository = Injection.provideStoryRepository(applicationContext)
        val preferences = Injection.providePreferences(applicationContext)
        val viewModel = WidgetViewModelFactory(storyRepository, preferences)
            .create(WidgetViewModel::class.java)

        return StackRemoteViewsFactory(this.applicationContext, viewModel)
    }
}