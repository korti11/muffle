package io.korti.muffle

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
import io.korti.muffle.database.entity.MufflePoint
import io.korti.muffle.viewmodel.EditMufflePointActivityViewModel
import kotlinx.android.synthetic.main.activity_edit_muffle_point.*
import kotlinx.android.synthetic.main.content_edit_muffle_point.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditMufflePointActivity : AppCompatActivity() {

    companion object {
        val MUFFLE_POINT_EXTRA = "MUFFLE_POINT_EXTRA"
    }

    @Inject
    lateinit var editMufflePointActivityViewModel: EditMufflePointActivityViewModel

    @Inject
    lateinit var audioManager: AudioManager

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MuffleApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_muffle_point)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }

        ringtoneVolume.max =
            audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_RING)
        mediaVolume.max = audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_MUSIC)
        notificationsVolume.max =
            audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_NOTIFICATION)
        alarmVolume.max = audioManager.getMaxVolumeOfPhone(android.media.AudioManager.STREAM_ALARM)

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
                editMufflePointActivityViewModel.updateRadius(progress)
                rangeLabel.text = seekBar?.context?.getString(R.string.label_range, progress + 100)
            }

            /**
             * Notification that the user has started a touch gesture. Clients may want to use this
             * to disable advancing the seekbar.
             * @param seekBar The SeekBar in which the touch gesture began
             */
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Don't need this.
            }

            /**
             * Notification that the user has finished a touch gesture. Clients may want to use this
             * to re-enable advancing the seekbar.
             * @param seekBar The SeekBar in which the touch gesture began
             */
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Don't need this.
            }

        })

        deleteButton.setOnClickListener {
            Toast.makeText(this, "Deleted muffle point.", Toast.LENGTH_SHORT).show()
            editMufflePointActivityViewModel.deleteMufflePoint()
            this.finish()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            this.map = map
            editMufflePointActivityViewModel.mufflePoint.observe(this, Observer {
                editMufflePointActivityViewModel.mapCircle.value = map.addCircle(
                    CircleOptions().center(
                        LatLng(it.lat, it.lng)
                    ).radius(it.radius).strokeColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorAccent
                        )
                    )
                )
                editMufflePointActivityViewModel.mapZoom.observe(this, Observer { zoom ->
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lng), zoom))
                })
                map.addMarker(MarkerOptions().position(LatLng(it.lat, it.lng)))
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.lat, it.lng),
                        editMufflePointActivityViewModel.mapZoom.value!!
                    )
                )

                muffleName.setText(it.name)
                rangeLabel.text =
                    applicationContext.getString(R.string.label_range, it.radius.toInt())
                enableSwitch.isChecked = it.status >= MufflePoint.Status.ENABLE
                muffleRange.progress = it.radius.toInt()
                ringtoneCheckBox.isChecked = it.ringtoneVolume >= 0
                ringtoneVolume.progress = if (ringtoneCheckBox.isChecked) it.ringtoneVolume else 0
                mediaCheckBox.isChecked = it.mediaVolume >= 0
                mediaVolume.progress = if (mediaCheckBox.isChecked) it.mediaVolume else 0
                notificationsCheckBox.isChecked = it.notificationVolume >= 0
                notificationsVolume.progress =
                    if (notificationsCheckBox.isChecked) it.notificationVolume else 0
                alarmCheckBox.isChecked = it.alarmVolume >= 0
                alarmVolume.progress = if (alarmCheckBox.isChecked) it.alarmVolume else 0
            })

            val mufflePointId = intent.extras?.getString(MUFFLE_POINT_EXTRA)
            editMufflePointActivityViewModel.loadMufflePoint(mufflePointId.orEmpty())
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
                Toast.makeText(this, "Edited muffle point saved.", Toast.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch {
                    withContext(Dispatchers.Default) {
                        map.snapshot {
                            editMufflePointActivityViewModel.saveMufflePoint(
                                muffleName.text.toString(),
                                it,
                                enableSwitch.isChecked,
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
}
