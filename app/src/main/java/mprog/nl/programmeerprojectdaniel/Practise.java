package mprog.nl.programmeerprojectdaniel;

/* Student name: Daniel Oliemans
 * Student number: 11188669
 * Universiteit van Amsterdam
 * Programmeer Project
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

/*
 * Activity that allows users to create lists of their own and shows users the word lists they have
 * created
 */
public class Practise extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Declaring the DBHelper file and the ListView
    DBHelper dbHelper;
    ListView wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialising the ListView and DBHelper file
        wordList = (ListView)findViewById(R.id.wordList);
        dbHelper = new DBHelper(this, null, null, 1);

        // Fill the ArrayList with the list returned from the function in DBHelper
        ArrayList<String> listsArrayList = dbHelper.checkLists();

        // Set the ArrayAdapter to display the ArrayList
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listsArrayList);
        wordList.setAdapter(arrayAdapter);

        // If item is long clicked, it can be deleted
        wordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id){
                final String chosenList = String.valueOf(adapterView.getItemAtPosition(position));

                    // Creates dialog window for confirmation of deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(Practise.this);
                    builder
                            .setMessage("Are you sure you want to delete this list?")
                            .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Deletes the list, including its contents
                                    dbHelper.deleteLists(chosenList);
                                    Toast.makeText(Practise.this, "List " + chosenList + " has been deleted.", Toast.LENGTH_SHORT).show();

                                    // Re-set the adapter to update the ListView
                                    ArrayList<String> listsArrayList = dbHelper.checkLists();
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Practise.this, android.R.layout.simple_list_item_1, listsArrayList);
                                    wordList.setAdapter(arrayAdapter);
                                }
                            })
                            // Nothing is done when "No" is pressed
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                return false;
            }
        });

        // If item is clicked, user goes to the corresponding Exercises activity
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Determine chosen wordlist and opens new intent
                final String chosenList = String.valueOf(adapterView.getItemAtPosition(position));
                final Intent exercisesIntent = new Intent(Practise.this, Exercises.class);

                // Creates dialog window for language selection
                AlertDialog.Builder builder = new AlertDialog.Builder(Practise.this);
                builder
                        .setMessage("Practise words EN-NL or NL-EN?")
                        // Set list to EN-NL if pressed
                        .setPositiveButton("EN-NL",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Create string language which is passed on to Exercises
                                String language = "english";

                                // Bundles the word list as string and bundles the english words
                                ArrayList<String> englishWordsArray = dbHelper.getEnglishWords(chosenList);
                                Bundle bundle = new Bundle();
                                bundle.putString("chosenlist", chosenList);
                                bundle.putStringArrayList("wordlist", englishWordsArray);
                                bundle.putString("language", language);

                                // Start exercises activity
                                exercisesIntent.putExtras(bundle);
                                startActivity(exercisesIntent);
                            }
                        })
                        // Set list to NL-EN if pressed
                        .setNegativeButton("NL-EN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                // Create string language which is  passed on to Exercises
                                String language = "dutch";

                                // Bundles the word list as string and bundles the english words
                                ArrayList<String> dutchWordsArray = dbHelper.getDutchWords(chosenList);
                                Bundle bundle = new Bundle();
                                bundle.putString("chosenlist", chosenList);
                                bundle.putStringArrayList("wordlist", dutchWordsArray);
                                bundle.putString("language", language);

                                // Start exercises activity
                                exercisesIntent.putExtras(bundle);
                                startActivity(exercisesIntent);
                            }
                        })
                        .show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addButton);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newList = new Intent(view.getContext(), NewList.class);
                newList.putExtra("newList", view.getId());
                startActivity(newList);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.homeButton) {
            Intent home = new Intent(this, MainActivity.class);
            startActivity(home);
        } else if (id == R.id.dictionaryButton) {
            Intent dictionary = new Intent(this, Dictionary.class);
            startActivity(dictionary);
        } else if (id == R.id.practiseButton) {
            Toast.makeText(this, "You are already at Practise", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.wordListButton) {
            Intent wordList = new Intent(this, WordLists.class);
            startActivity(wordList);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
