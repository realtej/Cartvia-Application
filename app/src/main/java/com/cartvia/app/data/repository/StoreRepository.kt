package com.cartvia.app.data.repository

import com.cartvia.app.data.model.StoreResponse
import com.cartvia.app.data.model.StoreTrafficResponse
import com.cartvia.app.data.model.ZoneHeatmapDto
import com.cartvia.app.data.model.StoreOccupancyDto
import com.cartvia.app.data.remote.StoreApi
import com.cartvia.app.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(
    private val storeApi: StoreApi
) : BaseRepository() {

    suspend fun getStores(): ApiResult<List<StoreResponse>> = safeApiCall {
        storeApi.getStores()
    }

    suspend fun getTraffic(storeId: String): ApiResult<StoreTrafficResponse> = safeApiCall {
        // TODO: backend endpoint pending — see api contract in comments in StoreApi
        storeApi.getStoreTraffic(storeId)
    }

    suspend fun getStoreHeatmapZones(storeId: String): ApiResult<List<ZoneHeatmapDto>> = safeApiCall {
        storeApi.getStoreHeatmapZones(storeId)
    }

    suspend fun getStoreOccupancyRanking(): ApiResult<List<StoreOccupancyDto>> = safeApiCall {
        storeApi.getStoreOccupancyRanking()
    }
}
