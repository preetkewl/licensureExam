package com.crcexam.android.network;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;


public interface RetrofitService {
    @POST("signup")
    Call<ResponseBody> signUp(@Body JsonObject jsonObject);

    /*@FormUrlEncoded
    @POST("api/v1/accounts/register/")
    Call<ResponseBody> register(@Field("username") String userName,
                                @Field("password") String password,
                                @Field("password_confirm") String passwordConfirm,
                                @Field("first_name") String firstName,
                                @Field("last_name") String lastName,
                                @Field("g-recaptcha-response") String capcha);*/

    @POST("api/v1/accounts/change-password/")
    Call<ResponseBody> changePassword(@Body JsonObject jsonObject);

    @POST("Mobile/Login")
    Call<ResponseBody> signin(@Body JsonObject jsonObject);

    @POST("Mobile/Login")
    Call<ResponseBody> login(@Header("apikey") String apikey, @Header("siteid") String siteid, @Header("username") String username, @Header("password") String password);

    @POST("Mobile/Register")
    Call<ResponseBody> register(@Header("apikey") String apikey, @Header("siteid") String siteid, @Header("username") String username, @Header("password") String password);

    @POST("reset_password")
    Call<ResponseBody> resetPassword(@Body JsonObject jsonObject);

    @POST("accounts/profile/")
    Call<ResponseBody> profileCreate(@Body JsonObject jsonObject);

    @PUT("accounts/profile/")
    Call<ResponseBody> profileUpdate(@Body JsonObject jsonObject);

    @GET("Mobile/Profile")
    Call<ResponseBody> getProfile(@Header("apikey") String apikey, @Header("siteid") String siteid, @Header("authtoken") String authtoken);

    @FormUrlEncoded
    @POST("Mobile/Profile")
    Call<ResponseBody> updateProfile(@Header("apikey") String apikey, @Header("siteid") String siteid, @Header("authtoken") String authtoken,
                                     @Field("Body content type") String title, @Field("account") JsonObject account);

    @POST("Mobile/Profile")
    Call<ResponseBody> updatePassword(@Header("apikey") String apikey, @Header("siteid") String siteid,
                                      @Header("authtoken") String authtoken, @Header("oldpassword") String oldpassword,@Header("newpassword") String newpassword);


    @POST("Mobile/History")
    Call<ResponseBody> saveHistory(@Header("apikey") String apikey, @Header("siteid") String siteid, @Header("authtoken") String authtoken, @Header("mchistory") JsonObject obj);


    @GET("IAPService?")
    Call<ResponseBody> getExpDetailById(@Query("id") int id);


    @GET("IAPService?siteid=6")
    Call<ResponseBody> getAllExamList();


}
