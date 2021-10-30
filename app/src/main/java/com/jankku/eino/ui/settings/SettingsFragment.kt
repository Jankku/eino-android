package com.jankku.eino.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jankku.eino.BuildConfig
import com.jankku.eino.R

class SettingsFragment : PreferenceFragmentCompat() {

    private var versionPref: Preference? = null
    private var githubPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        versionPref = findPreference(getString(R.string.app_key))
        versionPref?.summary = BuildConfig.VERSION_NAME

        githubPref = findPreference(getString(R.string.github_key))
        githubPref?.onPreferenceClickListener = githubListener
    }

    private val githubListener = Preference.OnPreferenceClickListener {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubPref?.summary as String?))
        startActivity(intent)
        true
    }
}