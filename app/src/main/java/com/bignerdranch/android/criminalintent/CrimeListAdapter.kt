package com.bignerdranch.android.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.databinding.ListItemCrimeBinding
import java.util.UUID

class CrimeHolder(
    private val binding: ListItemCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = crime.date.toString()
        binding.crimeCategoryText.text = crime.category
        binding.crimeAmount.text = crime.amount.toString()
        when(crime.category){
            "FOOD" -> binding.crimeCategory.setImageResource(R.drawable.fastfood_24)
            "ENTERTAINMENT" -> binding.crimeCategory.setImageResource(R.drawable.baseline_celebration_24)
            "HOUSING" -> binding.crimeCategory.setImageResource(R.drawable.baseline_house_24)
            "UTILITIES" -> binding.crimeCategory.setImageResource(R.drawable.utilities_services_24)
            "FUEL" -> binding.crimeCategory.setImageResource(R.drawable.baseline_local_gas_station_24)
            "AUTOMOTIVE" -> binding.crimeCategory.setImageResource(R.drawable.autocar_24)
            "MISC" -> binding.crimeCategory.setImageResource(R.drawable.othermisc_suggest_24)
        }

        binding.root.setOnClickListener {
            onCrimeClicked(crime.id)
        }
    }
}

class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (crimeId: UUID) -> Unit
) : RecyclerView.Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.bind(crime, onCrimeClicked)
    }

    override fun getItemCount() = crimes.size
}
