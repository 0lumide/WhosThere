package co.mide.whosthere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.quinny898.library.persistentsearch.SearchBox.SearchListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    NoBackSearchBox search;
    private ViewSwitcher viewSwitcher;
    private boolean isSearchResultsMode = false;
    private View contentLayout;
    private View searchResultsLayout;
    private RecyclerView recyclerView;
    SharedPreferences preferences;
    private int total, successful;
    TextView totalView, succView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, PhoneStateMonitor.class);
        startService(intent);
        preferences = getSharedPreferences("co.mide.whosthere", Context.MODE_PRIVATE);
        total = preferences.getInt("total", 0);
        successful = preferences.getInt("successful", 0);

        viewSwitcher = (ViewSwitcher)findViewById(R.id.view_switcher);
        search = (NoBackSearchBox)findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);

        contentLayout = View.inflate(this, R.layout.content_main, viewSwitcher);
        searchResultsLayout = View.inflate(this, R.layout.search_results, viewSwitcher);


        totalView = (TextView)contentLayout.findViewById(R.id.requests);
        succView = (TextView)contentLayout.findViewById(R.id.successful);

        totalView.setText(total+"");
        succView.setText(successful+"");

        recyclerView = (RecyclerView)searchResultsLayout.findViewById(R.id.recycler_view);
        ArrayList<RecyclerViewAdapter.SearchResult> data = new ArrayList<>();
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        search.setBackListener(new NoBackSearchBox.SearchBackPressedListener() {
            @Override
            public boolean onBackPressed() {
                if (isSearchResultsMode) {
                    ((RecyclerViewAdapter) recyclerView.getAdapter()).clear();
                    viewSwitcher.showNext();
                    search.setSearchString("");
                    isSearchResultsMode = false;
                    return true;
                }
                return false;
            }
        });

        search.setMenuListener(new SearchBox.MenuListener() {

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(MainActivity.this, "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchListener() {

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String changed) {
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String sTerm) {
                final String searchTerm = sTerm.trim();
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Searching");
                progressDialog.show();
                adapter.clear();
                total++;
                preferences.edit().putInt("total", total);
                totalView.setText(total + "");
                MyGcmListenerService.setResultsListener(new ResultsListener() {
                    @Override
                    public void onResultGotten(final String query, final String result) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.addData(query, result);
                                successful++;
                                preferences.edit().putInt("successful", successful);
                                succView.setText(successful + "");
                            }
                        });
                    }
                });
                final ServerRequests serverRequests = new ServerRequests(MainActivity.this);
                (new Thread() {
                    public void run() {
                        // do stuff;
                        if(ContactsManager.isName(searchTerm)) {
                            Log.v("oaf", "is Name");
                            serverRequests.findPhoneNumber(searchTerm,
                                    ContactsManager.getOwnPhoneNumber(MainActivity.this),
                                    ContactsManager.getAllContacts(MainActivity.this), null);
                        }else{
                            serverRequests.findName(ContactsManager.getOwnPhoneNumber(MainActivity.this),
                                    searchTerm, ContactsManager.getAllContacts(MainActivity.this), null);
                        }
                    }
                }).start();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        serverRequests.cleanUp(ContactsManager.getOwnPhoneNumber(MainActivity.this), null);
                        progressDialog.dismiss();
                        //TODO populate recyler view
                        search.toggleSearch();
                        MyGcmListenerService.setResultsListener(null);
                    }
                }, 20 * 1000);
                if(!isSearchResultsMode) {
                    viewSwitcher.showNext();
                    isSearchResultsMode = true;
                }
            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }


            @Override
            public void onSearchCleared() {

            }

        });
        if(!GCMRegistration.isGCMRegistered(this)) {
            new GCMRegistration(this, new GCMRegistration.OnKeyStoredCallback() {
                @Override
                public void onKeyStored(boolean stored) {
                    //Do nothing
                }
            }).register();
        }
    }

    @Override
    public void onBackPressed(){
        if(isSearchResultsMode) {
            isSearchResultsMode = false;
            viewSwitcher.showNext();
            search.setSearchString("");
        }else
            super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
