package com.adios.conversee.currencies.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import com.adios.conversee.currencies.data.models.Rates

interface ExchangeRatesAPI {
    @GET("currency-api@{date}/{apiVersion}/currencies/eur.min.json")
    suspend fun getLatestRates(
        /**
         *  Date formatted as YYYY-MM-DD or "latest"
         **/
        @Path("date") date: String = "latest",
        @Path("apiVersion") apiVersion: String = "v1"
    ): Rates
}
