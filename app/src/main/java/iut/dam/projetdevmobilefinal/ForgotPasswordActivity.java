package iut.dam.projetdevmobilefinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText confirmerMDP;
    EditText MDP;
    Button ButtonPassword;

    Button buttonChangerMDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButtonPassword = findViewById(R.id.buttonForgotPassword);
        Button buttonCreateAccount = findViewById(R.id.Connexion);
        EditText email = findViewById(R.id.editTextEmail);
        buttonChangerMDP = findViewById(R.id.ChangerMDP);
        confirmerMDP = findViewById(R.id.editTextConfirmerNouveauMotDePasse);
        MDP = findViewById(R.id.editTextNouveauMotDePasse);
        confirmerMDP.setVisibility(View.GONE);
        MDP.setVisibility(View.GONE);
        buttonChangerMDP.setVisibility(View.GONE);
        buttonCreateAccount.setBackgroundColor(Color.parseColor("#FFD700"));
        ButtonPassword.setBackgroundColor(Color.parseColor("#FFD700"));
        ButtonPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String monemail = email.getText().toString();
                if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(monemail).matches())) {
                    Toast.makeText(getApplicationContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
                    return;
                }

                String urlString = "http://192.168.1.13/monappli/searchEmail.php?email=" + monemail;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlString)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                String message = jsonObject.getString("message");
                                if (message.equals("Email found in database")) {
                                    // L'e-mail a été trouvé dans la base de données, vous pouvez effectuer des actions ici
                                    modifierMotDePasse(email.getText().toString());
                                } else {
                                    emailIntrouvable();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            emailIntrouvable();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Toast.makeText(getApplicationContext(), R.string.echecServeur, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void emailIntrouvable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.emailIntrouvable, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void modifierMotDePasse(String email) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ButtonPassword.setVisibility(View.GONE);
                confirmerMDP.setVisibility(View.VISIBLE);
                MDP.setVisibility(View.VISIBLE);
                buttonChangerMDP.setVisibility(View.VISIBLE);
                buttonChangerMDP.setBackgroundColor(Color.parseColor("#FFD700"));
                buttonChangerMDP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mdp = MDP.getText().toString();
                        String confirmerleMDP = confirmerMDP.getText().toString();
                        if (mdp.length() < 8 || !mdp.matches(".*[A-Z].*") || !mdp.matches(".*\\d.*")) {
                            Toast.makeText(getApplicationContext(), R.string.invalid_password_format, Toast.LENGTH_SHORT).show();
                        }
                        else if(!(mdp.equals(confirmerleMDP)))
                            Toast.makeText(getApplicationContext(), "Les mots de passes doivent être identiques", Toast.LENGTH_SHORT).show();

                        else{
                            String urlString = "http://192.168.1.13/monappli/updatePassword.php?email=" + email + "&password=" + mdp;

                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(urlString)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        try {
                                            JSONObject jsonObject = new JSONObject(responseBody);
                                            String message = jsonObject.getString("message");
                                            if (message.equals("Password updated successfully")) {
                                                // Le mot de passe a été mis à jour avec succès
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Mot de passe changé avec succès. Veuillez vous rendre sur la page de connexion.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                // La mise à jour du mot de passe a échoué
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour du mot de passe", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        // Gérer le cas où la réponse n'est pas réussie
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Échec de la mise à jour du mot de passe : Erreur réseau", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    // Gérer l'échec de la requête HTTP
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Échec de la mise à jour du mot de passe : Erreur réseau", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    }
                });
            }
        });


    }
}
