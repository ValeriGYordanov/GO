package studios.devs.mobi.extension

import android.view.View


fun Boolean.toVisibility(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.GONE
    }
}