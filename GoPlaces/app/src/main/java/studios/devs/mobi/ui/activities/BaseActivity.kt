package studios.devs.mobi.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.loading_dialog.*
import studios.devs.mobi.model.Screen
import java.io.Serializable
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import studios.devs.mobi.R
import studios.devs.mobi.extension.takeScreenShot
import java.util.*


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {


    companion object {
        const val PARAM_DATA = "data"
        const val BACKGROUND_BLUR_AMOUNT = 30
        var LOADING_DIALOG_IS_INITIALISED = false
    }

    val compositeDisposable = CompositeDisposable()


    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getActivityClassFromString(activityName: String): Class<out Activity> {
        val fullActivityNameWithPath = "studios.devs.mobi.ui.activities." + activityName + "Activity"
        return Class.forName(fullActivityNameWithPath) as Class<Activity>
    }

    open fun startActivityByNameWithParams(screen: Screen<out Serializable?>) {
        // Cut the name of the Screen subclass
        val screenType = screen.toString().substringAfter('$').substringBefore('(')
        val intent = Intent(this, getActivityClassFromString(screenType))
        if (screen.someData != null) {
            intent.putExtra(PARAM_DATA, screen.someData)
        }
        startActivity(intent)
    }

    open fun startActivityByNameWithParamsAndFinish(screen: Screen<out Serializable?>) {
        startActivityByNameWithParams(screen)
        finish()
    }

    fun renderError(error: String){
        val errorDialog = AlertDialog.Builder(this)
        errorDialog.setMessage(error)
        errorDialog.create().show()
    }

    lateinit var loadingDialog: Dialog

    fun renderLoading(shouldShow: Boolean){
        initialiseLoadingDialog()

        if (shouldShow){
            Observable.create<Drawable> {
                it.onNext(BitmapDrawable(resources, fastBlur(this, BACKGROUND_BLUR_AMOUNT)))
                it.onComplete()
            }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        loadingDialog.rootView.background = it
                        loadingDialog.show()
                    }.addTo(compositeDisposable)
//            val backgroundDrawable = BitmapDrawable(resources, fastBlur(this, BACKGROUND_BLUR_AMOUNT))
//            loadingDialog.rootView.background = backgroundDrawable
//            loadingDialog.show()
        }
        if (!shouldShow){
            loadingDialog.dismiss()
        }
    }

    private fun initialiseLoadingDialog(){
        if (!LOADING_DIALOG_IS_INITIALISED){
            loadingDialog = Dialog(this, android.R.style.ThemeOverlay)
            loadingDialog.setContentView(R.layout.loading_dialog)
//            loadingDialog.setCancelable(false)
//            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            Glide.with(this)
                    .load(R.drawable.go_loading)
                    .into(loadingDialog.loading_gif)
            LOADING_DIALOG_IS_INITIALISED = true
        }
    }

    fun fastBlur(activity: Activity, blurAmount: Int): Bitmap? {
        val originBitmap = activity.takeScreenShot()
        val bitmap = originBitmap.copy(originBitmap.config, true)

        if (blurAmount < 1) {
            return null
        }

        val w = bitmap.width
        val h = bitmap.height

        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = blurAmount + blurAmount + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = blurAmount + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -blurAmount
            while (i <= blurAmount) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + blurAmount]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = blurAmount

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - blurAmount + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + blurAmount + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -blurAmount * w
            i = -blurAmount
            while (i <= blurAmount) {
                yi = Math.max(0, yp) + x

                sir = stack[i + blurAmount]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = blurAmount
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - blurAmount + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h)

        return bitmap
    }

    fun getParameter(): Serializable {
        return intent.getSerializableExtra(PARAM_DATA)
    }

    fun showToastWithArgument(flag: Boolean, ifPositiveText: String, ifNegativeText: String){
        if (flag){
            showToast(ifPositiveText)
        }else{
            showToast(ifNegativeText)
        }
    }

    fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}
