package com.faviansa.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.faviansa.storyapp.data.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val storyRepository = Injection.provideStoryRepository(applicationContext)
        val viewModel = WidgetViewModelFactory(storyRepository)
            .create(WidgetViewModel::class.java)

        return StackRemoteViewsFactory(this.applicationContext, viewModel)
    }
}