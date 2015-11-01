package nl.jackevers.jwraats.websocket;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {
    private WebSocketClient mWebSocketClient;
    private String orientation;
    private boolean connection = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectWebSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.orientation = "landscape";
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            this.orientation = "portrait";
        }
        if(this.connection) {
            mWebSocketClient.send(this.orientation);
        }
    }

    private void changeColour(String message){
        if (message.equals("isGreen: 'true'")) {
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        } else if(message.equals("isGreen: 'false'")){
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        }
    }

    private void sendCurrentOrientation(){
        if(connection) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mWebSocketClient.send("landscape");
            } else {
                mWebSocketClient.send("portrait");
            }
        }
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://www.senzingyou.nl:1337");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }


        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                connection = true;
                sendCurrentOrientation();
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                changeColour(message);

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                connection = false;
                System.out.println("Closed: "+s);
                getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Error: "+e.toString());
                getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            }
        };
        mWebSocketClient.connect();
        sendCurrentOrientation();
    }
}
