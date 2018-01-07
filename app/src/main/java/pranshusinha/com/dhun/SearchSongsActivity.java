package pranshusinha.com.dhun;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by PranshuSinha on 23/04/16.
 */

public class SearchSongsActivity extends ListActivity {
    private static final int REQUEST_CODE = 1234;
    SongManager song;
    String[] ssongindex;
    private TextView searchtext;
    private ListView list;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
//    Button Start;
    TextView Speech;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        song = new SongManager();
//        list = (ListView) findViewById();
        songsList = song.getPlayList();
        searchtext = (TextView) findViewById(R.id.searchstring);
        ImageButton search = (ImageButton) findViewById(R.id.btnsearch2);
        ImageButton searchv = (ImageButton) findViewById(R.id.btnsearch1);
        textlist = (ListView) findViewById(R.id.listView);

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Search(songsList, searchtext);

            }
        });

        searchv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SearchVoice(songsList);

            }
        });
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting listitem index
                int songIndex = Integer.parseInt(ssongindex[position]);

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        AndroidBuildingMusicPlayerActivity.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", songIndex);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });
    }


    public void Search(ArrayList<HashMap<String, String>> songsList, TextView searchtext)
    {
        textlist.setAdapter(null);
        setListAdapter(null);
        ArrayList<String> index = new ArrayList<String>();
        for(int songindex=0;songindex<songsList.size();songindex++)
        {
            String songname = songsList.get(songindex).get("songTitle");
            String temp = songname.toLowerCase();
            String temp2 = searchtext.getText().toString();
            boolean result = temp.indexOf(temp2) > -1;
            if(result)
            {
                File file = new File(songsList.get(songindex).get("songPath"));
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
                index.add(Integer.toString(songindex));
                results.add(song);
            }else{}

        }
        ssongindex = new String[index.size()];
        ssongindex = index.toArray(ssongindex);

        ListAdapter adapter = new SimpleAdapter(this, results,
                R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
                R.id.songTitle });
        setListAdapter(adapter);

    }

    public void SearchVoice(ArrayList<HashMap<String, String>> songsList)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something!!");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "hi-In");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        textlist.setAdapter(null);
//            String hello = data.getDataString();
        matches_text = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            String hello= matches_text.get(matches_text.lastIndexOf(matches_text));
        ArrayAdapter<String> adapter =    new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, matches_text);
        textlist.setAdapter(adapter);
        textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Speech.setText("You have said " +matches_text.get(position));
//                match_text_dialog.hide();
                searchtext.setText(matches_text.get(position));
                Search(songsList, searchtext);
                textlist.setAdapter(null);
            }
        });
        super.onActivityResult(requestCode, resultCode, data);

    }

    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public void onBackPressed() {
        // Add data to your intent
        backbutton();
    }
    public void backbutton()
    {
        Intent openMainActivity= new Intent(SearchSongsActivity.this, AndroidBuildingMusicPlayerActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openMainActivity);
    }


}