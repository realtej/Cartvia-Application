package com.cartvia.app.data.remote

import com.cartvia.app.data.model.PaginatedResponse
import com.cartvia.app.data.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("/api/products")
    suspend fun getProducts(
        @Query("storeId") storeId: String?,
        @Query("search") search: String?,
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiEnvelope<PaginatedResponse<ProductResponse>>

    @GET("/api/products/{id}")
    suspend fun getProduct(
        @Path("id") id: String,
        @Query("storeId") storeId: String?
    ): ApiEnvelope<ProductResponse>

    @GET("/api/products/barcode/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("storeId") storeId: String?
    ): ApiEnvelope<ProductResponse>
}
