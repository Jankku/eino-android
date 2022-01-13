package com.jankku.eino.ui.settings

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.jankku.eino.BuildConfig
import com.jankku.eino.NavGraphDirections
import com.jankku.eino.R
import com.jankku.eino.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private var accountPref: Preference? = null
    private var versionPref: Preference? = null
    private var themePref: Preference? = null
    private var githubPref: Preference? = null
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        versionPref = findPreference(getString(R.string.settings_app_key))
        versionPref?.summary = BuildConfig.VERSION_NAME

        githubPref = findPreference(getString(R.string.settings_github_key))
        githubPref?.onPreferenceClickListener = githubListener

        themePref = findPreference(getString(R.string.settings_theme_key))
        themePref?.onPreferenceChangeListener = themeListener

        accountPref = findPreference(getString(R.string.settings_account_key))
        accountPref?.onPreferenceClickListener = accountListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeNavRailHeader(requireActivity())
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.username.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> accountPref?.title = it.data
                is Result.Error -> accountPref?.title = "Error: couldn't find username"
                else -> {}
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is Event.LogoutSuccessEvent -> {
                            findNavController().navigateSafe(NavGraphDirections.actionGlobalAuthGraph())
                        }
                        is Event.LogoutErrorEvent -> {
                            view?.let { view ->
                                showSnackBar(
                                    view,
                                    it.message,
                                    BaseTransientBottomBar.LENGTH_LONG
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }

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

    private val accountListener = Preference.OnPreferenceClickListener {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_logout_title)
            .setPositiveButton(R.string.dialog_logout_button_positive_text) { dialog: DialogInterface, _: Int ->
                viewModel.logOut()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_logout_button_negative_text) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
        true
    }
}