package ru.practicum.android.diploma.filters.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSettingFiltersBinding
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.util.BindingFragment

class FragmentSettingFilters:BindingFragment<FragmentSettingFiltersBinding>() {

    val viewModel by viewModel<FiltersViewModel>()
    var bundle:Bundle? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingFiltersBinding {
        return FragmentSettingFiltersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchToPlaceOfWorkScreen()
        switchToIndustriesScreen()

        viewModel.getFiltersStateLiveData().observe(requireActivity()){
            render(it)
        }
        viewModel.showFiltersData()
        binding.placeOfWorkClear.setOnClickListener {
            clearPlaceWork()
        }
        binding.clearAll.setOnClickListener {
            clearPlaceWork()
        }
        binding.buttonApply.setOnClickListener {
            viewModel.writeFilters()
            findNavController().navigateUp() }

    }
    fun switchToPlaceOfWorkScreen(){
        binding.placeOfWorkButton.setOnClickListener{
            findNavController().navigate(R.id.action_settingFilters_to_fragmentPlaceOfWork)
        }
    }
    fun switchToIndustriesScreen(){
        binding.industryButton.setOnClickListener{
            bundle = bundleOf(SCREEN to INDUSTRIES)
            findNavController().navigate(R.id.action_settingFilters_to_fragmentChooseFilter, bundle)
        }
    }
    fun back(){
        binding.arrowback
    }
    private fun render(state: FiltersDataState) {
        when (state) {
            is FiltersDataState.filtersData -> showFiltersData(state.filters)
        }
    }
    private fun showFiltersData(filters: Filters) {
        var placeOfWork = ""
        var industries = ""
        filters.countryName?.let {
            placeOfWork = it
            binding.placeOfWorkEditText.setText(placeOfWork)
            binding.placeOfWorkButton.visibility = View.INVISIBLE
            binding.placeOfWorkClear.visibility = View.VISIBLE
        }
        filters.areasNames?.let {
            placeOfWork+=", $it"
            binding.placeOfWorkEditText.setText(placeOfWork)
        }
        filters.industriesName?.let {
            industries+="$it "
            binding.industryEditText.setText(industries)
        }

    }
    private fun clearPlaceWork(){
        binding.placeOfWorkEditText.text?.clear()
        binding.placeOfWorkClear.visibility = View.GONE
        binding.placeOfWorkButton.visibility = View.VISIBLE
        viewModel.clearCountry()
        viewModel.clearRegion()
    }
    companion object{
        const val SCREEN = "screen"
        const val COUNTRIES = "COUNTRIES"
        const val REGION = "REGION"
        const val INDUSTRIES = "INDUSTRIES"
    }
}