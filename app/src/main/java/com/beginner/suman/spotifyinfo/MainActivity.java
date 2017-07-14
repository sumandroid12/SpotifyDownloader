package com.beginner.suman.spotifyinfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {
    private static MainActivity ins;
    SpotifyReceiver receiver;
    IntentFilter intentFilter;
    String artist,track;
    EditText editTextSearch;
    ArrayList<String> titles=new ArrayList<>(),links = new ArrayList<>(), sizes= new ArrayList<>();
    SongListViewAdapter songListViewAdapter;
    ListView listViewSongs;
    Button buttonSearch;
    SizeTask sizeTask;
    ScraperTask scraperTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;
        setContentView(R.layout.activity_main);
        listViewSongs = (ListView) findViewById(R.id.list);
        songListViewAdapter = new SongListViewAdapter(getApplicationContext(),titles,sizes);
        listViewSongs.setAdapter(songListViewAdapter);
        receiver = new SpotifyReceiver();
        intentFilter = new IntentFilter("com.spotify.music.metadatachanged");
        editTextSearch = (EditText) findViewById(R.id.url);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sizeTask != null) {
                    sizeTask.cancel(true);
                }
                if (scraperTask != null) {
                    scraperTask.cancel(true);
                }
                if (titles != null || links != null || sizes != null) {
                    try {
                        titles.clear();
                        sizes.clear();
                        links.clear();
                        songListViewAdapter.updateList(titles, sizes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                scraperTask = new ScraperTask();
                scraperTask.execute(editTextSearch.getText().toString());
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
        });

        listViewSongs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    DownloadTask d = new DownloadTask(getApplicationContext());
                    d.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,links.get(i), titles.get(i));
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return true;
                }
            }
        });
    }

    public static MainActivity  getInstance(){
        return ins;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    public void updateTrack(String newArtist, String newTrack){
        artist = newArtist;
        track = newTrack;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextSearch.setText(artist+" "+track);
            }
        });
    }
    public class ScraperTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String URL = strings[0];
            URL.replace(" ","_");
            try {
                Document doc = Jsoup.connect("http://musicwhales.net/tracks/"+URL+".html").get();
                Elements results = doc.select("ul.results > li");
                for (Element result : results) {
                    Log.d("title",result.select("#xftitle").text());
                    titles.add(result.select("#xftitle").text());
                    links.add(result.select("#norber").text());
                }
                Log.d("AllTitles", titles.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    }
                });
                if(titles.size()==0){
                    titles.add("No results found.");
                    sizes.add(" ");
                }
                afterReq();


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        public void afterReq(){
            sizeTask = new SizeTask();
            sizeTask.execute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public class SizeTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String...arg) {
            sizes.clear();
            Log.d("Links",links.toString());
            sizes = new ArrayList<>(Collections.nCopies(titles.size(), " "));
            if (links!=null || links.size()!=0){
                Log.d("string", sizes.toString());
                for (int i = 0; i < links.size(); i++) {
                    Log.d("url", links.get(i));
                    try{
                        URL url = new URL(links.get(i));
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        int file_size = (int) (urlConnection.getContentLength())/1024;
                        sizes.set(i, Integer.toString(file_size)+"KB");
                    }catch (Exception e){
                        e.printStackTrace();
                        sizes.add("");
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            songListViewAdapter.updateList(titles, sizes);
                        }
                    });
                }
            }
            return null;
        }
    }
}
