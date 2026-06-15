package com.example.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ui.screens.ReportDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CivicViewModel

class ReportDetailFragment : Fragment() {
    private lateinit var viewModel: CivicViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Access Activity-scoped CivicViewModel to share reactive reports list state
        viewModel = ViewModelProvider(requireActivity())[CivicViewModel::class.java]
        val reportId = arguments?.getInt("reportId") ?: -1

        return ComposeView(requireContext()).apply {
            setContent {
                MyApplicationTheme {
                    ReportDetailScreen(
                        reportId = reportId,
                        viewModel = viewModel,
                        onNavigateBack = {
                            findNavController().navigateUp()
                        }
                    )
                }
            }
        }
    }
}
