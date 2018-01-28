package cz.dmn.towlogger.ui

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import cz.dmn.towlogger.R
import dagger.android.AndroidInjection

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        injectActivity()
        super.onCreate(savedInstanceState)
    }

    private fun injectActivity() {
        AndroidInjection.inject(this)
    }

    protected fun showError(@StringRes text: Int, @StringRes title: Int = R.string.titleError) =
            showError(getString(text), title)

    protected fun showError(text: String, @StringRes title: Int = R.string.titleError) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(text)
                .setNeutralButton(R.string.ok, null)
                .show()
    }
}
