package abhishekwl.github.io.radar.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import abhishekwl.github.io.radar.Activities.QueryHashtagActivity;
import abhishekwl.github.io.radar.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertService extends Service {

    private OkHttpClient okHttpClient;
    private String requestUrl;
    private double percentChangeThreshold;
    private NotificationManagerCompat notificationManager;

    public AlertService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = NotificationManagerCompat.from(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (okHttpClient==null) {
                        okHttpClient = new OkHttpClient();
                    }
                    requestUrl = getApplicationContext().getString(R.string.mcx_live_price);
                    Request request = new Request.Builder().url(requestUrl).build();
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject rootJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                    parseJsonTree(rootJson);
                } catch (IOException | JSONException e) {
                    Log.v("ALERT_SERV", e.getMessage());
                }
            }
        }, 0, 5000);
        return START_STICKY;
    }

    private void parseJsonTree(JSONObject rootJson) throws JSONException {
        JSONArray dataJsonArray = rootJson.getJSONArray("data");
        for (int i=0; i<dataJsonArray.length(); i++) {
            JSONObject jsonObject = dataJsonArray.getJSONObject(i);
            String symbol = jsonObject.getString("Symbol");
            if (symbol.equals("GOLD") || symbol.equals("SILVER") || symbol.equals("CRUDE OIL") || symbol.equals("COPPER") || symbol.equals("LEAD") || symbol.equals("NICKLE") || symbol.equals("ZINC") || symbol.equals("ALUMINIUM") || symbol.equals("NATURAL GAS") || symbol.equals("COTTON")) {
                String rawPercent = jsonObject.getString("% Change");
                rawPercent = rawPercent.replace("%", "");
                double percentChange = Double.parseDouble(rawPercent);

                Intent intent = new Intent(this, QueryHashtagActivity.class);
                intent.putExtra("HASHTAG_DISPLAY", symbol);
                intent.putExtra("HASHTAG_TITLE", symbol);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel")
                        .setSmallIcon(R.drawable.radar)
                        .setContentTitle(symbol+" prices increased by "+percentChange)
                        .setContentText("Commodities prices fluctuated")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(100, builder.build());

                Log.v(symbol, String.valueOf(percentChange));
            }
        }
    }
}
