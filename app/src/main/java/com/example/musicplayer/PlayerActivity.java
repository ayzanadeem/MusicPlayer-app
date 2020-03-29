package com.example.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
//#ea5167
public class PlayerActivity extends AppCompatActivity {

    Button btn_next, btn_previous, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    static MediaPlayer mp;
    int position;
    ArrayList<File> songs;
    Thread updateSeekBar;
    String sname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songTextLabel = findViewById(R.id.songLabel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Now Playing");


        btn_next = findViewById(R.id.next);
        btn_previous = findViewById(R.id.previous);
        btn_pause = findViewById(R.id.pause);
        songSeekbar = findViewById(R.id.seekBar);

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPos = 0;

                while (currentPos < totalDuration) {
                    try {
                        sleep(500);
                        currentPos = mp.getCurrentPosition();
                        if (songSeekbar!=null){
                            songSeekbar.setProgress(currentPos);
                        }

                    } catch (InterruptedException e) {
                    }
                }

            }
        };

        if (mp != null) {
            mp.stop();
            mp.release();
        }

        Bundle extras = getIntent().getExtras();
        songs = (ArrayList) extras.getParcelableArrayList("songs");
        //String sname = songs.get(position).getName();
        songTextLabel.setText(extras.getString("songname"));
        songTextLabel.setSelected(true);

        position = extras.getInt("pos", 0);
        Uri u = Uri.parse(songs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        songSeekbar.setMax(mp.getDuration());
        updateSeekBar.start();


        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });


        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(mp.getDuration());
                if (mp.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mp.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mp.start();
                }
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean previouspaused = false;
                if (!mp.isPlaying()){
                    previouspaused=true;
                }
                mp.stop();
                mp.release();

                position = ((position + 1) % (songs.size()));
                Uri u = Uri.parse(songs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);

                sname = songs.get(position).getName();
                songTextLabel.setText(sname);

                if (!previouspaused){
                    mp.start();}
          }
        });


        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean previouspaused = false;
                if (!mp.isPlaying()){
                    previouspaused = true;
                }
                mp.stop();
                mp.release();

                position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
                Uri u = Uri.parse(songs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);

                sname = songs.get(position).getName();
                songTextLabel.setText(sname);
                if (!previouspaused){
                    mp.start();
                }


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
