package io.korti.muffle

import android.app.Dialog
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

class OpenSourceLicensesDialog : DialogFragment() {

    companion object {
        fun showLicences(activity: AppCompatActivity) {
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val previousFragment = fragmentManager.findFragmentByTag("dialog_license")
            if (previousFragment != null) {
                fragmentTransaction.remove(previousFragment)
            }
            fragmentTransaction.addToBackStack(null)
            OpenSourceLicensesDialog().show(fragmentManager, "dialog_license")
        }
    }

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * [.onCreateView] does not need
     * to be implemented since the AlertDialog takes care of its own content.
     *
     *
     * This method will be called after [.onCreate] and
     * before [.onCreateView].  The
     * default implementation simply instantiates and returns a [Dialog]
     * class.
     *
     *
     * *Note: DialogFragment own the [ Dialog.setOnCancelListener][Dialog.setOnCancelListener] and [ Dialog.setOnDismissListener][Dialog.setOnDismissListener] callbacks.  You must not set them yourself.*
     * To find out about these events, override [.onCancel]
     * and [.onDismiss].
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val webView = WebView(requireActivity())
        webView.loadUrl("file:///android_asset/open_source_licenses.html")
        return AlertDialog.Builder(requireActivity()).setTitle("Open Source Licenses")
            .setView(webView).setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }.create()
    }
}