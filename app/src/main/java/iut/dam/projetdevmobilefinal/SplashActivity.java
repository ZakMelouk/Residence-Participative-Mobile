package iut.dam.projetdevmobilefinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000; // Délai en millisecondes (ici, 2000 ms ou 2 secondes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Trouvez l'image et le texte dans le layout
        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView textViewWelcome = findViewById(R.id.textViewWelcome);

        // Animation fade in pour l'image et le texte
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageViewLogo.startAnimation(fadeInAnimation);
        textViewWelcome.startAnimation(fadeInAnimation);

        // Utilisation d'un Handler pour passer à l'activité principale après un délai
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish(); // Ferme la Splash Activity après le passage à l'activité principale
            }
        }, SPLASH_TIME_OUT);
    }
}