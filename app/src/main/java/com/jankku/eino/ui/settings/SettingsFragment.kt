package com.jankku.eino.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jankku.eino.BuildConfig
import com.jankku.eino.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val versionPref: Preference? = findPreference(getString(R.string.app_key))
        versionPref?.summary = BuildConfig.VERSION_NAME
    }
}