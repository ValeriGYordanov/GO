package studios.devs.mobi.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.loading_dialog.*
import studios.devs.mobi.R
import studios.devs.mobi.model.Screen
import java.io.Serializable


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {


    companion object {
        const val PARAM_DATA = "data"
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
            loadingDialog.show()
        }
        if (!shouldShow){
            loadingDialog.dismiss()
        }
    }

    private fun initialiseLoadingDialog(){
        if (!LOADING_DIALOG_IS_INITIALISED){
            loadingDialog = Dialog(this)
            loadingDialog.setContentView(R.layout.loading_dialog)
            loadingDialog.setCancelable(false)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            Glide.with(this)
                    .load(R.drawable.go_loading)
                    .into(loadingDialog.loading_gif)
            LOADING_DIALOG_IS_INITIALISED = true
        }
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
