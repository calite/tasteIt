package com.example.tasteit_java.ApiUtils;

import android.content.Context;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.ApiService.UserApi;
import com.example.tasteit_java.ApiService.UserCommentApi;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.fragments.FragmentSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoader {
    private final ApiRequests apiRequests;
    private MutableLiveData<HashMap<String, Object>> userProfileLiveData;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<Boolean> isFollowLiveData;
    private MutableLiveData<List<Comment>> userCommentsLiveData;
    private MutableLiveData<List<User>> usersLiveData;
    private Context context;
    private String sender_token;
    private String receiver_token;
    private int  skipper;

    public UserLoader(ApiRequests apiRequests, Context context, String sender_token) {
        this.apiRequests = apiRequests;
        this.context = context;
        this.sender_token = sender_token;
        userProfileLiveData = new MutableLiveData<>();
    }

    public UserLoader(ApiRequests apiRequests, Context context, String sender_token, String receiver_token) {
        this.apiRequests = apiRequests;
        this.context = context;
        this.sender_token = sender_token;
        this.receiver_token = receiver_token;
        isFollowLiveData = new MutableLiveData<>();
    }

    public UserLoader(ApiRequests apiRequests, Context context, String sender_token, int skipper) {
        this.apiRequests = apiRequests;
        this.context = context;
        this.sender_token = sender_token;
        this.skipper = skipper;
        userCommentsLiveData = new MutableLiveData<>();
        usersLiveData = new MutableLiveData<>();
    }

    public LiveData<HashMap<String, Object>> getAllUser() {
        return userProfileLiveData;
    }

    public void loadAllUser() {
        HashMap<String, Object> temp = new HashMap<>();
        apiRequests.getCountRecipes(sender_token).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    Integer counter = response.body().get(0);
                    temp.put("recipes", counter);
                } else {
                    // La solicitud no fue exitosa
                }
            }
            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        apiRequests.getCountFollowers(sender_token).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    Integer counter = response.body().get(0);
                    temp.put("followers", counter);
                } else {
                    Toast.makeText(context, "Primer error", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        apiRequests.getCountFollowing(sender_token).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    Integer counter = response.body().get(0);
                    temp.put("following", counter);
                } else {
                    Toast.makeText(context, "Primer error", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        apiRequests.getCountRecipesLiked(sender_token).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    Integer counter = response.body().get(0);
                    temp.put("liked", counter);
                } else {
                    Toast.makeText(context, "Primer error", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }
            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        apiRequests.getUserByToken(sender_token).enqueue(new Callback<UserApi>() {
            @Override
            public void onResponse(Call<UserApi> call, Response<UserApi> response) {
                if (response.isSuccessful()) {
                    UserApi userApi = response.body();
                    User user = new User(
                            userApi.getUsername(),
                            userApi.getBiography(),
                            userApi.getImgProfile(),
                            userApi.getToken()
                    );
                    temp.put("user", user);
                    userProfileLiveData.postValue(temp);
                } else {
                    Toast.makeText(context, "Primer error", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<UserApi> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<Boolean> getIsFollow() {
        return isFollowLiveData;
    }

    public void loadIsFollow() {

        apiRequests.getIsFollowingUser(sender_token, receiver_token).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean bool = response.body();
                    isFollowLiveData.postValue(bool);
                } else {
                    Toast.makeText(context, "Primer error", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<Comment>> getUserComments() {
        return userCommentsLiveData;
    }

    public void loadUserComments() {
        apiRequests.getCommentsOnUser(sender_token, skipper).enqueue(new Callback<List<UserCommentApi>>() {
            @Override
            public void onResponse(Call<List<UserCommentApi>> call, Response<List<UserCommentApi>> response) {
                if (response.isSuccessful()) {
                    List<UserCommentApi> commentsApi = response.body();
                    List<Comment> comments = new ArrayList<>();

                    for (UserCommentApi temp : commentsApi) {
                        Comment comment = new Comment(
                                temp.getComment().getComment(),
                                temp.getComment().getDateCreated(),
                                new User(temp.getUser().getUsername(), temp.getUser().getBiography(), temp.getUser().getImgProfile(), temp.getUser().getToken())
                        );
                        comments.add(comment);
                    }

                    userCommentsLiveData.postValue(comments);
                } else {
                    Toast.makeText(context, "Primer error comentarios", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<UserCommentApi>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<User>> getFollowingByUser() {
        return usersLiveData;
    }

    public void loadFollowingByUser() {
        apiRequests.getFollowingUsers(sender_token, skipper).enqueue(new Callback<List<UserApi>>() {
            @Override
            public void onResponse(Call<List<UserApi>> call, Response<List<UserApi>> response) {
                if (response.isSuccessful()) {
                    List<UserApi> usersApi = response.body();
                    List<User> users = new ArrayList<>();

                    //tratamos los datos
                    for (UserApi userApi : usersApi) {
                        User user = new User(
                                userApi.getUsername(),
                                userApi.getBiography(),
                                userApi.getImgProfile(),
                                userApi.getToken()
                        );
                        users.add(user);
                    }
                    usersLiveData.postValue(users);
                } else {
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<UserApi>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<User>> getFollowersToUser() {
        return usersLiveData;
    }

    public void loadFollowersToUser() {
        apiRequests.getFollowersUsers(sender_token, skipper).enqueue(new Callback<List<UserApi>>() {
            @Override
            public void onResponse(Call<List<UserApi>> call, Response<List<UserApi>> response) {
                if (response.isSuccessful()) {
                    List<UserApi> usersApi = response.body();
                    List<User> users = new ArrayList<>();

                    //tratamos los datos
                    for (UserApi userApi : usersApi) {
                        User user = new User(
                                userApi.getUsername(),
                                userApi.getBiography(),
                                userApi.getImgProfile(),
                                userApi.getToken()
                        );
                        users.add(user);
                    }
                    usersLiveData.postValue(users);
                } else {
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<UserApi>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
