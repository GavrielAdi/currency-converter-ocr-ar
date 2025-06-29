package com.adios.conversee

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import com.adios.conversee.currencies.CurrenciesViewModel
import com.adios.conversee.currencies.CurrencyFragment
import com.adios.conversee.currencies.OcrCaptureActivity
import com.adios.conversee.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val vm by viewModels<CurrenciesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        requestGDPRMessage()
        enableEdgeToEdge()

        ActivityMainBinding.inflate(layoutInflater).apply {
            setSupportActionBar(bottomAppBar)

            supportFragmentManager.beginTransaction().apply {
                replace(R.id.f_main_mode, CurrencyFragment())
                commit()
            }

            fab.setOnClickListener {
                vm.showAd(this@MainActivity) {
                    if (isTaskRoot) {
                        Intent(this@MainActivity, OcrCaptureActivity::class.java).also {
                            startActivity(it)
                        }
                    } else {
                        finish()
                    }
                }
            }
        }.also {
            setContentView(it.root)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        vm.loadAd()
    }
}
