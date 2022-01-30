package com.jankku.eino.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jankku.eino.BuildConfig
import com.jankku.eino.R
import com.jankku.eino.util.applyTheme
import com.jankku.eino.util.removeNavRailHeader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private var versionPref: Preference? = null
    private var themePref: Preference? = null
    private var githubPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        versionPref = findPreference(getString(R.string.settings_app_key))
        versionPref?.summary = BuildConfig.VERSION_NAME

        githubPref = findPreference(getString(R.string.settings_github_key))
        githubPref?.onPreferenceClickListener = githubListener

        themePref = findPreference(getString(R.string.settings_theme_key))
        themePref?.onPreferenceChangeListener = themeListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeNavRailHeader(requireActivity())
    }

    private val githubListener = Preference.OnPreferenceClickListener {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubPref?.summary as String?))
        startActivity(intent)
        true
    }

    private val themeListener = Preference.OnPreferenceChangeListener { _, value ->
        applyTheme(requireActivity(), value.toString(), true)
        true
    }
}