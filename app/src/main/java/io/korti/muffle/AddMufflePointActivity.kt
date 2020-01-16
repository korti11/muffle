package io.korti.muffle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.korti.muffle.audio.AudioManager
import io.korti.muffle.viewmodel.AddMufflePointActivityViewModel
import kotlinx.android.synthetic.main.activity_add_muffle_point.*
import kotlinx.android.synthetic.main.content_add_muffle_point.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddMufflePointActivity : AppCompatActivity() {

    companion object {
        private val TAG = AddMufflePointActivity::class.java.simpleName
        private const val REQUEST_CODE = 1
    }

    @Inject
    lateinit var addMufflePointActivityViewModel: AddMufflePointActivityViewModel
    @Inject
    lateinit var audioManager: AudioManager

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MuffleApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_muffle_point)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }

        rangeLabel.text = this.getString(R.string.label_range, muffleRange.progress + 100)
        muffleRange.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            /**
             * Notification that the progress level has changed. Clients can use the fromUser parameter
             * to distinguish user-initiated changes from those that occurred programmatically.
             *
             * @param seekBar The SeekBar whose progress has changed
             * @param progress The current progress level. This will be in the range min..max where min
             * and max were set by [ProgressBar.setMin] and
             * [ProgressBar.setMax], respectively. (The default values for
             * min is 0 and max is 100.)
             * @param fromUser True if the progress change was initiated by the user.
             */
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                addMufflePointActivityViewModel.updateRadius(progress)
                rangeLabel.text = seekBar?.context?.getString(R.string.label_range, progress + 100)
            }

            /**
             * Notification that the user has started a touch gesture. Clients may want to use this
             * to disable advancing the seekbar.
             * @param seekBar The SeekBar in which the touch gesture began
             */
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // I don't need this
            }

            /**
             * Notification that the user has finished a touch gesture. Clients may want to use this
             * to re-enable advancing the seekbar.
             * @param seekBar The SeekBar in which the touch gesture began
             */
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // I don't need this
            }
        })

        ringtoneVolume.max =
            audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_RING)
        ringtoneVolume.progress = ringtoneVolume.max / 2

        mediaVolume.max = audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_MUSIC)
        mediaVolume.progress = mediaVolume.max / 2

        notificationsVolume.max =
            audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_NOTIFICATION)
        notificationsVolume.progress = notificationsVolume.max / 2

        alarmVolume.max = audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_ALARM)
        alarmVolume.progress = alarmVolume.max / 2

        selectActivityButton.setOnClickListener {
            Intent(this, SelectMufflePointActivity::class.java).also { intent ->
                startActivityForResult(intent, REQUEST_CODE)
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment

        mapFragment.getMapAsync {
            map = it

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    addMufflePointActivityViewModel.mapCameraPosition.value,
                    addMufflePointActivityViewModel.mapZoom.value!!
                )
            )

            addMufflePointActivityViewModel.mapCameraPosition.observe(this, Observer { pos ->
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15F))
                addMufflePointActivityViewModel.mapCircle.value?.center = pos
                addMufflePointActivityViewModel.mapMarker.value?.position = pos
            })
            addMufflePointActivityViewModel.mapCircle.value = map.addCircle(
                CircleOptions().center(addMufflePointActivityViewModel.mapCameraPosition.value)
                    .radius(muffleRange.progress.toDouble() + 100).strokeColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorAccent
                        )
                    )
            )
            addMufflePointActivityViewModel.mapMarker.value = map.addMarker(
                MarkerOptions().position(addMufflePointActivityViewModel.mapCameraPosition.value!!)
            )
            addMufflePointActivityViewModel.mapZoom.observe(this, Observer { zoom ->
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        addMufflePointActivityViewModel.mapCameraPosition.value,
                        zoom
                    )
                )
            })

            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [.onPrepareOptionsMenu].
     *
     *
     * The default implementation populates the menu with standard system
     * menu items.  These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     *
     * When you add items to the menu, you can implement the Activity's
     * [.onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_muffle_point, menu)
        return true
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     *
     * Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     *
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     *
     * @see .onCreateOptionsMenu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                Toast.makeText(this, getString(R.string.toast_muffle_point_added), Toast.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch {
                    withContext(Dispatchers.Default) {
                        map.snapshot {
                            addMufflePointActivityViewModel.saveMufflePoint(
                                muffleName.text.toString(),
                                it,
                                if (ringtoneCheckBox.isChecked) ringtoneVolume.progress else -1,
                                if (mediaCheckBox.isChecked) mediaVolume.progress else -1,
                                if (notificationsCheckBox.isChecked) notificationsVolume.progress else -1,
                                if (alarmCheckBox.isChecked) alarmVolume.progress else -1
                            )
                        }
                    }
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val location: LatLng? =
                data?.getParcelableExtra(SelectMufflePointActivity.LOCATION_RESULT)
            if (location != null) {
                addMufflePointActivityViewModel.updateCameraPosition(location)
            }
        }
    }
}
