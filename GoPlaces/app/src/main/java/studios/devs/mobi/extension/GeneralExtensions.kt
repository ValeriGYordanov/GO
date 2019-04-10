package studios.devs.mobi.extension

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View


fun Boolean.toVisibility(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.GONE
    }
}


fun Activity.takeScreenShot(): Bitmap {
    val view = this.window.decorView
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache()
    val b1 = view.getDrawingCache(false)
    val frame = Rect()
    this.window.decorView.getWindowVisibleDisplayFrame(frame)
    val statusBarHeight = frame.top
    val width = this.windowManager.defaultDisplay.width
    val height = this.windowManager.defaultDisplay.height

    val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
    view.destroyDrawingCache()
    return b
}