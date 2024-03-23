package iut.dam.projetdevmobilefinal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> notificationMessages;

    private User user;

    private ArrayList <Integer> mesId;

    private CountDownLatch latch;

    private boolean setCoin=false;
    private boolean deleteAppliance_time_slot=false;


    public NotificationsAdapter(@NonNull Context context, @NonNull ArrayList<String> notificationMessages, @NonNull User user, @NonNull ArrayList <Integer> mesId) {
        super(context, 0, notificationMessages);
        this.context = context;
        this.notificationMessages = notificationMessages;
        this.user = user;
        this.mesId = mesId;
        int i = 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.item_notifications, parent, false);
        }

        String currentMessage = notificationMessages.get(position);
        latch = new CountDownLatch(2);
        TextView residentTV = listItemView.findViewById(R.id.residentTV);
        residentTV.setText(currentMessage);
        Button ouiButton = listItemView.findViewById(R.id.Oui);
        Button nonButton = listItemView.findViewById(R.id.Non);

        ouiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCoin(user.eco_coin+1);
                deleteAppliance_time_slot(position);

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(setCoin && deleteAppliance_time_slot){
                    user.setEco_coin(1);
                    //eco_coin.setText(user.getEco_coin()+"");
                    notificationMessages.remove(position);
                    // Mettre à jour la vue
                    notifyDataSetChanged();
                    mesId.remove(position);
                }
                else{
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Échec, veuillez réessayer. Si le problème persiste, redémarrez l'application et vérifiez votre connexion internet", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

        nonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCoin(user.eco_coin-1);
                user.setEco_coin(-1);
                notificationMessages.remove(position);
                notifyDataSetChanged();
            }
        });

        return listItemView;
    }

    private void setCoin(int coin) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.1.13/monappli/setCoin.php?user_id=" + user.getId() + "&new_eco_coin=" + coin;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    setCoin = true;
                    latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                latch.countDown();
            }
        });
    }


    private void deleteAppliance_time_slot(int position) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.1.13/monappli/deleteAppliances.php?user_id=" + user.getId() + "&time_slot_id=" + mesId.get(position);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                deleteAppliance_time_slot = true;
                latch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                latch.countDown();
            }
        });
    }

}
