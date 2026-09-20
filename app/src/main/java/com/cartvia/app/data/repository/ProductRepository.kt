package com.cartvia.app.data.repository

import com.cartvia.app.data.model.PaginatedResponse
import com.cartvia.app.data.model.ProductResponse
import com.cartvia.app.data.remote.ProductApi
import com.cartvia.app.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productApi: ProductApi
) : BaseRepository() {

    suspend fun getProducts(
        storeId: String?,
        search: String?,
        category: String?,
        page: Int,
        size: Int
    ): ApiResult<PaginatedResponse<ProductResponse>> = safeApiCall {
        productApi.getProducts(storeId, search, category, page, size)
    }

    suspend fun getProduct(id: String, storeId: String?): ApiResult<ProductResponse> = safeApiCall {
        productApi.getProduct(id, storeId)
    }

    suspend fun getProductByBarcode(barcode: String, storeId: String?): ApiResult<ProductResponse> = safeApiCall {
        productApi.getProductByBarcode(barcode, storeId)
    }
}
