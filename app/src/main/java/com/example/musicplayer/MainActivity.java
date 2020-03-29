package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        listView = findViewById(R.id.songListView);

        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                display();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                System.exit(0);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    public ArrayList<File> findSongs(File file) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        String artist;
        ArrayList<File> songlist = new ArrayList<>(); //removed "File" from tags. put back if error occurs
        File[] files = file.listFiles();

        for (File singlefile : files) {
            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                songlist.addAll(findSongs(singlefile));
            } else {
                if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                    songlist.add(singlefile);
                    metaRetriever.setDataSource(singlefile.getPath());
                    try {
                        artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    }
                    catch (Exception e){
                        artist = "Unknown Artist";
                    }
               /*     Toast toast = Toast.makeText(getApplicationContext(), artist, Toast.LENGTH_SHORT);
                    toast.show();
*/
                }
            }
        }

        return songlist;
    }


    void display() {
        final ArrayList<File> songs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];

        for (int i = 0; i < songs.size(); i++) {
            items[i] = songs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songSelected = listView.getItemAtPosition(position).toString();
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("songs",songs)
                        .putExtra("songname",songSelected).putExtra("pos",position));
            }
        });
    }
}

