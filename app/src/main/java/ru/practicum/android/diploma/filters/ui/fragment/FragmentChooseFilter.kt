package ru.practicum.android.diploma.filters.ui.fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSelectionBinding
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Region
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
    val areaList = mutableListOf<Region>()

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
        addRegion()
    }

    private fun initAdapter(){
        adapter = FiltersAdapter(object: FilterSelectionClickListener {
            override fun onClickRegion(model: Region?, isChecked: Boolean) {
                when(isChecked){
                    true -> areaList.add(model!!)
                    false -> areaList.remove(model)
                }
                binding.buttonApply.visibility = View.GONE
                areaList.takeIf { it.isNotEmpty()}?.let{ binding.buttonApply.visibility = View.VISIBLE}
                Log.d("Area", "$areaList")
            }
            override fun onClickIndustries(model: Industries?) {
            }
            override fun onClickCountry(model: Areas?) {
                viewModel.addCountry(model!!)
                findNavController().navigateUp()

            }
        })
    }
    private fun addRegion(){
        binding.buttonApply.setOnClickListener {
            viewModel.addArea(areaList)
            findNavController().navigateUp()
        }
    }
    private fun back(){
        binding.arrowback.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun chooseScreen(state:ScreenState){
        when(state){
            is ScreenState.showIndustriesScreen -> showIndustriesScreen(state.industriesList)
            is ScreenState.showAreasScreen -> showAreasScreen(state.areasList)
            is ScreenState.showCountriesScreen -> {
                showCountriesScreen(state.countriesList)
            }

        }
    }
    private fun showCountriesScreen(countriesList:List<Areas>){
        adapter?.setCountry(countriesList)
        binding.searchEditText.visibility = View.GONE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_country)
    }
    private fun showIndustriesScreen(industriesList:List<Industries>){
        adapter?.setIndustrie(industriesList)
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = "Выбор отрасли"
    }
    private fun showAreasScreen(areas:List<Region>){

        adapter?.setRegion(areas)
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_region)
    }
}