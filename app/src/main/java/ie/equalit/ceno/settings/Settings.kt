/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package ie.equalit.ceno.settings

import android.content.Context
import androidx.preference.PreferenceManager
import ie.equalit.ceno.R

object Settings {
    fun shouldShowOnboarding(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_show_onboarding), false
        )
    fun isMobileDataEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_mobile_data), false
        )

    fun isTelemetryEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_telemetry),
            true
        )

    fun getOverrideAmoUser(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            context.getString(R.string.pref_key_override_amo_user),
            ""
        ) ?: ""

    fun getOverrideAmoCollection(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            context.getString(R.string.pref_key_override_amo_collection),
            ""
        ) ?: ""

    fun setShowOnboarding(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_show_onboarding)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun setMobileData(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_mobile_data)
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply()
    }

    fun setOverrideAmoUser(context: Context, value: String) {
        val key = context.getString(R.string.pref_key_override_amo_user)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun setOverrideAmoCollection(context: Context, value: String) {
        val key = context.getString(R.string.pref_key_override_amo_collection)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun isAmoCollectionOverrideConfigured(context: Context): Boolean {
        return getOverrideAmoUser(context).isNotEmpty() && getOverrideAmoCollection(context).isNotEmpty()
    }

    fun deleteOpenTabs(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_delete_open_tabs), false
        )
    }

    fun deleteBrowsingHistory(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_delete_browsing_history), false
        )
    }

    fun deleteCookies(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_delete_cookies_now), false
        )
    }

    fun deleteCache(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_delete_cache_now), false
        )
    }

    fun deleteSitePermissions(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            context.getString(R.string.pref_key_delete_site_permissions), false
        )
    }

    fun setDeleteOpenTabs(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_delete_open_tabs)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun setDeleteBrowsingHistory(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_delete_browsing_history)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun setDeleteCookies(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_delete_cookies_now)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun setDeleteCache(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_delete_cache_now)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }
    fun setDeleteSitePermissions(context: Context, value: Boolean) {
        val key = context.getString(R.string.pref_key_delete_site_permissions)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }
}
