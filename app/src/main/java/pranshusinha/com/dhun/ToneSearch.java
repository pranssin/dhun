package pranshusinha.com.dhun;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by PranshuSinha on 23/04/16.
 */
public class ToneSearch extends Activity {
    Complex[][] results;
    private int RECORDER_SAMPLERATE = 48000;
    private int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    byte audio[];
    ArrayList<String> totalpoints;
    ListView textlist;
    ArrayAdapter<String> adapter;
    TextToSpeech t1;
    int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100,48000 };
    MediaRecorder recorder1 = new MediaRecorder();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalpoints = new ArrayList<>();
        textlist = (ListView) findViewById(R.id.listview2);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);


        PlayGifView pGif = (PlayGifView) findViewById(R.id.surfaceView);
        pGif.setImageResource(R.drawable.d6);
        pGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.speak("Recording Started", TextToSpeech.QUEUE_FLUSH, null);
                Complex[][] results;
                startRecording();
                try {
                    data_show(get_results());
                    textlist.setAdapter(adapter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    public Complex[][] get_results()
    {
        return results;
    }

    private void startRecording() {
        recorder1 = new MediaRecorder();

//        recorder = findAudioRecord();
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        recorder1.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder1.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder1.setAudioChannels(2);
        recorder1.setAudioSamplingRate(48000);
        recorder1.setOutputFile(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/myrecording.mp3");
        recorder1.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder1.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder1.start();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                results = writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();

    }
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
    private Complex[][] writeAudioDataToFile() {
        // Write the output audio in byte

        byte[] sData = new byte[BufferElements2Rec];
        String filePath = "/sdcard/voice8K16bitmono.txt";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        File aFile = new File("/recording.mp3");
        InputStream is = null;
        try {
            is = new FileInputStream(aFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] temp = new byte[1024];
        int read;

        try {
            while((read = is.read(temp)) >= 0){
                buffer.write(temp, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = buffer.toByteArray();
        Complex[][] results = fft_calculate(data);

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        isRecording=true;
        int bytesRead = 0;
        while (bytesRead < 100000) {
            // gets the voice output from microphone to byte format

//            int numvytesread = recorder1.read(sData, 0, BufferElements2Rec);
//            System.out.println("Short wirting to file" + sData.toString());
//            // // writes the data to file from buffer
//            // // stores the voice buffer
//            bytesRead = bytesRead + numvytesread;
////                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
//            out.write(sData, 0, numvytesread);
            audio = data;
            //                os.write(sData, 0, numvytesread);

        }
        return results;

    }

    void set_audio(byte[] audio) {
        this.audio = audio;
    }
    private void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    Complex[][] fft_calculate(byte[] song) {
        int totalSize = song.length;
        int amountPossible = totalSize / Harvester.CHUNK_SIZE;
        Complex[][] results = new Complex[amountPossible][];
        for (int times = 0; times < amountPossible; times++) {
            Complex[] complex = new Complex[Harvester.CHUNK_SIZE];
            for (int j = 0; j < Harvester.CHUNK_SIZE; j++) {
                complex[j] = new Complex(audio[(times * Harvester.CHUNK_SIZE) + j], 0);
            }
            results[times] = FFT.fft(complex);
        }
        return results;
    }

    static class Harvester {
        public static final int CHUNK_SIZE = 1024;
        public static final int LOWER_LIMIT = 40;
        public static final int UPPER_LIMIT = 300;
        public static final int[] RANGE = new int[] { 80, 120, 180, 300 };
    }

    public int[] data_show(Complex[][] results) throws InterruptedException
    {
        int[] recordPoints = new int[] { 0, 0, 0, 0 };
        double[] highscores = new double[] { 0.0, 0.0, 0.0, 0.0 };
        for(int i = 0; i < results.length; i++) {
            Complex[] res = results[i];
            for (int freq = 40; freq < 300-1; freq++) {


                double mag = res[freq].abs()+1;
                int index = getIndex(freq);

//		        System.out.println("Magnitude = "+mag);
                if (mag > highscores[index]) {
                    highscores[index] = mag;
                    recordPoints[index] = freq;
                }
            }
            String a=recordPoints[0]+","+recordPoints[1]+","+recordPoints[2]+","+recordPoints[3];
            if(totalpoints.contains(a)){}
            else{totalpoints.add(a);}
        }
        adapter =    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, totalpoints);
        return recordPoints;

    }

    private int getIndex(int freq)
    {
        int i = 0;
        while (Harvester.RANGE[i] < freq)
            i++;
        return i;
    }

    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

}
