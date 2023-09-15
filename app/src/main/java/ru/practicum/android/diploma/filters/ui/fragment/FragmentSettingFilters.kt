package ru.practicum.android.diploma.filters.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import ru.practicum.android.diploma.util.app.App

class FragmentSettingFilters : BindingFragment<FragmentSettingFiltersBinding>() {

    val viewModel by viewModel<FiltersViewModel>()
    var bundle: Bundle? = null
    lateinit var placeHolderText:String
    var lastSalary:String = ""

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingFiltersBinding {
        return FragmentSettingFiltersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getShowViewStateLiveData().observe(requireActivity()) { showView(it) }
        switchToPlaceOfWorkScreen()
        switchToIndustriesScreen()
        back()
        viewModel.showFiltersData()
        placeHolderText = requireActivity().getText(R.string.enter_salary).toString()

        binding.salaryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard()
                binding.salaryEditText.clearFocus()
                binding.clearIcon.visibility = View.GONE
                if(binding.salaryEditText.text!!.isNotEmpty()){
                    binding.salaryTextInput.defaultHintTextColor = resources.getColorStateList(R.color.black, null)
                }else{
                    binding.salaryTextInput.defaultHintTextColor = resources.getColorStateList(R.color.gray, null)
                }
                true
            } else {
                false
            }
        }

        binding.salaryEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.setOnFocus(binding.salaryEditText.text.toString(), hasFocus)
            binding.clearIcon.visibility = View.GONE
        }

        binding.salaryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.isNullOrEmpty()) {
                    viewModel.addSalary("0")
                } else {
                    viewModel.addSalary(s.toString())
                }
                viewModel.setOnFocus(binding.salaryEditText.text.toString(), binding.salaryEditText.hasFocus())
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        viewModel.getFiltersStateLiveData().observe(requireActivity()) {
            render(it)
        }

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
            clearEditText()
            binding.salaryTextInput.defaultHintTextColor = resources.getColorStateList(R.color.gray, null)
            viewModel.addOnlyWithSalary(false)
            binding.filterCheckbox.isChecked = false
            binding.clearAll.visibility = View.GONE
            binding.buttonApply.visibility = View.VISIBLE
        }
        binding.buttonApply.setOnClickListener {
            viewModel.writeFilters()
            findNavController().navigateUp()
        }
        binding.clearIcon.setOnClickListener {
            clearEditText()
        }
        binding.filterCheckbox.setOnClickListener {
            viewModel.addOnlyWithSalary(binding.filterCheckbox.isChecked)
            if(binding.filterCheckbox.isChecked.equals(true)){
                binding.clearAll.visibility= View.VISIBLE
            }
            binding.buttonApply.visibility = View.VISIBLE
        }

    }

    override fun onDetach() {
        super.onDetach()
        App.DADA_HAS_CHANGHED = "no"
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.salaryEditText.windowToken, 0)
        binding.salaryEditText.clearFocus()

    }

    private fun switchToPlaceOfWorkScreen() {
        binding.placeOfWorkButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingFilters_to_fragmentPlaceOfWork)
        }
    }

    private fun switchToIndustriesScreen() {
        binding.industryButton.setOnClickListener {
            bundle = bundleOf(SCREEN to INDUSTRIES)
            findNavController().navigate(R.id.action_settingFilters_to_fragmentChooseFilter, bundle)
        }
    }

    private fun back() {
        binding.arrowback.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun render(state: FiltersDataState) {
        when (state) {
            is FiltersDataState.filtersData -> showFiltersData(state.filters)
            else -> {}
        }
    }

    private fun showFiltersData(filters: Filters) {
        viewModel.hasDataChanged()
        var placeOfWork = ""
        var industries = ""
        filters.countryName?.let {
            placeOfWork = it
            binding.placeOfWorkEditText.setText(placeOfWork)
            binding.placeOfWorkButton.visibility = View.INVISIBLE
            binding.placeOfWorkClear.visibility = View.VISIBLE
            binding.clearAll.visibility= View.VISIBLE
        }
        filters.areasNames?.let {
            placeOfWork += ", $it"
            binding.placeOfWorkEditText.setText(placeOfWork)

        }
        filters.industriesName?.let {
            industries += "$it "
            binding.industryEditText.setText(industries)
            binding.industryButton.visibility = View.INVISIBLE
            binding.industryClear.visibility = View.VISIBLE
            binding.clearAll.visibility= View.VISIBLE
        }


        if (filters.salary != 0) {
            lastSalary = filters.salary.toString()
            binding.salaryEditText.setText(filters.salary.toString())
            binding.clearAll.visibility= View.VISIBLE
        }

        if (filters.onlyWithSalary != false) {
            binding.filterCheckbox.isChecked = true
            binding.clearAll.visibility= View.VISIBLE
        }

    }

    private fun clearPlaceWork() {
        binding.placeOfWorkEditText.text?.clear()
        binding.placeOfWorkClear.visibility = View.GONE
        binding.placeOfWorkButton.visibility = View.VISIBLE
        viewModel.clearCountry()
        viewModel.clearRegion()
        showApplyButton()
    }

    private fun clearIndustries() {
        binding.industryEditText.text?.clear()
        binding.industryClear.visibility = View.GONE
        binding.industryButton.visibility = View.VISIBLE
        viewModel.clearIndustries()
        showApplyButton()
    }

    private fun showView(state: ShowViewState) {
        when (state) {
            is ShowViewState.showClearIcon -> showClearIcon()
            is ShowViewState.hideClearIcon -> hideClearIcon()
            is ShowViewState.clearEditText  -> clearEditText()
            is ShowViewState.showApplyButton  -> showApplyButton()
            is ShowViewState.showClearAllButton -> showClearAllButton()

        }
    }
    private fun showClearAllButton(){
        binding.clearAll.visibility = View.VISIBLE
    }
    private fun showApplyButton(){
        binding.buttonApply.visibility = View.VISIBLE
    }
    private fun clearEditText(){
        binding.salaryEditText.text?.clear()
    }

    private fun showClearIcon() {
        binding.salaryEditText.setTextColor(requireActivity().getColor(R.color.black))
        binding.clearIcon.visibility = View.VISIBLE
    }

    private fun hideClearIcon() {
        binding.clearIcon.visibility = View.GONE
    }


    companion object {
        const val SCREEN = "screen"
        const val COUNTRIES = "COUNTRIES"
        const val REGION = "REGION"
        const val INDUSTRIES = "INDUSTRIES"
    }
}