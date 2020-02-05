package io.korti.muffle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_open_source_licenses.*
import kotlinx.android.synthetic.main.content_open_source_licenses.*

class OpenSourceLicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_source_licenses)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }

        licensesView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        licensesView.loadUrl("file:///android_asset/open_source_licenses.html")
    }

}
