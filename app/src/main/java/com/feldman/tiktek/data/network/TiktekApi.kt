package com.feldman.tiktek.data.network


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


// Generic envelope for ASMX JSON: { "d": { Success, MessageCode, ResultData } }
@Serializable
data class TiktekEnvelope<T>(
    @SerialName("d") val d: TiktekResponse<T>
)


@Serializable
data class TiktekResponse<T>(
    @SerialName("Success") val success: Boolean,
    @SerialName("MessageCode") val messageCode: Int? = null,
    @SerialName("ResultData") val resultData: T? = null
)


// ---------- Requests ----------
@Serializable
data class GetBooksRequest(
    @SerialName("subjectID") val subjectID: String,
    @SerialName("schoolID") val schoolID: String? = null,
    @SerialName("locationID") val locationID: String? = null
)


@Serializable
data class GetSolutionsExRequest(
    @SerialName("bookID") val bookID: String,
    @SerialName("page") val page: String,
    @SerialName("question") val question: String,
    @SerialName("sq") val sq: String? = null,
    @SerialName("ssq") val ssq: String? = null,
    @SerialName("userID") val userID: String? = null
)


// ---------- Models ----------
@Parcelize
@Serializable
data class Book(
    @SerialName("ID") val id: String,
    @SerialName("Title") val title: String = "",
    @SerialName("Image") val image: String? = null,
    @SerialName("Subdir") val subdir: String? = null,
    @SerialName("BT1") val bt1: String? = null,
    @SerialName("BT2") val bt2: String? = null,
    @SerialName("BT3") val bt3: String? = null,
    @SerialName("HasSolutions") val hasSolutions: Boolean = false,
    @SerialName("Lang") val lang: String? = null,
    @SerialName("Status") val status: Int? = null
) : Parcelable


@Serializable
data class Solution(
    @SerialName("ID") val id: String,
    @SerialName("Image") val image: String,
    @SerialName("BookID") val bookId: String,
    @SerialName("Prefix") val prefix: String,
    @SerialName("Page") val page: Int? = null,
    @SerialName("Question") val question: Int? = null
)


interface TiktekApi {
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("il/services/SolutionSearch.asmx/GetBooks")
    suspend fun getBooks(@Body body: GetBooksRequest): TiktekEnvelope<List<Book>>


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("il/services/SolutionSearch.asmx/GetSolutionsEx")
    suspend fun getSolutionsEx(@Body body: GetSolutionsExRequest): TiktekEnvelope<List<Solution>>
}