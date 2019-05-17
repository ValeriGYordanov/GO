package studios.devs.mobi.ui.activities

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.online_view.*
import studios.devs.mobi.R
import android.view.animation.Animation
import android.view.animation.AnimationUtils


class OnlineActivity: BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.online_view)
        bottomNavigationView.inflateMenu(R.menu.online_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.action_home
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_profile -> {

            }
            R.id.action_home -> {

            }
            R.id.action_more -> {

            }
        }
        return true
    }
}