package com.faviansa.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.faviansa.storyapp.R
import com.faviansa.storyapp.views.story.ui.detail.DetailStoryActivity

/**
 * Implementation of App Widget functionality.
 */
class StoryWidget : AppWidgetProvider() {

    companion object {
        private const val STORY_ACTION = "com.faviansa.storyapp.widget.StoryWidget.STORY_ACTION"
        const val EXTRA_ITEM = "com.faviansa.storyapp.widget.StoryWidget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.story_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view_text)
            }

            val storyIntent = Intent(context, StoryWidget::class.java).apply {
                action = STORY_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                storyIntent,
                flags
            )
            views.setPendingIntentTemplate(R.id.stack_view, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                appWidgetIds?.let {
                    onUpdate(context, appWidgetManager, it)
                }
            }

            STORY_ACTION -> {
                val storyId = intent.getStringExtra(EXTRA_ITEM)
                if (storyId != null) {
                    val detailIntent = Intent(context, DetailStoryActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("story_id", storyId)
                    }
                    context.startActivity(detailIntent)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }
}
