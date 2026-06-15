package com.example.buildlogai;

import com.example.buildlogai.model.AIRequest;
import com.example.buildlogai.model.AIResponse;
import com.example.buildlogai.model.AuthResponse;
import com.example.buildlogai.model.ImageResponse;
import com.example.buildlogai.model.Project;
import com.example.buildlogai.model.ParseRequest;
import com.example.buildlogai.model.ParseResponse;
import com.example.buildlogai.model.ProjectRequest;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.RecordImageDTO;
import com.example.buildlogai.model.ReportRequestDTO;
import com.example.buildlogai.model.ReportResponseDTO;
import com.example.buildlogai.model.UserRequest;
import com.example.buildlogai.model.UserResponse;
import com.example.buildlogai.model.VerifyRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("records")
    Call<List<RecordDTO>> getRecords();

    @GET("api/projects")
    Call<List<Project>> getProjects();

    @GET("records/project/{projectId}")
    Call<List<RecordDTO>> getRecordsByProject(@Path("projectId") Long projectId);

    @POST("api/projects")
    Call<Project> createProject(@Body Project project);

    @POST("records")
    Call<RecordDTO> saveRecord(@Body RecordDTO record);

    @PUT("records/{id}")
    Call<RecordDTO> updateRecord(
            @Path("id") Long id,
            @Body RecordDTO record
    );

    @POST("ai/parse")
    Call<AIResponse> parseAI(@Body AIRequest request);

    @POST("records/parse")
    Call<ParseResponse> parseText(@Body ParseRequest request);

    @POST("/api/users/register")
    Call<UserResponse> register(@Body UserRequest request);

    @POST("/api/users/login")
    Call<AuthResponse> login(@Body UserRequest request);

    @POST("reports/generate")
    Call<ReportResponseDTO> generateReport(
            @Body ReportRequestDTO request
    );

    @POST("reports/generate-pdf")
    Call<ResponseBody> generatePdf(
            @Body ReportRequestDTO request
    );

    @GET("records/{id}")
    Call<RecordDTO> getRecordById(
            @Path("id") Long id
    );

    @PATCH("records/{id}/status")
    Call<RecordDTO> updateRecordStatus(
            @Path("id") Long id,
            @Body Map<String, String> body
    );

    @POST("/api/projects/{projectId}/users/{userId}")
    Call<Void> addUserToProject(
            @Path("projectId") Long projectId,
            @Path("userId") Long userId
    );

    @GET("/api/users/email")
    Call<UserResponse> getUserByEmail(
            @Query("email") String email
    );


    @GET("records/{id}/images")
    Call<List<RecordImageDTO>> getImages(
            @Path("id") Long recordId
    );

    @DELETE("records/images/{imageId}")
    Call<Void> deleteImage(
            @Path("imageId") Long imageId
    );

    @GET("api/projects/{id}/users")
    Call<List<UserResponse>> getProjectUsers(
            @Path("id") Long projectId
    );

    @PUT("api/projects/{id}")
    Call<Void> updateProject(
            @Path("id") Long projectId,
            @Body ProjectRequest request
    );

    @DELETE("api/projects/{id}")
    Call<Void> deleteProject(
            @Path("id") Long projectId
    );

    @Multipart
    @POST("records/{id}/images")
    Call<ImageResponse> uploadImage(
            @Path("id") Long recordId,
            @Part MultipartBody.Part image
    );

    @POST("/api/users/verify")
    Call<String> verifyEmail(
            @Body VerifyRequest request
    );

    @POST("api/users/resend-verification")
    Call<String> resendVerification(
            @Query("email") String email
    );

    @DELETE("api/projects/{projectId}/users/{userId}")
    Call<Void> removeUserFromProject(
            @Path("projectId") Long projectId,
            @Path("userId") Long userId
    );

    @DELETE("records/{id}")
    Call<Void> deleteRecord(
            @Path("id") Long recordId
    );
}
