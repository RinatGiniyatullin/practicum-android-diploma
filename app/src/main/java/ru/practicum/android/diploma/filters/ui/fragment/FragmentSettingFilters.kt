package ru.practicum.android.diploma.filters.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSettingFiltersBinding
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.filters.presentation.models.ShowViewState
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
        viewModel.getShowViewStateLiveData().observe(requireActivity()){showView(it)}
        switchToPlaceOfWorkScreen()
        switchToIndustriesScreen()
        back()
        hideKeyBoard()

        binding.salaryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard()
                binding.salaryEditText.clearFocus()
                binding.clearIcon.visibility = View.GONE
                true
            } else {
                false
            }
        }
        binding.salaryEditText.setOnFocusChangeListener{_, hasFocus ->
            if(hasFocus){
                binding.salaryEditText.text?.clear()
                binding.clearIcon.visibility = View.GONE
            }

            viewModel.setOnFocus(binding.salaryEditText.text.toString(), hasFocus)
        }
        binding.salaryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setOnFocus(s.toString(), binding.salaryEditText.hasFocus())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        viewModel.getFiltersStateLiveData().observe(requireActivity()){
            render(it)
        }
        viewModel.showFiltersData()
        binding.placeOfWorkClear.setOnClickListener {
            clearPlaceWork()
        }
        binding.industryClear.setOnClickListener {
            clearIndustries()
        }
        binding.clearAll.setOnClickListener {
            clearPlaceWork()
            clearIndustries()
            viewModel.writeFilters()
        }
        binding.buttonApply.setOnClickListener {
            viewModel.writeFilters()
            findNavController().navigateUp() }
        binding.clearIcon.setOnClickListener {
            binding.salaryEditText.text?.clear()
        }

    }
    private fun hideKeyBoard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.salaryEditText.windowToken, 0)
        binding.salaryEditText.clearFocus()

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
        binding.arrowback.setOnClickListener {
            findNavController().navigateUp()
        }
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
            binding.industryButton.visibility = View.INVISIBLE
            binding.industryClear.visibility = View.VISIBLE
        }
        if(filters.salary!=0)binding.salaryEditText.setText(filters.salary.toString())

    }
    private fun clearPlaceWork(){
        binding.placeOfWorkEditText.text?.clear()
        binding.placeOfWorkClear.visibility = View.GONE
        binding.placeOfWorkButton.visibility = View.VISIBLE
        viewModel.clearCountry()
        viewModel.clearRegion()
    }
    private fun clearIndustries(){
        binding.industryEditText.text?.clear()
        binding.industryClear.visibility = View.GONE
        binding.industryButton.visibility = View.VISIBLE
        viewModel.clearIndustries()
    }
    private fun showView(state: ShowViewState){
        when(state){
            is ShowViewState.showClearIcon -> showClearIcon()
            is ShowViewState.hideClearIcon -> hideClearIcon()
        }
    }
    fun showClearIcon(){
        binding.clearIcon.visibility = View.VISIBLE
    }
    fun hideClearIcon(){
        binding.clearIcon.visibility = View.GONE
    }
    companion object{
        const val SCREEN = "screen"
        const val COUNTRIES = "COUNTRIES"
        const val REGION = "REGION"
        const val INDUSTRIES = "INDUSTRIES"
    }
}