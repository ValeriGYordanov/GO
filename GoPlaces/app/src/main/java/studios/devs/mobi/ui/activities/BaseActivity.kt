package studios.devs.mobi.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import studios.devs.mobi.model.Screen
import java.io.Serializable


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    companion object {
        const val PARAM_DATA = "data"
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
        val fullActivityNameWithPath = "studios.devs.mobi.ui.activities" + activityName + "Activity"
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
        val errorDialog = Dialog(this)
        errorDialog.show()
    }

    var loadingDialog: Dialog? = null

    fun renderLoading(shouldShow: Boolean){
        //FIXME Temporary solution
        if (loadingDialog == null && shouldShow){
//            loadingDialog = Dialog(this, R.style.LoadingDialogStyle)
//            loadingDialog?.setContentView(R.layout.dialog_loading)
//            loadingDialog?.findViewById<TextView>(R.id.loading_text)?.text = getString(R.string.loading)
            loadingDialog?.show()
        }
        if (!shouldShow){
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    fun getParameter(): Serializable {
        return intent.getSerializableExtra(PARAM_DATA)
    }
}
