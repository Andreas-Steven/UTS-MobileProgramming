package umn.ac.id.uts2020_mobile_bl_00000020150_andreassteven_jukebox;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    Button BtnPlay, BtnPause, BtnAbout;
    SeekBar PlayPosition;
    MediaPlayer MP;
    TextView ElapsedTimeLabel, RemainingTimeLabel;
    ImageView RotatingImage;
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

        final ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(RotatingImage , "rotation", 0f, 360f);
        imageViewObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        imageViewObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        imageViewObjectAnimator.setDuration(3000);
        imageViewObjectAnimator.setInterpolator(new AccelerateInterpolator());

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
                        Playled = true;
                    }
                    else if(Playled == true)
                    {
                        MP.start();
                        imageViewObjectAnimator.resume();
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
                }
            }
        });

        MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer arg0)
            {
                imageViewObjectAnimator.end();
                Playled = false;
            }
        });
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
