package com.bignerdranch.android.criminalintent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>>
    get() = _crimes.asStateFlow()

    fun init1() {
        viewModelScope.launch {
            crimeRepository.getCrimes().collect {
                Log.d("TAG", "Fetch All")
                _crimes.value = it
                Log.d("TAG", "Fetched Data Size: ${it.size}")
            }
        }
    }

    fun collectCrimesByCategory(category : String) {
        Log.d("TAG", "Fetch Category: $category")
        viewModelScope.launch {
            crimeRepository.getCrimesByCategory(category).collect {
                _crimes.value = it
                Log.d("TAG", "Fetched Data Size: ${it.size}")
            }
        }
    }

    fun collectCrimesByDate(date: Date) {
        Log.d("TAG", "Fetch Category: $date")
        viewModelScope.launch {
            crimeRepository.getCrimesByDate(date).collect {
                _crimes.value = it
                Log.d("TAG", "Fetched Data Size: ${it.size}")
            }
        }
    }

    suspend fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}
