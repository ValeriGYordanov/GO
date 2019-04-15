package studios.devs.mobi.ui.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.online_view.*
import studios.devs.mobi.R

class OnlineActivity: BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.online_view)
        curved_navigation.inflateMenu(R.menu.online_navigation)
    }
}