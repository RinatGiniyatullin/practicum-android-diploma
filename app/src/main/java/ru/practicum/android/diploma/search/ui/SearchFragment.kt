package ru.practicum.android.diploma.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.details.presentation.ui.VacancyFragment
import ru.practicum.android.diploma.search.domain.SearchState
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.BindingFragment
import ru.practicum.android.diploma.util.adapter.VacancyAdapter
import ru.practicum.android.diploma.util.debounce


class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private lateinit var adapter: VacancyAdapter
    private lateinit var onVacancyClickDebounce: (Vacancy) -> Unit
    private lateinit var vacancySearchDebounce: (String) -> Unit
    private val viewModel by viewModel<SearchViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { render(it) }

        viewModel.searchIconStateLiveData.observe(viewLifecycleOwner) { state ->
            changeIconInEditText(state)
        }

        viewModel.filterIconStateLiveData.observe(viewLifecycleOwner) { state ->
            changeFilterIcon(state)
        }

        initAdapter()
        listener()
    }

    private fun initAdapter() {
        adapter = VacancyAdapter(ArrayList<Vacancy>())
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun listener() {

        viewModel.showFilters()

        vacancySearchDebounce = debounce<String>(
            SEARCH_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { text ->
            search(text)
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) hideKeyBoard()
            viewModel.setOnFocus(binding.searchEditText.text.toString(), hasFocus)
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vacancySearchDebounce(s.toString())
                viewModel.setOnFocus(s.toString(), binding.searchEditText.hasFocus())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(binding.searchEditText.text.toString())
                true
            }
            false
        }

        binding.editTextCloseImage.setOnClickListener {
            clearInputEditText()
        }

        binding.filterIcon.setOnClickListener {
            openFilters()
        }

        adapter.itemClickListener = { position, vacancy ->
            onVacancyClickDebounce(vacancy)
        }

        onVacancyClickDebounce = debounce<Vacancy>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { vacancy ->
            openVacancy(vacancy)
        }

        binding.searchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val pos =
                        (binding.searchRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    val itemsCount = adapter.itemCount
                    if (pos >= itemsCount - 1) {
                        viewModel.onLastItemReached()
                    }
                }
            }
        })
    }

    private fun showVacanciesList(vacancies: List<Vacancy>, foundValue: Int) {

        binding.searchResult.visibility = View.VISIBLE
        binding.searchResult.text =
            resources.getQuantityString(R.plurals.search_result_number, foundValue, foundValue)
        binding.searchRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.progressBarForLoad.visibility = View.GONE
        binding.progressBarInEnd.visibility = View.GONE

        hideKeyBoard()
        adapter.setVacancies(vacancies)
    }

    private fun showError(errorMessage: String) {
        binding.searchResult.text = errorMessage
        binding.searchResult.visibility = View.VISIBLE
        binding.searchRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.progressBarForLoad.visibility = View.GONE
        binding.progressBarInEnd.visibility = View.GONE

        adapter.setVacancies(null)
    }

    private fun showEmpty(emptyMessage: String) {
        binding.searchResult.text = emptyMessage
        binding.searchResult.visibility = View.VISIBLE
        binding.searchRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.progressBarForLoad.visibility = View.GONE
        binding.progressBarInEnd.visibility = View.GONE

        adapter.setVacancies(null)
    }

    private fun changeIconInEditText(state: SearchIconState) {
        when (state) {
            SearchIconState.CloseSearchIcon -> setCloseIconForEditText()
            SearchIconState.SearchSearchIcon -> setSearchIconForEditText()
        }
    }

    private fun changeFilterIcon(state: FilterIconState) {
        when (state) {
            FilterIconState.NoFilters -> showEmptyFilterIcon()
            FilterIconState.YesFilters -> showNoEmptyFilterIcon()
        }
    }

    private fun setCloseIconForEditText() {
        binding.editTextSearchImage.visibility = View.GONE
        binding.editTextCloseImage.visibility = View.VISIBLE
    }

    private fun setSearchIconForEditText() {
        binding.editTextCloseImage.visibility = View.GONE
        binding.editTextSearchImage.visibility = View.VISIBLE
    }

    private fun showEmptyFilterIcon() {
        binding.filterIcon.setImageResource(R.drawable.filter_off)
    }

    private fun showNoEmptyFilterIcon() {
        binding.filterIcon.setImageResource(R.drawable.filter_on)
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        binding.searchEditText.clearFocus()
    }

    private fun showLoading() {

        binding.searchResult.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.progressBarForLoad.visibility = View.VISIBLE
        binding.progressBarInEnd.visibility = View.GONE
    }

    private fun showAddLoading() {
        binding.searchResult.visibility = View.VISIBLE
        binding.searchRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.progressBarForLoad.visibility = View.GONE
        binding.progressBarInEnd.visibility = View.VISIBLE
    }

    private fun showStopLoadind() {
        binding.searchResult.visibility = View.VISIBLE
        binding.searchRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.progressBarForLoad.visibility = View.GONE
        binding.progressBarInEnd.visibility = View.GONE
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.FirstLoading -> showLoading()
            is SearchState.AddLoading -> showAddLoading()
            is SearchState.VacancyContent -> showVacanciesList(state.vacancies, state.foundValue)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
            SearchState.StopLoad -> showStopLoadind()
        }
    }

    private fun clearInputEditText() {
        binding.searchEditText.text.clear()
        binding.searchEditText.clearFocus()
        viewModel.clearInputEditText()

        binding.searchResult.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.progressBarForLoad.visibility = View.GONE
        binding.progressBarInEnd.visibility = View.GONE
        hideKeyBoard()
        adapter.notifyDataSetChanged()
    }

    private fun search(text: String) {
        viewModel.search(text)
    }

    private fun openVacancy(vacancy: Vacancy) {

        findNavController().navigate(
            R.id.action_searchFragment_to_vacancyFragment,
            VacancyFragment.createArgs(Gson().toJson(vacancy))
        )
    }

    private fun openFilters() {
        findNavController().navigate(
            R.id.action_searchFragment_to_settingFilters
        )
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}