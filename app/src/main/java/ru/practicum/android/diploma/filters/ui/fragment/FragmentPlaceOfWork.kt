package ru.practicum.android.diploma.filters.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.practicum.android.diploma.databinding.FragmentPlaceOfWorkBinding
import ru.practicum.android.diploma.util.BindingFragment

class FragmentPlaceOfWork:BindingFragment<FragmentPlaceOfWorkBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaceOfWorkBinding {
        return FragmentPlaceOfWorkBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}