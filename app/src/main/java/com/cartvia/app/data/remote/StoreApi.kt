package com.cartvia.app.data.remote

import com.cartvia.app.data.model.StoreResponse
import com.cartvia.app.data.model.StoreTrafficResponse
import com.cartvia.app.data.model.ZoneHeatmapDto
import com.cartvia.app.data.model.StoreOccupancyDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreApi {
    @GET("/api/stores")
    suspend fun getStores(): ApiEnvelope<List<StoreResponse>>

    @GET("/api/stores/{storeId}/traffic")
    suspend fun getStoreTraffic(
        @Path("storeId") storeId: String
    ): ApiEnvelope<StoreTrafficResponse>

    @GET("/api/heatmap/stores/{storeId}/zones")
    suspend fun getStoreHeatmapZones(
        @Path("storeId") storeId: String
    ): ApiEnvelope<List<ZoneHeatmapDto>>

    @GET("/api/heatmap/stores/ranking")
    suspend fun getStoreOccupancyRanking(): ApiEnvelope<List<StoreOccupancyDto>>
}
