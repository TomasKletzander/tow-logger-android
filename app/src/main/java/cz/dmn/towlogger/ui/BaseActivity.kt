package cz.dmn.towlogger.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        injectActivity()
        super.onCreate(savedInstanceState)
    }

    private fun injectActivity() {
        AndroidInjection.inject(this)
    }
}
