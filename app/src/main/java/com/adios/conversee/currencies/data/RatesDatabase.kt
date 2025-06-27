package com.adios.conversee.currencies.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adios.conversee.currencies.data.models.Exchanges
import com.adios.conversee.currencies.data.models.Rates

@Database(
    entities = [Rates::class, Exchanges::class],
    version = 6
)
@TypeConverters(DateTypeConverter::class)
abstract class RatesDatabase : RoomDatabase() {
    abstract fun ratesDao(): RatesDao
}
