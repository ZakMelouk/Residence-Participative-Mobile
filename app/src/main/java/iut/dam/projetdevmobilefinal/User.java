package iut.dam.projetdevmobilefinal;

import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class User implements Serializable {
    int id;
    String firstname;
    String lastname;

    String email;
    String token;
    String expired_at;
    int habitat_id;
    int eco_coin;


    public static User getFromJson(String json){
        Gson gson = new Gson();
        User obj = gson.fromJson(json, User.class);
        return obj;
    }
    public static List<User> getListFromJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>(){}.getType();
        List<User> list = gson.fromJson(json, type);
        return list;
    }

    public int getId(){
        return id;
    }
    public String getFirstname(){
        return firstname;
    }

    public String getLastname(){
        return lastname;
    }

    public int getHabitat_id(){
        return habitat_id;
    }

    public int getEco_coin(){
        return eco_coin;
    }

    public void setEco_coin(int coin){
        this.eco_coin += coin;
    }

}
