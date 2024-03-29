package com.bignerdranch.android.criminalintent.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getCrime(id: UUID): Crime

    @Update
    suspend fun updateCrime(crime: Crime)

    @Insert
    suspend fun addCrime(crime: Crime)

    @Query("SELECT * FROM crime WHERE category=(:category)")
    fun getCrimesByCategory(category: String): Flow<List<Crime>>


    @Query("SELECT * FROM crime WHERE date=(:date)")
    fun getCrimesByDate(date: Date): Flow<List<Crime>>
}


