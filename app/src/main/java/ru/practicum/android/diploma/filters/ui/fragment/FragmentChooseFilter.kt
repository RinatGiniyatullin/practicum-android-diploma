package ru.practicum.android.diploma.filters.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSelectionBinding
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.ScreenState
import ru.practicum.android.diploma.filters.ui.adapter.FilterSelectionClickListener
import ru.practicum.android.diploma.filters.ui.adapter.FiltersAdapter
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.SCREEN
import ru.practicum.android.diploma.util.BindingFragment

class FragmentChooseFilter:BindingFragment<FragmentFilterSelectionBinding>() {



    private val viewModel by viewModel<FiltersViewModel>()
    private var adapter:FiltersAdapter? = null
    private var screen:String? =null
    val PermKrai = Country(id = "1", name = "Россия", url = "2")
    val Samara = Country(id = "1", name = "Украина", url = "3")
    val industriesList:MutableList<Country> = mutableListOf(PermKrai, Samara
    )
    val countries = listOf<Country>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterSelectionBinding {
        return FragmentFilterSelectionBinding.inflate(inflater, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getScreenStateLiveData().observe(requireActivity()){chooseScreen(it)}
        screen = arguments?.getString(SCREEN)
        viewModel.setScreen(screen!!)
        initAdapter()
        back()
        binding.recyclerViewFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFilters.adapter = adapter

    }
    private fun initAdapter(){
        adapter = FiltersAdapter(object: FilterSelectionClickListener {
            override fun onClickRegion(model: Areas?) {
            }
            override fun onClickIndustries(model: Industries?) {
            }
            override fun onClickCountry(model: Country?) {
            }
        })
    }
    private fun back(){
        binding.arrowback.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun chooseScreen(state:ScreenState){
        when(state){
            is ScreenState.showIndustriesScreen -> showIndustriesScreen()
            is ScreenState.showAreasScreen -> showAreasScreen(state.areasList)
            is ScreenState.showCountriesScreen -> {
                showCountriesScreen(state.countriesList)
            }

        }
    }
    private fun showCountriesScreen(countriesList:List<Country>){
        adapter?.setCountry(countriesList)
        binding.searchEditText.visibility = View.GONE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_country)
    }
    private fun showIndustriesScreen(){
    }
    private fun showAreasScreen(areas:List<Areas>){
        adapter?.setRegion(areas)
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_region)
    }


}