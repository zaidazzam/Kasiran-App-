import com.bdi.kasiran.response.diskon.DiskonResponse
import com.bdi.kasiran.response.login.LoginResponse
import com.bdi.kasiran.response.logout.LogoutResponse
import com.bdi.kasiran.response.menu.MenuResponse
import com.bdi.kasiran.response.menu.MenuResponsePost
import com.bdi.kasiran.response.order.OrderCompleteResponse
import com.bdi.kasiran.response.order.OrderResponse
import com.bdi.kasiran.response.order.OrderStore
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpoint {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<LogoutResponse>


    @POST("menu")
    fun getMenuData(@Header("Authorization") token: String): Call<MenuResponse>

    @FormUrlEncoded
    @POST("menu")
    fun searchMenu(
        @Header("Authorization") token: String,
        @Field("s") key: String,
    ): Call<MenuResponse>

    @POST("menu/delete/{id}")
    fun deleteMenu(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<MenuResponsePost>

    @Multipart
    @POST("menu/store")
    fun tambahMenu(
        @Header("Authorization") token: String,
        @Part("menu_name") menuName: RequestBody,
        @Part("menu_price") menuPrice: RequestBody,
        @Part("menu_qty") menuStok: RequestBody,
        @Part("menu_type") menuType: RequestBody,
        @Part("menu_desc") menuDecs: RequestBody,
        @Part menuImage: MultipartBody.Part // Gunakan anotasi @Part untuk pengunggahan berkas
    ): Call<MenuResponsePost>

    @Multipart
    @POST("menu/update/{id}")
    fun editMenu(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("menu_name") menuName: RequestBody,
        @Part("menu_price") menuPrice: Int,
        @Part("menu_qty") menuStok: RequestBody,
        @Part("menu_type") menuType: RequestBody,
        @Part("menu_desc") menuDecs: RequestBody,
        @Part menuImage: MultipartBody.Part? // Gunakan anotasi @Part untuk pengunggahan berkas
    ): Call<MenuResponsePost>

    @POST("order")
    fun getOrder(@Header("Authorization") token: String): Call<OrderResponse>


    @POST("diskon")
    fun getDiskon(@Header("Authorization") token: String): Call<DiskonResponse>

    @Multipart
    @POST("menu/add-stock/{id}")
    fun updateMenuStock(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("menu_qty") menuStok: RequestBody,
    ): Call<MenuResponsePost>

    @FormUrlEncoded
    @POST("order/complete")
    fun getCompleteOrder(
        @Header("Authorization") token: String,
        @Field("id_order") id: String,
    ): Call<OrderCompleteResponse>

    @FormUrlEncoded
    @POST("order/cancel")
    fun cancelOrder(
        @Header("Authorization") token: String,
        @Field("id_order") id: String,
    ): Call<OrderCompleteResponse>

    @POST("order/store")
    fun storeOrder(
        @Header("Authorization") token: String,
        @Body order: OrderStore,
    ): Call<OrderCompleteResponse>
}
