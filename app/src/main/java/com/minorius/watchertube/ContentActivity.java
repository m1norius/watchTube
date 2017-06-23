package com.minorius.watchertube;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minorius.watchertube.gson.Item;
import com.minorius.watchertube.gson.Snippet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public int flag;

    public static final String KEY = "AIzaSyAFUj0tBwzsexsjV81qjF9f1pCGtmDemDE";

    public static final String LINK_1 = "PLuztlLiWulOskrqywdF27W-L1_K8qExRQ";
    public static final String LINK_2 = "PLuztlLiWulOvqN3m2-msBXMHGI0To-bPm";
    public static final String LINK_3 = "PLuztlLiWulOtaFNzX3jGYVyn102XQuBe4";

    public static int maxResult = 10;

    public  String PLAYLIST_1 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_1+"&key="+KEY+"&maxResults="+maxResult;
    public  String PLAYLIST_2 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_2+"&key="+KEY+"&maxResults="+maxResult;
    public  String PLAYLIST_3 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_3+"&key="+KEY+"&maxResults="+maxResult;

    private static ArrayList<ViewElements> listForView;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private NavigationView navigationView;

    private EndlessRecyclerViewScrollListener scrollListener;

    MyAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        recyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);
        navigationView = (NavigationView) findViewById(R.id.id_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        imageView = (ImageView) findViewById(R.id.id_logo);

        listForView = new ArrayList<>();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        imageView.setVisibility(View.GONE);

        switch (item.getItemId()){
            case R.id.first_id:
                flag = 1;
                maxResult = 10;
                if (scrollListener != null){
                    scrollListener.resetState();
                }
                loadJSON(PLAYLIST_1);
                break;
            case R.id.second_id:
                flag = 2;
                maxResult = 10;
                if (scrollListener!=null){
                    scrollListener.resetState();
                }
                loadJSON(PLAYLIST_2);
                break;
            case R.id.third_id:
                flag = 3;
                maxResult = 10;
                if (scrollListener!=null){
                    scrollListener.resetState();
                }
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

    public void loadJSON(final String link){
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    final String loadedDataFromLink = sb.toString();

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            JsonObject jsonObject = (new JsonParser()).parse(loadedDataFromLink).getAsJsonObject();
                            Gson gson = new Gson();

                            com.minorius.watchertube.gson.Gson fromJson = gson.fromJson(jsonObject, com.minorius.watchertube.gson.Gson.class);
                            listForView.clear();
                            if (fromJson != null){
                                for (Item pageInfo : fromJson.getItems()){
                                    Snippet snippet = pageInfo.getSnippet();
                                    listForView.add(new ViewElements(
                                            snippet.getTitle(),
                                            snippet.getDescription(),
                                            snippet.getResourceId().getVideoId(),
                                            snippet.getThumbnails().getMedium().getUrl()));
                                }
                            }


                            if (maxResult == 10){
                                startRecyclerView(listForView);
                            }
                            adapter.notifyItemRangeInserted(listForView.size(), listForView.size()-1);

                        }
                    });

                    br.close();
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        };
        new Thread(runnable).start();
    }

    public void startRecyclerView(final ArrayList<ViewElements> list){
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(list, getApplicationContext());
        recyclerView.setAdapter(adapter);
        updateRecyclerView();
    }

    public void updateRecyclerView(){
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(view);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }

    public void loadMore(View view){

        view.post(new Runnable() {
            @Override
            public void run() {
                maxResult+=10;

                switch (flag){
                    case 1:
                        String NEXT_LIST_1 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_1+"&key="+KEY+"&maxResults="+maxResult;
                        loadJSON(NEXT_LIST_1);
                        break;
                    case 2:
                        String NEXT_LIST_2 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_2+"&key="+KEY+"&maxResults="+maxResult;
                        loadJSON(NEXT_LIST_2);
                        break;
                    case 3:
                        String NEXT_LIST_3 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+LINK_3+"&key="+KEY+"&maxResults="+maxResult;
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
        switch (item.getItemId()){
            case R.id.id_logout :
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LoginManager.getInstance().logOut();
                startActivity(intent);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Error_menu", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(intent);
    }
}
