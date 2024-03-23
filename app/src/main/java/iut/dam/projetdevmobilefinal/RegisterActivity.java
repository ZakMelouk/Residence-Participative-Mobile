package iut.dam.projetdevmobilefinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText editTextFirstName = findViewById(R.id.editTextFirstName);
        EditText editTextLastName = findViewById(R.id.editTextLastName);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextFloor = findViewById(R.id.editTextFloor);
        EditText editTextHeight = findViewById(R.id.editTextHeight);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonConnexion = findViewById(R.id.Connexion);

        int buttonColor = Color.parseColor("#FFD700");
        buttonConnexion.setBackgroundColor(buttonColor);
        buttonRegister.setBackgroundColor(buttonColor);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérez les valeurs des champs
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String floor = editTextFloor.getText().toString();
                String height = editTextHeight.getText().toString();

                if(!(validateFields(firstName,lastName, email,password,floor,height)))
                    return;

                String urlString = "http://192.168.1.13/monappli/register.php?email="+email+"&password="+password+"&firstname="+firstName+"&lastname="+lastName+"&floor="+floor+"&area="+height;
                OkHttpClient client = new OkHttpClient();
                Toast.makeText(getApplicationContext(), urlString, Toast.LENGTH_SHORT).show();

                Request request = new Request.Builder()
                        .url(urlString)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        runOnUiThread(() -> {
                            if (responseBody.equals("\"User registered successfully!\"")) {
                                Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_SHORT).show();
                                Intent newIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(newIntent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.email_already_exits, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), R.string.registration_failure_message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private boolean validateFields(String firstName, String lastName, String email, String password, String floor, String height) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || floor.isEmpty() || height.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.empty_field, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
            Toast.makeText(getApplicationContext(), R.string.invalid_password_format, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int floorValue = Integer.parseInt(floor);
            if (floorValue < 0 || floorValue > 20) {
                Toast.makeText(getApplicationContext(), R.string.invalid_Floor, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), R.string.floor_must_be_number, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int heightValue = Integer.parseInt(height);
            if (heightValue < 1 || heightValue > 500) {
                Toast.makeText(getApplicationContext(), R.string.invalid_height, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), R.string.height_must_be_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}