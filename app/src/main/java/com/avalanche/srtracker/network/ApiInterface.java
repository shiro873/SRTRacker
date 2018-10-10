package com.avalanche.srtracker.network;

import com.avalanche.srtracker.model.SrDestinationLocations;
import com.avalanche.srtracker.model.SrLocation;
import com.avalanche.srtracker.model.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/token")
    Call<Token> getToken(@Field("username") int userId, @Field("password") String password, @Field("grant_type") String grant_type);

    @POST("/api/LocationTracks")
    Call<SrLocation> postTrackLog(@Header("Authorization") String authHeader, @Header("HEADER") String token, @Body SrLocation location);

    @GET("api/DealerTravelLocation/{employeeId}/{date}")
    Call<List<SrDestinationLocations>> getLocations(@Path("employeeId") String employeeId, @Path("date") String date);

    @GET("api/LocationTrack/UpdateVisitStatus/{id}")
    Call updateLocationReached(@Path("id") String id);

    @POST("/api/LocationTracks")
    Call<SrLocation> postTrackLog(@Body SrLocation location);

}
