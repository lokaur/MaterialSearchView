package com.lokaur.samplesearchview;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lokaur.materialsearchview.MaterialSearchView;
import com.lokaur.materialsearchview.SearchViewAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView mSimpleSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mSimpleSearchView = findViewById(R.id.simpleSV);
        mSimpleSearchView.setOnSearchListener(new SearchViewAdapter() {
            @Override
            public void onSearchQuerySubmit(String searchQuery) {
                Toast.makeText(MainActivity.this, searchQuery, Toast.LENGTH_LONG).show();
            }
        });

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionSearch) {
            mSimpleSearchView.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mSimpleSearchView.isFocused() && mSimpleSearchView.isVisible()) {
            mSimpleSearchView.hide();
            return;
        }

        super.onBackPressed();
    }
}
