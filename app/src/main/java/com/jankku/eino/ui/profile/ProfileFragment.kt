package com.jankku.eino.ui.profile

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.jankku.eino.NavGraphDirections
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentProfileBinding
import com.jankku.eino.databinding.LayoutProfileBookBinding
import com.jankku.eino.databinding.LayoutProfileInfoBinding
import com.jankku.eino.databinding.LayoutProfileMovieBinding
import com.jankku.eino.network.response.profile.BookStats
import com.jankku.eino.network.response.profile.MovieStats
import com.jankku.eino.network.response.profile.ProfileResponse
import com.jankku.eino.network.response.profile.ScoreDistribution
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : BindingFragment<FragmentProfileBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentProfileBinding::inflate
    private val viewModel: ProfileViewModel by viewModels()
    private var _bookBinding: LayoutProfileBookBinding? = null
    private var _movieBinding: LayoutProfileMovieBinding? = null
    private var _infoBinding: LayoutProfileInfoBinding? = null
    private val bookBinding get() = _bookBinding!!
    private val movieBinding get() = _movieBinding!!
    private val infoBinding get() = _infoBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeNavRailHeader(requireActivity())
        _bookBinding = LayoutProfileBookBinding.bind(binding.root)
        _movieBinding = LayoutProfileMovieBinding.bind(binding.root)
        _infoBinding = LayoutProfileInfoBinding.bind(binding.root)
        showNavRail(requireActivity())
        showBottomNav(requireActivity())
        setupObservers()
        setupLogOutButton()
        setupSwipeToRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bookBinding = null
        _movieBinding = null
        _infoBinding = null
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getProfile()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            when (profile) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    profile.data?.run {
                        setupUserInfo(this)
                        setupBookStats(stats.book)
                        setupMovieStats(stats.movie)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                }
                else -> {}
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.ProfileError -> showSnackBar(binding.root, event.message)
                        is Event.LogoutSuccess -> {
                            findNavController().navigateSafe(NavGraphDirections.actionGlobalAuthGraph())
                        }
                        is Event.LogoutError -> {
                            showSnackBar(
                                requireView(),
                                requireView(),
                                event.message,
                                BaseTransientBottomBar.LENGTH_LONG
                            )
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun setupLogOutButton() {
        infoBinding.btnLogout.setOnClickListener {
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
        }
    }

    private fun setupUserInfo(profile: ProfileResponse) {
        with(profile) {
            infoBinding.tvUsernameValue.text = username
            infoBinding.tvRegistrationDateValue.text = utcToLocal(registration_date)
        }
    }

    private fun setupBookStats(stats: BookStats) {
        with(stats) {
            setupChart(bookBinding.bookChart, score_distribution)
            bookBinding.tvBookCountValue.text = count
            bookBinding.tvBookPagesReadValue.text = pages_read
            bookBinding.tvBookScoreAverageValue.text = score_average
        }
    }

    private fun setupMovieStats(stats: MovieStats) {
        with(stats) {
            setupChart(movieBinding.movieChart, score_distribution)
            movieBinding.tvMovieCountValue.text = count
            movieBinding.tvMovieWatchtimeValue.text = watch_time
            movieBinding.tvMovieScoreAverageValue.text = score_average
        }
    }

    private fun setupChart(chart: BarChart, scoreDistribution: List<ScoreDistribution>) {
        val currentTextColor =
            MaterialColors.getColor(requireContext(), R.attr.colorOnSurface, Color.WHITE)
        val currentAccentColor =
            MaterialColors.getColor(requireContext(), R.attr.colorPrimary, Color.WHITE)

        chart.apply {
            xAxis.apply {
                setDrawGridLines(true)
                position = XAxis.XAxisPosition.TOP
                labelCount = 11
                textColor = currentTextColor
                textSize = 12f
            }
            axisLeft.apply {
                axisMinimum = 0f
                setDrawGridLines(true)
                textColor = currentTextColor
                textSize = 12f
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
        }


        val entries = scoreDistribution.map { distribution ->
            BarEntry(distribution.score.toFloat(), distribution.count.toFloat())
        }
        val dataSet = BarDataSet(entries, null).apply {
            color = currentAccentColor
            valueTextColor = currentTextColor
            valueTextSize = 13f
            valueFormatter = TableValueFormatter()
        }

        chart.apply {
            setHardwareAccelerationEnabled(true)
            setTouchEnabled(false)
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)
            setFitBars(true)
            setNoDataText(getString(R.string.profile_chart_no_data))
            setNoDataTextColor(currentTextColor)
            description = null
            data = BarData(dataSet)
            setExtraOffsets(0f, 15f, 0f, 0f)
            invalidate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigateSafe(R.id.action_profileFragment_to_settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}