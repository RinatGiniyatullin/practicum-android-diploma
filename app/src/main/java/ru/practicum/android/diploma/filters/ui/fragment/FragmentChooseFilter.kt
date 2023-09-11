package ru.practicum.android.diploma.filters.ui.fragment
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import ru.practicum.android.diploma.filters.presentation.models.ShowViewState
import ru.practicum.android.diploma.filters.ui.adapter.FilterSelectionClickListener
import ru.practicum.android.diploma.filters.ui.adapter.FiltersAdapter
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.SCREEN
import ru.practicum.android.diploma.util.BindingFragment

class FragmentChooseFilter:BindingFragment<FragmentFilterSelectionBinding>() {

    private val viewModel by viewModel<FiltersViewModel>()
    private var adapter:FiltersAdapter? = null
    private var screen:String? =null
    private val areaList = mutableListOf<Region>()
    private val industryList = mutableListOf<Industries>()
    private var isRegionScreen:Boolean = false

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterSelectionBinding {
        return FragmentFilterSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getScreenStateLiveData().observe(requireActivity()){chooseScreen(it)}
        viewModel.getShowViewStateLiveData().observe(requireActivity()){showView(it)}
        screen = arguments?.getString(SCREEN)
        viewModel.setScreen(screen!!)
        initAdapter()
        back()
        binding.recyclerViewFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFilters.adapter = adapter
        applyButtom()
        listeners()

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
            override fun onClickIndustries(model: Industries?, isChecked:Boolean) {
                when(isChecked){
                    true -> industryList.add(model!!)
                    false -> industryList.remove(model)
                }
                binding.buttonApply.visibility = View.GONE
                industryList.takeIf { it.isNotEmpty()}?.let{ binding.buttonApply.visibility = View.VISIBLE}

            }
            override fun onClickCountry(model: Areas?) {
                viewModel.addCountry(model!!)
                findNavController().navigateUp()

            }

        })
    }
    private fun listeners(){
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)hideKeyBoard()
            viewModel.setOnFocus(binding.searchEditText.text.toString(), hasFocus)

        }
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setOnFocus(s.toString(), binding.searchEditText.hasFocus())
                when(isRegionScreen){
                    true -> viewModel.searchRegion(s.toString())
                    else -> viewModel.searchIndustry(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.editTextCloseImage.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.searchEditText.clearFocus()
            hideKeyBoard()
        }
    }
    private fun applyButtom(){
        binding.buttonApply.setOnClickListener {
            areaList.takeIf { it.isNotEmpty() }?.let {
                viewModel.addArea(it)
            }
            industryList.takeIf { it.isNotEmpty() }?.let {
                viewModel.addIndustries(it)
            }
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
            is ScreenState.showIndustriesScreen -> showIndustriesScreen(state.industryList)
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
        if(countriesList.isEmpty()){binding.placeholderImage.visibility = View.VISIBLE
        }else{
            binding.placeholderImage.visibility = View.GONE
        }

    }
    private fun showIndustriesScreen(industryList:List<Industries>){
        isRegionScreen = false
        adapter?.setIndustrie(industryList)
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_industry)
        binding.searchEditText.setHint(requireActivity().getText(R.string.choose_of_industry))
        binding.searchEditText.visibility = View.VISIBLE
        if(industryList.isEmpty()){binding.placeholderImage.visibility = View.VISIBLE
        }else{
            binding.placeholderImage.visibility = View.GONE
        }

    }
    private fun showAreasScreen(areas:List<Region>){
        isRegionScreen = true
        adapter?.setRegion(areas)
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_region)
        binding.searchEditText.setHint(requireActivity().getText(R.string.choose_of_region))
        binding.searchEditText.visibility = View.VISIBLE
        if(areas.isEmpty()){binding.placeholderImage.visibility = View.VISIBLE
        }else{
            binding.placeholderImage.visibility = View.GONE
        }

    }
    private fun hideKeyBoard() {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
    }
    fun showView(state: ShowViewState){
        when(state){
            is ShowViewState.showClearIcon -> showClearIcon()
            is ShowViewState.hideClearIcon -> hideClearIcon()
        }
    }
    fun showClearIcon(){
        binding.editTextSearchImage.visibility = View.GONE
        binding.editTextCloseImage.visibility = View.VISIBLE
    }
    fun hideClearIcon(){
        binding.editTextSearchImage.visibility = View.VISIBLE
        binding.editTextCloseImage.visibility = View.GONE

    }

}