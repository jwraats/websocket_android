package nl.jackevers.jwraats.websocket;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.URISyntaxException;

import io.socket.client.Manager;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:1337");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                System.out.println("Connected.."+args[0]);
            }
        }).on("text", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                System.out.println("Something happend" + args[0]);
            }
        });
        mSocket.connect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mSocket.send("landscape");
            getWindow().getDecorView().setBackgroundColor(Color.RED);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mSocket.send("portrait");
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        }
    }
}
