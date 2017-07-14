package com.beginner.suman.spotifyinfo;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadTask extends AsyncTask<String,Void,Void> {
    Context c;
    public DownloadTask(Context context) {
        this.c = context;
    }
    @Override
    protected Void doInBackground(String...arg){
        try {
            Log.d("DlTask", arg[0] + " " + arg[1]);
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse((arg[0]).replaceAll(" ","%20")));
            r.allowScanningByMediaScanner();
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, arg[1]+".mp3");
            r.setMimeType("audio/MP3");
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
