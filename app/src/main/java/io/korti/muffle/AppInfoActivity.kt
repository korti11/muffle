package io.korti.muffle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_app_info.*
import kotlinx.android.synthetic.main.content_app_info.*

class AppInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        appVersion.apply {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            text = context.getString(R.string.label_version, packageInfo.versionName)
        }
        licensesButton.setOnClickListener {
            Intent(it.context, OpenSourceLicensesActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }

}
