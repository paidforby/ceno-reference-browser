/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package ie.equalit.ceno.settings

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.net.InetAddresses
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ie.equalit.ceno.BuildConfig
import ie.equalit.ceno.R
import ie.equalit.ceno.ext.getPreferenceKey
import mozilla.components.support.ktx.android.view.showKeyboard
import mozilla.components.support.ktx.kotlin.ifNullOrEmpty
import java.util.Locale

class NetworkSettingsFragment : PreferenceFragmentCompat() {

    private val btSourcesMap = mutableMapOf<String, String>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.network_detail_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        setUpBTSources()
        setupPreferences()
        getActionBar().apply{
            show()
            setTitle(R.string.preferences_ceno_network_config)
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.ceno_action_bar)))
        }
    }


    private fun setupPreferences() {

        val preferenceAboutOuinetProtocol = getPreference(R.string.pref_key_about_ouinet_protocol)
        val preferenceReachabilityStatus = getPreference(R.string.pref_key_ouinet_reachability_status)
        val preferenceLocalUdpEndpoint = getPreference(R.string.pref_key_ouinet_local_udp_endpoints)
        val preferenceExternalUdpEndpoint = getPreference(R.string.pref_key_ouinet_external_udp_endpoints)
        val preferencePublicUdpEndpoint = getPreference(R.string.pref_key_ouinet_public_udp_endpoints)
        val preferenceUpnpStatus = getPreference(R.string.pref_key_ouinet_upnp_status)
        val extraBootstrapBittorrentKey = requireContext().getPreferenceKey(R.string.pref_key_ouinet_extra_bittorrent_bootstraps)

        preferenceAboutOuinetProtocol?.summary = "${CenoSettings.getOuinetProtocol(requireContext())}"
        preferenceReachabilityStatus?.summary = CenoSettings.getReachabilityStatus(requireContext())
        preferenceLocalUdpEndpoint?.summary = CenoSettings.getLocalUdpEndpoint(requireContext()).ifNullOrEmpty { getString(R.string.not_applicable) }
        preferenceExternalUdpEndpoint?.summary = CenoSettings.getExternalUdpEndpoint(requireContext()).ifNullOrEmpty { getString(R.string.not_applicable) }
        preferencePublicUdpEndpoint?.summary = CenoSettings.getPublicUdpEndpoint(requireContext()).ifNullOrEmpty { getString(R.string.not_applicable) }
        preferenceUpnpStatus?.summary = CenoSettings.getUpnpStatus(requireContext())

        val preferenceExtraBitTorrentBootstrap = findPreference<ListPreference>(extraBootstrapBittorrentKey)

        preferenceExtraBitTorrentBootstrap?.entries = btSourcesMap.keys.toList().toTypedArray()
        preferenceExtraBitTorrentBootstrap?.entryValues = btSourcesMap.values.toList().toTypedArray()
        preferenceExtraBitTorrentBootstrap?.onPreferenceChangeListener = getClickListenerForExtraBitTorrentBootstraps()
        preferenceExtraBitTorrentBootstrap?.summary = getBTPreferenceSummary()

    }

    private fun getClickListenerForExtraBitTorrentBootstraps(): Preference.OnPreferenceChangeListener {
        return Preference.OnPreferenceChangeListener { _, newValue ->

            val context = requireContext()
            val dialogView = View.inflate(context, R.layout.extra_bittorrent_bootstrap_override_dialog, null)
            val customBTSourcesView = dialogView.findViewById<EditText>(R.id.bootstrap)

            when(newValue) {
                getString(R.string.bt_sources_none) -> {
                    CenoSettings.setBTSource(requireContext(), "")
                    getPreference(R.string.pref_key_ouinet_extra_bittorrent_bootstraps)?.summary = getBTPreferenceSummary()
                }
                getString(R.string.bt_sources_custom) -> {
                    AlertDialog.Builder(context).apply {
                        setTitle(context.getString(R.string.customize_extra_bittorrent_bootstrap))
                        setView(dialogView)
                        setNegativeButton(R.string.customize_addon_collection_cancel) { dialog: DialogInterface, _ ->
                            dialog.cancel()
                        }

                        setPositiveButton(R.string.customize_add_bootstrap_save) { _, _ ->
                            val ipAddresses = customBTSourcesView.text.toString().trim().removeSuffix(",").split(",")

                            for (ipAddress in ipAddresses) {
                                val trimmedIpAddress = ipAddress.trim().removeSuffix(",")

                                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && InetAddresses.isNumericAddress(trimmedIpAddress))
                                    || ((Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) && Patterns.IP_ADDRESS.matcher(trimmedIpAddress).matches())) {
                                    CenoSettings.setBTSource(requireContext(), customBTSourcesView.text.toString().trim())
                                    getPreference(R.string.pref_key_ouinet_extra_bittorrent_bootstraps)?.summary = getBTPreferenceSummary()
                                } else {
                                    CenoSettings.setBTSource(requireContext(), "")
                                    getPreference(R.string.pref_key_ouinet_extra_bittorrent_bootstraps)?.summary = getBTPreferenceSummary()
                                    Toast.makeText(requireContext(), getString(R.string.bt_invalid_source_error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        customBTSourcesView.setText(
                            CenoSettings.getExtraBitTorrentBootstrap(
                                context
                            )?.trim()
                        )
                        customBTSourcesView.requestFocus()
                        customBTSourcesView.showKeyboard()
                        create()
                    }.show()
                }
                else -> {
                    CenoSettings.setBTSource(requireContext(), newValue as String)
                    getPreference(R.string.pref_key_ouinet_extra_bittorrent_bootstraps)?.summary = getBTPreferenceSummary()
                }
            }

            true
        }
    }

    private fun getPreference(key : Int) : Preference? {
        val prefKey = requireContext().getPreferenceKey(key)
        return findPreference(prefKey)
    }

    private fun setUpBTSources() {
        for (entry in BuildConfig.BT_BOOTSTRAP_EXTRAS) btSourcesMap[Locale("", entry[0]).displayCountry] = entry[1]
        btSourcesMap[getString(R.string.bt_sources_custom)] = getString(R.string.bt_sources_custom)
        btSourcesMap[getString(R.string.bt_sources_none)] = getString(R.string.bt_sources_none)
    }

    private fun getBTPreferenceSummary(): String? {
        val currentValue = CenoSettings.getExtraBitTorrentBootstrap(requireContext())?.trim()

        return when {
            currentValue.isNullOrEmpty() -> getString(R.string.bt_sources_none)
            btSourcesMap.values.contains(currentValue) -> btSourcesMap.entries.find { it.value.trim() == currentValue }?.key
            else -> currentValue
        }
    }

    private fun getActionBar() = (activity as AppCompatActivity).supportActionBar!!
}
