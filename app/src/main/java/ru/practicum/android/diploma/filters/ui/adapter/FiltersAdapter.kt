package ru.practicum.android.diploma.filters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Region
import ru.practicum.android.diploma.filters.ui.viewholder.ViewHolder

class FiltersAdapter(val onClickListener:FilterSelectionClickListener):RecyclerView.Adapter<ViewHolder>() {

    var areasList = mutableListOf<Region>()
    var industriesList = mutableListOf<Industries>()
    var countryList = mutableListOf<Areas>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.filter_selection_itemview, parent,
                    false
                )
            )
    }
    override fun getItemCount(): Int {
        return areasList.size+industriesList.size+countryList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        areasList.takeIf { it.isNotEmpty()}?.let {
            holder.bindRegion(areasList.get(position), onClickListener)
        }
        industriesList.takeIf { it.isNotEmpty()}?.let {
            holder.bindIndustries(industriesList.get(position), onClickListener)
        }
        countryList.takeIf { it.isNotEmpty()}?.let {
            holder.bindCountry(countryList.get(position), onClickListener)
        }
    }
    fun setRegion(newAreasList:List<Region>){
        industriesList.clear()
        countryList.clear()
        areasList.addAll(newAreasList)
        notifyDataSetChanged()
    }
    fun setIndustrie(newIndustriesList:List<Industries>){
        areasList.clear()
        countryList.clear()
        industriesList.addAll(newIndustriesList)
        notifyDataSetChanged()
    }
    fun setCountry(newCountryList:List<Areas>){
        areasList.clear()
        industriesList.clear()
        countryList.addAll(newCountryList)
        notifyDataSetChanged()
    }
}
 interface FilterSelectionClickListener {
    fun onClickRegion(model:Region?, isChecked:Boolean)
    fun onClickIndustries(model:Industries?)
    fun onClickCountry(model:Areas?)
}