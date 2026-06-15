package com.example.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CivicViewModel

class DashboardFragment : Fragment() {
    private lateinit var viewModel: CivicViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Shared ViewModel scope matching Activity lifecycle so states persist across screens
        viewModel = ViewModelProvider(requireActivity())[CivicViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                MyApplicationTheme {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToNewReport = {
                            findNavController().navigate(R.id.action_dashboard_to_newReport)
                        },
                        onNavigateToSettings = {
                            findNavController().navigate(R.id.action_dashboard_to_settings)
                        },
                        onNavigateToReportDetail = { reportId ->
                            val bundle = Bundle().apply {
                                putInt("reportId", reportId)
                            }
                            findNavController().navigate(R.id.action_dashboard_to_reportDetail, bundle)
                        }
                    )
                }
            }
        }
    }
}
