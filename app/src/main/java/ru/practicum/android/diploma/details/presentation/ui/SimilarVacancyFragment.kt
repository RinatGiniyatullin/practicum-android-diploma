package ru.practicum.android.diploma.details.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentSimilarVacancyBinding
import ru.practicum.android.diploma.details.presentation.SimilarVacancyViewModel
import ru.practicum.android.diploma.search.domain.SearchState
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.BindingFragment
import ru.practicum.android.diploma.util.adapter.VacancyAdapter

class SimilarVacancyFragment: BindingFragment<FragmentSimilarVacancyBinding>() {

    private val viewModel by viewModel<SimilarVacancyViewModel>()
    private lateinit var adapter: VacancyAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSimilarVacancyBinding {
        return FragmentSimilarVacancyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()

        viewModel.getSimilarVacanciesById(requireArguments().getString(VACANCY_ID)!!)

        viewModel.state.observe(viewLifecycleOwner){state ->
            when (state) {
                is SearchState.Loading -> {}
                is SearchState.VacancyContent -> showVacanciesList(state.vacancies)
                is SearchState.Error -> showError(state.errorMessage)
                is SearchState.Empty -> showEmpty(state.message)
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.getSimilarVacanciesById(requireArguments().getString(VACANCY_ID)!!)
        }

        binding.backIcon.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun initAdapters(){
        adapter = VacancyAdapter(ArrayList())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun showVacanciesList(vacancies: List<Vacancy>) {
        binding.searchResult.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.refreshButton.visibility = View.GONE
        adapter.setVacancies(vacancies)
        adapter.notifyDataSetChanged()
    }

    private fun showError(errorMessage: String) {
        binding.searchResult.text = errorMessage
        showToast(errorMessage)
        binding.searchResult.visibility = View.VISIBLE
        binding.refreshButton.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun showToast(message: String){
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_LONG)
            .show()
    }

    private fun showEmpty(emptyMessage: String) {
        binding.searchResult.text = emptyMessage
        binding.searchResult.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    companion object {
        const val VACANCY_ID = "vacancy_id"

        fun createArgs(vacancyId: String): Bundle = bundleOf(VACANCY_ID to vacancyId)
    }
}