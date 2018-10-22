package studios.devs.mobi.ui.activities

import android.app.Dialog
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.Disposable
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
        //TODO: Ask for permissions here - if not granted disable buttons!
    }

    override fun onStart() {
        super.onStart()
        viewModel
                .bind(this)
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
    this.setTitle("Error")
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
            this.btnGoOnline.rxClick.subscribe { activity.goOnline() }
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

    )
}
