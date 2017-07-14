package com.beginner.suman.spotifyinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SpotifyReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Intent", " " + intent.getAction());
        String artist = intent.getStringExtra("artist");
        String track = intent.getStringExtra("track");
        MainActivity.getInstance().updateTrack(artist, track);
    }
}