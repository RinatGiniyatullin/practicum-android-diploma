package ru.practicum.android.diploma.filters.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentPlaceOfWorkBinding
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.REGION
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.SCREEN
import ru.practicum.android.diploma.util.BindingFragment

class FragmentPlaceOfWork : BindingFragment<FragmentPlaceOfWorkBinding>() {

    private val viewModel by viewModel<FiltersViewModel>()

    private var bundle: Bundle? = null
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaceOfWorkBinding {
        return FragmentPlaceOfWorkBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFiltersStateLiveData().observe(requireActivity()) { render(it) }
        viewModel.showFiltersData()



        binding.clearCountryName.setOnClickListener {
            clearCountry()
            clearRegion()

        }
        binding.clearRegion.setOnClickListener {
            clearRegion()
        }



        binding.buttonApply.setOnClickListener {
            viewModel.writeFilters()
            findNavController().navigateUp()
        }
        binding.chooseCountryBottom.setOnClickListener {
            bundle = bundleOf(FragmentSettingFilters.SCREEN to FragmentSettingFilters.COUNTRIES)
            findNavController().navigate(
                R.id.action_fragmentPlaceOfWork_to_fragmentChooseFilter,
                bundle
            )
        }
        binding.regionButton.setOnClickListener {
            bundle = bundleOf(SCREEN to REGION)
            findNavController().navigate(
                R.id.action_fragmentPlaceOfWork_to_fragmentChooseFilter,
                bundle
            )
        }
    }

    private fun clearRegion() {
        binding.regionButton.visibility = View.VISIBLE
        binding.clearRegion.visibility = View.GONE
        binding.regionEditText.text?.clear()
        viewModel.clearRegion()

    }

    private fun clearCountry() {
        binding.chooseCountryBottom.visibility = View.VISIBLE
        binding.clearCountryName.visibility = View.GONE
        binding.countryEditText.text?.clear()
        binding.regionEditText.text?.clear()
        viewModel.clearCountry()
    }

    private fun render(state: FiltersDataState) {
        when (state) {
            is FiltersDataState.filtersData -> showFiltersData(state.filters)
        }
    }

    private fun showFiltersData(filters: Filters) {
        filters.countryName?.let {
            binding.countryEditText.setText(it)
            binding.buttonApply.visibility = View.VISIBLE
            binding.chooseCountryBottom.visibility = View.INVISIBLE
            binding.clearCountryName.visibility = View.VISIBLE
        }

        filters.areasNames?.let {
            binding.regionEditText.setText(it)
            binding.buttonApply.visibility = View.VISIBLE
            binding.regionButton.visibility = View.INVISIBLE
            binding.clearRegion.visibility = View.VISIBLE
        }
    }
}