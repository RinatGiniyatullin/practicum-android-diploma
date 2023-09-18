package ru.practicum.android.diploma.details.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.details.presentation.VacancyState
import ru.practicum.android.diploma.details.presentation.VacancyViewModel
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.BindingFragment

class VacancyFragment : BindingFragment<FragmentVacancyBinding>() {

    private val viewModel by viewModel<VacancyViewModel>()
    private var checkFavourite: Boolean = false

    private lateinit var vacancy: Vacancy
    private lateinit var vacancyDetails: VacancyDetails
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentVacancyBinding {
        return FragmentVacancyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBasicVacancyInfo()
        setConfirmDialog()

        viewModel.loadVacancyDetails(vacancy.id)

        viewModel.checkFavourite(vacancy)

        viewModel.observeStateFavouriteIcon().observe(viewLifecycleOwner) {
            renderStateFavouriteIcon(it)
        }

        viewModel.stateVacancyInfoDb.observe(viewLifecycleOwner) { vacancyDetailsDb ->
            if (vacancyDetailsDb == null){
                binding.favouritesIcon.isClickable = false
                return@observe
            }
            vacancyDetails = vacancyDetailsDb
            binding.detailsData.visibility = View.VISIBLE
            binding.similarVacanciesButton.visibility = View.GONE
            initVacancyDetails()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VacancyState.Content -> {
                    binding.progressBarForLoad.visibility = View.GONE
                    vacancyDetails = state.vacancyDetails
                    binding.detailsData.visibility = View.VISIBLE
                    binding.refreshButton.visibility = View.GONE
                    initVacancyDetails()
                }
                is VacancyState.Error -> {
                    binding.progressBarForLoad.visibility = View.GONE
                    showToast(state.errorMessage)
                    binding.detailsData.visibility = View.GONE
                    binding.refreshButton.visibility = View.VISIBLE
                    viewModel.initVacancyDetailsInDb(vacancy)
                }

                VacancyState.Loading -> binding.progressBarForLoad.visibility = View.VISIBLE
            }
        }

        initClickListeners()
    }

    private fun initBasicVacancyInfo(){
        val jsonVacancy = requireArguments().getString(VACANCY)
        vacancy = Gson().fromJson(jsonVacancy, Vacancy::class.java)
        binding.vacancyName.text = vacancy.name
        initSalary()
        binding.employerName.width = (resources.displayMetrics.widthPixels*0.7).toInt()
        binding.city.width = (resources.displayMetrics.widthPixels*0.7).toInt()
        binding.employerName.text = vacancy.employerName
        binding.city.text = vacancy.city
    }

    private fun setConfirmDialog(){
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireActivity().getString(R.string.write_to_email))
            .setNegativeButton(requireActivity().getString(R.string.no)) { _, _ ->

            }.setPositiveButton(requireActivity().getString(R.string.yes)) { _, _ ->
                viewModel.shareEmail(vacancyDetails.contacts?.email!!)
            }
    }

    private fun initSalary(){
        var salaryText = ""
        if(vacancy.salaryFrom != null) salaryText = "от ${vacancy.salaryFrom} "
        if(vacancy.salaryTo != null) salaryText += "до ${vacancy.salaryTo}"
        if(vacancy.salaryCurrency != null) salaryText += " ${vacancy.salaryCurrency}"
        if(vacancy.salaryFrom == null && vacancy.salaryTo == null && vacancy.salaryCurrency == null) salaryText = getString(R.string.no_data)
        binding.salary.text = salaryText
    }

    private fun initVacancyDetails(){
        if (vacancyDetails == null) return
        val radius = resources.getDimensionPixelSize(R.dimen.margin_12)
        Glide.with(requireContext())
            .load(vacancyDetails.employer?.logoUrls?.original)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(binding.employerImage)

        val experience = vacancyDetails.experience?.name
        val schedule = vacancyDetails.schedule?.name
        val keySkills = vacancyDetails.keySkills
        val contacts = vacancyDetails.contacts
        val nameContact = vacancyDetails.contacts?.name
        val emailContact = vacancyDetails.contacts?.email
        val phoneContactList = vacancyDetails.contacts?.phones
        val firstPhoneContact = phoneContactList?.get(0)
        val phoneComment = firstPhoneContact?.comment
        val formattedPhoneContact = "+${firstPhoneContact?.country}(${firstPhoneContact?.city})${firstPhoneContact?.number?.dropLast(4)}-${firstPhoneContact?.number?.drop(3)?.dropLast(2)}-${firstPhoneContact?.number?.drop(5)}"
        val noData = getString(R.string.no_data)

        binding.city.text = if(vacancy.city.isEmpty()) vacancyDetails.area.name else vacancy.city
        binding.requiredExperienceValue.text = if(experience != null) experience else noData
        binding.scheduleValue.text = if(schedule != null) schedule else noData
        binding.vacancyDescriptionValue.text = HtmlCompat.fromHtml(vacancyDetails.description, FROM_HTML_MODE_COMPACT)

        if(!keySkills.isNullOrEmpty()) {
            binding.keySkillsContainer.visibility = View.VISIBLE
            binding.vacancyKeySkillsValue.text = keySkills.joinToString { it.name }
        } else
            binding.keySkillsContainer.visibility = View.GONE

        if(contacts != null) {
            binding.contactsContainer.visibility = View.VISIBLE
            binding.vacancyContactPersonValue.text = if(nameContact != null) nameContact else noData
            binding.vacancyContactEmailValue.text = if(emailContact != null) emailContact else noData
            binding.vacancyContactPhoneValue.text = if(phoneContactList != null) formattedPhoneContact else noData
            binding.vacancyPhoneCommentValue.text = if(phoneComment != null) phoneComment else noData
        }
        else
            binding.contactsContainer.visibility = View.GONE
    }

    private fun showToast(message: String){
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_LONG)
            .show()
    }

    private fun renderStateFavouriteIcon(isFavourite: Boolean?){
        when (isFavourite) {
            true -> {
                binding.favouritesIcon.setImageResource(R.drawable.favorites_on)
                checkFavourite = true
            }

            else -> {
                binding.favouritesIcon.setImageResource(R.drawable.favorites_off)
                checkFavourite = false
            }
        }
    }

    private fun initClickListeners(){
        binding.refreshButton.setOnClickListener{
            viewModel.loadVacancyDetails(vacancy.id)
        }

        binding.similarVacanciesButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_vacancyFragment_to_similarVacancyFragment,
                SimilarVacancyFragment.createArgs(vacancy.id)
            )
        }

        binding.backIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.favouritesIcon.setOnClickListener{

            viewModel.clickOnFavoriteIcon(vacancy, vacancyDetails)
        }

        binding.sharingIcon.setOnClickListener {
            viewModel.shareVacancyUrl(vacancyDetails.alternateUrl)
        }

        binding.vacancyContactPhoneValue.setOnClickListener {
            if(vacancyDetails.contacts?.phones != null)
                viewModel.sharePhone(binding.vacancyContactPhoneValue.text.toString())
        }

        binding.vacancyContactEmailValue.setOnClickListener {
            if(vacancyDetails.contacts?.email != null)
                confirmDialog.show()
        }
    }

    companion object {
        const val VACANCY = "vacancy"

        fun createArgs(jsonVacancy: String): Bundle = bundleOf(VACANCY to jsonVacancy)
    }
}