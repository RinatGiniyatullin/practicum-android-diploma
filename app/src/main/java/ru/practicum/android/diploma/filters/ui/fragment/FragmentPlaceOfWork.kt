package ru.practicum.android.diploma.filters.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentPlaceOfWorkBinding
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.REGION
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.SCREEN
import ru.practicum.android.diploma.util.BindingFragment

class FragmentPlaceOfWork:BindingFragment<FragmentPlaceOfWorkBinding>() {
    private var bundle:Bundle? = null
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaceOfWorkBinding {
        return FragmentPlaceOfWorkBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseCountryBottom.setOnClickListener {
            bundle = bundleOf(FragmentSettingFilters.SCREEN to FragmentSettingFilters.COUNTRIES)
            findNavController().navigate(R.id.action_fragmentPlaceOfWork_to_fragmentChooseFilter, bundle )
        }
        binding.regionButton.setOnClickListener {
            bundle = bundleOf(SCREEN to REGION )
            findNavController().navigate(R.id.action_fragmentPlaceOfWork_to_fragmentChooseFilter, bundle)
        }
    }
}