package todo.android.lwu.com.todos.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by lwu on 4/23/17.
 */
fun addFragmentToActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
    fragmentManager.beginTransaction().add(frameId, fragment).commit()
}