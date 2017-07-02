package com.minorius.watchertube;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minorius.watchertube.gson.duration.Example;
import com.minorius.watchertube.gson.main.Item;
import com.minorius.watchertube.gson.main.Main;
import com.minorius.watchertube.gson.main.Snippet;

import org.codehaus.jackson.map.util.ISO8601DateFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int flag;

    public static final String KEY = "AIzaSyAFUj0tBwzsexsjV81qjF9f1pCGtmDemDE";

    private static final String LINK_1 = "PLte2HHUYysP9gUZycxG1gq2Z5IoIcHVwe";
    private static final String LINK_2 = "PLte2HHUYysP8XdLtZAFgXnw2itjEOmgJm";
    private static final String LINK_3 = "PLuztlLiWulOtaFNzX3jGYVyn102XQuBe4";

    private String nextPageToken = "";
    private static int maxResult = 10;

    private  String PLAYLIST_1 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_1+"&key="+KEY+"&maxResults="+maxResult+"&pageToken="+nextPageToken;
    private  String PLAYLIST_2 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_2+"&key="+KEY+"&maxResults="+maxResult+"&pageToken="+nextPageToken;
    private  String PLAYLIST_3 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_3+"&key="+KEY+"&maxResults="+maxResult+"&pageToken="+nextPageToken;

    private ArrayList<ViewElement> listForView;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private ProfileTracker profileTracker;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    private MyAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomTitle();

        setContentView(R.layout.activity_content);

        recyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);
        navigationView = (NavigationView) findViewById(R.id.id_nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        imageView = (ImageView) findViewById(R.id.id_logo);

        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listForView = new ArrayList<>();

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        imageView.setVisibility(View.GONE);
        switch (item.getItemId()){
            case R.id.first_id:
                setDefaultPropertiesForRecyclerViewByFlag(1);
                loadJSON(PLAYLIST_1);
                break;
            case R.id.second_id:
                setDefaultPropertiesForRecyclerViewByFlag(2);
                loadJSON(PLAYLIST_2);
                break;
            case R.id.third_id:
                setDefaultPropertiesForRecyclerViewByFlag(3);
                loadJSON(PLAYLIST_3);

                break;
            default:
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                break;
        }
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDefaultPropertiesForRecyclerViewByFlag(int flag){
        this.flag = flag;
        listForView.clear();
        nextPageToken = "";
        if (scrollListener != null){
            System.out.println("Reset state");
            scrollListener.resetState();
        }
    }

    public void loadJSON(final String link){
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final JsonObject jsonObject = getJSONFromLink(link);
                Gson gson = new Gson();
                final Main fromJson = gson.fromJson(jsonObject, Main.class);

                handler.post(new Runnable() {

                    JsonObject jsonObjectForDuration;

                    @Override
                    public void run() {

                        if (fromJson != null) {
                            for (Item pageInfo : fromJson.getItems()) {
                                try {
                                    final Snippet snippet = pageInfo.getSnippet();

                                    String videoID = snippet.getResourceId().getVideoId();
                                    final String linkForDuration = "https://www.googleapis.com/youtube/v3/videos?id="+videoID+"&key="+KEY+"&part=contentDetails";

                                    Thread t = new Thread(new Runnable() {

                                        Handler h2 = new Handler();
                                        @Override
                                        public void run() {
                                            final String duration;
                                            //System.out.println(linkForDuration);
                                            //+
                                            jsonObjectForDuration = getJSONFromLink(linkForDuration);
                                            //+
                                            String ISODuration = parseJSONForDuration(jsonObjectForDuration);

                                            duration = getDuration(ISODuration);

                                            h2.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    try {
                                                        ViewElement viewElement = new ViewElement( snippet.getTitle(),
                                                                snippet.getDescription(),
                                                                snippet.getResourceId().getVideoId(),
                                                                snippet.getThumbnails().getMedium().getUrl(),
                                                                duration);

                                                        if (!listForView.contains(viewElement)){
                                                            listForView.add(viewElement);
                                                            adapter.notifyDataSetChanged();
                                                        }


                                                    }catch (Exception e){
                                                        System.out.println(e);
                                                    }

                                                }
                                            });
                                        }
                                    });
                                    t.start();
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            }
                            if (fromJson.getPrevPageToken() == null) {
                                startRecyclerView(listForView);
                            }
                            nextPageToken = fromJson.getNextPageToken();
                        }
                    }

                    private String parseJSONForDuration(JsonObject jsonObject){

                        Gson gson = new Gson();
                        Example fromJson = gson.fromJson(jsonObject, Example.class);

                        for (com.minorius.watchertube.gson.duration.Item example : fromJson.getItems()) {
                            //System.out.println(example);
                            String duration = example.getContentDetails().getDuration();
                            return duration;
                        }

                        System.out.println("parseJSONForDuration error");
                        return "-";
                    }
                });
            }

            private JsonObject getJSONFromLink(String uri){

                JsonObject returnedJSONObject = null;

                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    returnedJSONObject = (new JsonParser()).parse(sb.toString()).getAsJsonObject();

                    br.close();
                }catch (Exception e){
                    System.out.println(e);
                }
                return returnedJSONObject;
            }
        };
        new Thread(runnable).start();
    }

    private void startRecyclerView(final ArrayList<ViewElement> list){
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(list, getApplicationContext());
        recyclerView.setAdapter(adapter);
        updateRecyclerView();
    }

    private void updateRecyclerView(){
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(view);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private String getDuration(String ISOString){

        String timeString;

        System.out.println(ISOString);
        try{
            if(ISOString != null){

                ISOString = ISOString.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "");
                String arr[] = ISOString.split(":");

                switch (arr.length){
                    case 1 : {
                        timeString = String.format("%02d", Integer.parseInt(arr[0]));
                        return "00:"+timeString;
                    }
                    case 2 : {
                        timeString = String.format("%02d:%02d", Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                        return timeString;
                    }
                    case 3 : {
                        timeString = String.format("%d:%02d:%02d", Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        return timeString;
                    }
                    default:
                        System.out.println("miss");
                }

            }

        }catch (Exception e){
            System.out.println("getDuration error");
        }

        return  "-";
    }

    public void loadMore(View view){
        view.post(new Runnable() {
            @Override
            public void run() {

                switch (flag){
                    case 1:
                        String NEXT_LIST_1 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_1+"&key="+KEY+"&maxResults="+maxResult+"&pageToken="+nextPageToken;
                        loadJSON(NEXT_LIST_1);
                        break;
                    case 2:
                        String NEXT_LIST_2 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_2+"&key="+KEY+"&maxResults="+maxResult+"&pageToken="+nextPageToken;
                        loadJSON(NEXT_LIST_2);
                        break;
                    case 3:
                        String NEXT_LIST_3 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_3+"&key="+KEY+"&maxResults="+maxResult+"&pageToken="+nextPageToken;
                        loadJSON(NEXT_LIST_3);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case R.id.id_logout :
                logOut();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Error_menu", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCustomTitle() {
        if (Profile.getCurrentProfile() != null){
            setTitle(Profile.getCurrentProfile().getName());
        }else {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    if (currentProfile != null) {
                        setTitle(currentProfile.getName());
                    }
                }
            };
        }
    }

    private void logOut(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        LoginManager.getInstance().logOut();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}