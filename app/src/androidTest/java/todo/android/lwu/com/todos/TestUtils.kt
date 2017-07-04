package todo.android.lwu.com.todos

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration

/**
 * Created by lwu on 7/3/17.
 */
object TestUtils {

    fun rotateOrientation(activity: Activity) {
        val currentOrientation = activity.resources.configuration.orientation

        when (currentOrientation) {
            Configuration.ORIENTATION_LANDSCAPE -> rotateToLandscape(activity)
            Configuration.ORIENTATION_PORTRAIT -> rotateToPortrait(activity)
            else -> rotateToLandscape(activity)
        }
    }

    fun rotateToLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun rotateToPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}