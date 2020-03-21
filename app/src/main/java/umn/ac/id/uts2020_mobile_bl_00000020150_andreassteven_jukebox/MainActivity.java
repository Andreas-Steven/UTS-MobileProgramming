package umn.ac.id.uts2020_mobile_bl_00000020150_andreassteven_jukebox;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    Button BtnPlay, BtnPause, BtnAbout;
    SeekBar PlayPosition;
    MediaPlayer MP;
    TextView ElapsedTimeLabel, RemainingTimeLabel;
    ImageView RotatingImage;
    TextView Lirik;
    int TotalTIme;
    Boolean Playled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlayPosition = findViewById(R.id.PlayPosition);
        BtnPlay = findViewById(R.id.BtnPlay);
        BtnPause = findViewById(R.id.BtnPause);
        RotatingImage = findViewById(R.id.RotatingImage);
        ElapsedTimeLabel = findViewById(R.id.ElapsedTimeLabel);
        RemainingTimeLabel = findViewById(R.id.RemainingTimeLabel);
        BtnAbout = findViewById(R.id.BtnAbout);
        Lirik = findViewById(R.id.TextLirik);

        final ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(RotatingImage , "rotation", 0f, 360f);
        imageViewObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        imageViewObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        imageViewObjectAnimator.setInterpolator(new LinearInterpolator());
        imageViewObjectAnimator.setDuration(3000);

        final ObjectAnimator textViewObjectAnimator = ObjectAnimator.ofFloat(Lirik, "translationY",0f, -10000f);
        textViewObjectAnimator.setInterpolator(new LinearInterpolator());
        textViewObjectAnimator.setStartDelay(72000);
        textViewObjectAnimator.setDuration(300000);

        MP = MediaPlayer.create(this, R.raw.music); //File musik. Sengaja difix buat liriknya
        MP.setLooping(false);
        MP.seekTo(0);
        MP.setVolume(100, 100);
        TotalTIme = MP.getDuration();

        PlayPosition.setMax(TotalTIme);
        PlayPosition.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                    {
                        if(fromUser)
                        {
                            MP.seekTo(progress);
                            PlayPosition.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar)
                    {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar)
                    {

                    }
                }
        );

        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (MP != null)
                {
                    try
                    {
                        Message msg = new Message();
                        msg.what = MP.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
            }
        }).start();

        BtnPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!MP.isPlaying())
                {
                    if(Playled == false)
                    {
                        MP.start();
                        imageViewObjectAnimator.start();
                        textViewObjectAnimator.start();
                        Playled = true;
                    }
                    else if(Playled == true)
                    {
                        MP.start();
                        imageViewObjectAnimator.resume();
                        textViewObjectAnimator.resume();
                    }
                }
            }
        });

        BtnPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(MP.isPlaying())
                {
                    MP.pause();
                    imageViewObjectAnimator.pause();
                    textViewObjectAnimator.pause();
                }
            }
        });

        MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer arg0)
            {
                imageViewObjectAnimator.end();
                textViewObjectAnimator.end();
                Playled = false;
            }
        });

        BtnAbout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent =  new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        MP.stop();
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            int CurrentPosition = msg.what;
            // Update positionBar.
            PlayPosition.setProgress(CurrentPosition);
            // Update Labels.
            String elapsedTime = createTimeLabel(CurrentPosition);
            ElapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(TotalTIme - CurrentPosition);
            RemainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time)
    {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }
}
