package iut.dam.projetdevmobilefinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonPassword = findViewById(R.id.buttonForgotPassword);
        EditText emailET = findViewById(R.id.TextEmail);
        EditText passwordET = findViewById(R.id.TextPassword);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        Button buttonLoginSocial = findViewById(R.id.buttonLoginSocial);

        // Configuration des couleurs des boutons
        int buttonColor = Color.parseColor("#FFD700");
        buttonCreateAccount.setBackgroundColor(buttonColor);
        buttonPassword.setBackgroundColor(buttonColor);
        buttonLogin.setBackgroundColor(buttonColor);
        buttonLoginSocial.setBackgroundColor(buttonColor);
        buttonLoginSocial.setVisibility(View.GONE);
        buttonLoginSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        // Configuration des listeners
        buttonLogin.setOnClickListener(v -> Login(emailET.getText().toString(), passwordET.getText().toString()));

        buttonPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        buttonCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void Login(String email, String password) {
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            Toast.makeText(getApplicationContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le mot de passe est vide
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.empty_field, Toast.LENGTH_SHORT).show();
            return;
        }

        String urlString = "http://192.168.1.13/monappli/login.php?email=" + email + "&password=" + password;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String token = jsonObject.getString("token");
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                        Intent newIntent = new Intent(LoginActivity.this, EditProfileActivity.class);
                        newIntent.putExtra("TOKEN", token);
                        newIntent.putExtra("EMAIL", email);
                        startActivity(newIntent);
                        finish();
                    });
                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), R.string.incorrect_email_or_password, Toast.LENGTH_SHORT).show();
                    });
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.login_failure_message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
