package pranshusinha.com.dhun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by PranshuSinha on 25/04/16.
 */
public class ParthFragment extends Activity {
    private GestureDetector detector;
    View.OnTouchListener gestureListener;
    private ImageButton next;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_parth);
        next = (ImageButton)findViewById(R.id.imageView3);
        detector = new GestureDetector(new SwipeGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        };
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(ParthFragment.this, SaumyaFragment.class);
                startActivity(i);
            }
        });
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 50;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                Toast t = Toast.makeText(ParthFragment.this, "Gesture detected", Toast.LENGTH_SHORT);
                t.show();
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onLeftSwipe();
                }
                // Right swipe
                else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("Home", "Error on gestures");
            }
            return false;
        }

    }
    public void onLeftSwipe()
    {

        Intent i = new Intent(getApplicationContext(),SaumyaFragment.class);
        startActivity(i);

    }

    public void onRightSwipe()
    {
        Intent i = new Intent(getApplicationContext(),Owners.class);
        startActivity(i);
    }
    public void onBackPressed() {
        // Add data to your intent
        backbutton();
    }
    public void backbutton()
    {
        Intent i = new Intent(getApplicationContext(), AndroidBuildingMusicPlayerActivity.class);
        startActivityForResult(i, 100);
    }
}