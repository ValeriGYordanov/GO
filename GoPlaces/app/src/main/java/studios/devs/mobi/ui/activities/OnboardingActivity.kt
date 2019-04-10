package studios.devs.mobi.ui.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import studios.devs.mobi.MainApplication
import studios.devs.mobi.R
import studios.devs.mobi.databinding.ActivityOnboardingBinding
import studios.devs.mobi.extension.addTo
import studios.devs.mobi.extension.rxClick
import studios.devs.mobi.viewmodels.OnboardingViewModel
import studios.devs.mobi.viewmodels.OnboardingViewModelInput
import studios.devs.mobi.viewmodels.OnboardingViewModelInputOutput
import studios.devs.mobi.viewmodels.OnboardingViewModelOutput
import javax.inject.Inject

class OnboardingActivity : BaseActivity() {

    lateinit var binding: ActivityOnboardingBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: OnboardingViewModelInputOutput by lazy {
        ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(OnboardingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.appComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
    }

    override fun onStart() {
        super.onStart()
        viewModel
                .bind(this)
                .addTo(compositeDisposable)
        informationDialog()
    }

    private fun informationDialog(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.start_information_dialog)
            dialog.setCancelable(false)
            dialog.findViewById<Button>(R.id.error_cancel).setOnClickListener {
                dialog.dismiss()
                requestPermission()
            }
            dialog.show()
        }else{
            viewModel.input.permissionsGranted()
        }
    }


    private fun requestPermission() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (granted) {
                        viewModel.input.permissionsGranted()
                    }
                }
                .addTo(compositeDisposable)
    }

    fun goOnline() {
        //FIXME: This is temporary dialog showing we are not yet supporting online version
        val onlineDialog = Dialog(this)
        onlineDialog.showTheDialog()
    }

}

private fun Dialog.showTheDialog() {
    this.setContentView(R.layout.online_error_dialog)
    this.window?.setBackgroundDrawableResource(R.color.colorPartlyTransparent)
    this.findViewById<Button>(R.id.error_cancel).setOnClickListener { this.dismiss() }
    this.show()
}


private fun OnboardingViewModelInputOutput.bind(activity: OnboardingActivity): List<Disposable> {
    return listOf(
            output.bind(activity.binding),
            output.bind(activity),
            input.bind(activity.binding),
            activity.binding.configureWith(activity)
    ).flatten()
}

private fun ActivityOnboardingBinding.configureWith(activity: OnboardingActivity): List<Disposable> {
    return listOf(
            //binding.
            this.btnGoOnline.rxClick.subscribe { activity.renderLoading(true) }
    )
}

private fun OnboardingViewModelInput.bind(binding: ActivityOnboardingBinding): List<Disposable> {
    return listOf(
            binding.btnGoOffline.rxClick.subscribe { goOffline() }
    )
}

private fun OnboardingViewModelOutput.bind(activity: OnboardingActivity): List<Disposable> {
    return listOf(
            startOfflineScreenStream.subscribe { activity.startActivityByNameWithParamsAndFinish(it) }
    )
}

private fun OnboardingViewModelOutput.bind(binding: ActivityOnboardingBinding): List<Disposable> {
    return listOf(
        permissionsGrantedStream.subscribe {
            binding.btnGoOffline.isEnabled = true
            binding.btnGoOnline.isEnabled = true
        }
    )
}
