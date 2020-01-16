package io.korti.muffle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import io.korti.muffle.viewmodel.SelectMufflePointActivityViewModel
import kotlinx.android.synthetic.main.activity_select_muffle_point.*
import kotlinx.android.synthetic.main.content_select_muffle_point.*
import javax.inject.Inject

class SelectMufflePointActivity : AppCompatActivity() {

    companion object {
        private val TAG = SelectMufflePointActivity::class.java.simpleName
        const val LOCATION_RESULT = "location_result"
    }

    @Inject
    lateinit var selectMufflePointActivityViewModel: SelectMufflePointActivityViewModel

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MuffleApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_muffle_point)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            this.finish()
        }

        fab.setOnClickListener { view ->
            selectMufflePointActivityViewModel.updateCameraToCurrentLocation()
            Toast.makeText(this, "Show current location.", Toast.LENGTH_SHORT).show()
        }

        searchText.setOnEditorActionListener { v, actionId, _ ->
            Log.d(TAG, "Search for location")
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    selectMufflePointActivityViewModel.requestLocation(v.text.toString())

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                    true
                }
                else -> false
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment

        mapFragment.getMapAsync {
            map = it

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    selectMufflePointActivityViewModel.cameraPosition.value,
                    16.5F
                )
            )
            selectMufflePointActivityViewModel.cameraPosition.observe(this, Observer { pos ->
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15F))
            })

            map.isMyLocationEnabled = true
            map.setOnMapClickListener { pos ->
                selectMufflePointActivityViewModel.updateLocation(
                    map.addMarker(
                        MarkerOptions().position(
                            pos
                        )
                    )
                )
            }
        }

        selectMufflePointActivityViewModel.init()
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
        menuInflater.inflate(R.menu.menu_select_muffle_point, menu)
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
                Toast.makeText(this, getString(R.string.toast_position_selected), Toast.LENGTH_SHORT)
                    .show()
                val result = Intent()
                result.putExtra(
                    LOCATION_RESULT,
                    selectMufflePointActivityViewModel.selectedPosition.value?.position
                )
                setResult(Activity.RESULT_OK, result)
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
