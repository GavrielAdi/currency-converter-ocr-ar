package com.adios.conversee.currencies.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.adios.conversee.currencies.data.models.Rates

@Dao
interface RatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRates(rates: Rates)

    @Update
    fun updateRates(rates: Rates)

    @Delete
    fun deleteRates(rates: Rates)

    @Query("SELECT * FROM Rates ORDER BY date DESC LIMIT 1")
    fun getLatestRates(): Flow<Rates>
}
