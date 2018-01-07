package pranshusinha.com.dhun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;


public class Main2Activity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.d7);
        int newHeight = getWindowManager().getDefaultDisplay().getHeight() / 2;
        int orgWidth = image.getDrawable().getIntrinsicWidth();
        int orgHeight = image.getDrawable().getIntrinsicHeight();

        PlayGifView pGif = (PlayGifView) findViewById(R.id.surfaceView);
        pGif.setImageResource(R.drawable.d7);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(Main2Activity.this, AndroidBuildingMusicPlayerActivity.class);
                Main2Activity.this.startActivity(mainIntent);
                Main2Activity.this.finish();
            }
        }, 10000);
        pGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_activity();
            }
        });

    }

    public void change_activity()
    {
        Intent in = new Intent(getApplicationContext(), AndroidBuildingMusicPlayerActivity.class);
        startActivity(in);
    }


}
