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
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSettingFiltersBinding
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.filters.presentation.models.ShowViewState
import ru.practicum.android.diploma.search.ui.SearchFragment
import ru.practicum.android.diploma.util.BindingFragment
import ru.practicum.android.diploma.util.app.App

class SettingFiltersFragment : BindingFragment<FragmentSettingFiltersBinding>() {

    private val viewModel by viewModel<FiltersViewModel>()
    private var bundle: Bundle? = null
    lateinit var placeHolderText: String
    private lateinit var getFilters: Filters

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
        doOnTextChanged()

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

        binding.salaryEditText.setOnFocusChangeListener { _, hasFocus ->
            setSalaryEditTextColor(binding.salaryEditText.text.toString(), hasFocus)
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
                setSalaryEditTextColor(
                    binding.salaryEditText.text.toString(), binding.salaryEditText.hasFocus()
                )
                viewModel.setOnFocus(
                    binding.salaryEditText.text.toString(),
                    binding.salaryEditText.hasFocus()
                )
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
            viewModel.addOnlyWithSalary(false)
            binding.filterCheckbox.isChecked = false
            binding.clearAll.visibility = View.GONE
            binding.buttonApply.visibility = View.VISIBLE
        }
        binding.buttonApply.setOnClickListener {
            viewModel.writeFilters()
            findNavController().navigate(
                R.id.action_settingFilters_to_searchFragment,
                SearchFragment.createArgs(Gson().toJson(getFilters))
            )
        }
        binding.clearIcon.setOnClickListener {
            clearEditText()
            binding.salaryEditText.clearFocus()
            hideKeyBoard()
        }
        binding.filterCheckbox.setOnClickListener {
            viewModel.addOnlyWithSalary(binding.filterCheckbox.isChecked)
            if (binding.filterCheckbox.isChecked.equals(true)) {
                binding.clearAll.visibility = View.VISIBLE
            }
            binding.buttonApply.visibility = View.VISIBLE
        }
        binding.editTextBackground.setOnClickListener {
            binding.salaryEditText.requestFocus()
            showKeyBoard()
            binding.salaryEditText.setSelection(binding.salaryEditText.text.length)
        }

    }

    override fun onDetach() {
        super.onDetach()
        App.DATA_HAS_CHANGED = "no"
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.salaryEditText.windowToken, 0)
        binding.salaryEditText.clearFocus()
    }

    private fun showKeyBoard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun switchToPlaceOfWorkScreen() {
        binding.placeOfWorkButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingFilters_to_fragmentPlaceOfWork)
        }
    }

    fun switchToIndustriesScreen() {
        binding.industryButton.setOnClickListener {
            bundle = bundleOf(SCREEN to INDUSTRIES)
            findNavController().navigate(R.id.action_settingFilters_to_fragmentChooseFilter, bundle)
        }
    }

    fun back() {
        binding.arrowBack.setOnClickListener {
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
        getFilters = filters
        var placeOfWork = ""
        var industries = ""
        filters.countryName?.let {
            placeOfWork = it
            binding.placeOfWorkEditText.setText(placeOfWork)
            binding.placeOfWorkButton.visibility = View.INVISIBLE
            binding.placeOfWorkClear.visibility = View.VISIBLE
        }
        filters.areasNames?.let {
            placeOfWork += ", $it"
            binding.placeOfWorkEditText.setText(placeOfWork)
            if (placeOfWork.isNotEmpty()) {
                binding.placeOfWork.defaultHintTextColor =
                    resources.getColorStateList(R.color.hint_edit_text_filed, null)
            }

        }
        filters.industriesName?.let {
            industries += "$it "
            binding.industryEditText.setText(industries)
            binding.industryButton.visibility = View.INVISIBLE
            binding.industryClear.visibility = View.VISIBLE
        }


        if (filters.salary != 0) {
            binding.salaryEditText.setText(filters.salary.toString())
            binding.salaryEditText.setTextColor(resources.getColor(R.color.black))
        }

        if (filters.onlyWithSalary != false) {
            binding.filterCheckbox.isChecked = true
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
            is ShowViewState.showApplyButton -> showApplyButton()
            is ShowViewState.showClearAllButton -> showClearAllButton()
            is ShowViewState.hideClearAllButton -> binding.clearAll.visibility = View.GONE

        }
    }

    private fun showClearAllButton() {
        binding.clearAll.visibility = View.VISIBLE
    }

    private fun showApplyButton() {
        binding.buttonApply.visibility = View.VISIBLE
    }

    private fun clearEditText() {
        binding.salaryEditText.text?.clear()
    }

    private fun showClearIcon() {
        binding.clearIcon.visibility = View.VISIBLE
    }

    private fun hideClearIcon() {
        binding.clearIcon.visibility = View.GONE
    }

    private fun TextInputLayout.inputTextChangeHandler(text: CharSequence?) {
        if (text.isNullOrEmpty()) this.setInputStrokeColor(R.color.hint_edit_text_empty) else this.setInputStrokeColor(
            R.color.hint_edit_text_filed
        )
    }

    private fun EditText.editTextSalaryChangeHandler(text: CharSequence?) {
        if (text.isNullOrEmpty()) this.setSalaryInputStrokeColor(R.color.salary_hint_empty)
        else this.setSalaryInputStrokeColor(
            R.color.salary_hint_filed
        )
    }

    fun doOnTextChanged() {
        binding.placeOfWork.editText!!.doOnTextChanged { inputText, _, _, _ ->
            viewModel.showAllClearButtom()
            binding.placeOfWork.inputTextChangeHandler(inputText)
        }
        binding.industry.editText!!.doOnTextChanged { inputText, _, _, _ ->
            viewModel.showAllClearButtom()
            binding.industry.inputTextChangeHandler(inputText)
        }

    }

    private fun TextInputLayout.setInputStrokeColor(colorStateList: Int) {
        this.defaultHintTextColor = resources.getColorStateList(colorStateList, null)
    }

    private fun EditText.setSalaryInputStrokeColor(colorStateList: Int) {
        binding.sallaryHint.setTextColor(resources.getColor(colorStateList, null))

    }

    private fun setSalaryEditTextColor(text: CharSequence?, hasFocus: Boolean) {
        if (hasFocus) {
            binding.sallaryHint.setTextColor(resources.getColor(R.color.blue))
        }
        if (!hasFocus && !text.isNullOrEmpty()) {
            binding.sallaryHint.setTextColor(resources.getColor(R.color.black))
        }
        if (!hasFocus && text.isNullOrEmpty()) {
            binding.sallaryHint.setTextColor(resources.getColor(R.color.salary_hint_empty))
        }

    }

    companion object {
        const val SCREEN = "screen"
        const val COUNTRIES = "COUNTRIES"
        const val REGION = "REGION"
        const val INDUSTRIES = "INDUSTRIES"
    }
}