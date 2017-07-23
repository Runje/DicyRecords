package thomas.koenig.dicy.dicyrecords.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.developerpaul123.filepickerlibrary.FilePickerActivity;
import com.github.developerpaul123.filepickerlibrary.FilePickerBuilder;
import com.github.developerpaul123.filepickerlibrary.enums.Request;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;

import org.joda.time.DateTime;

import java.io.File;
import java.util.List;

import thomas.koenig.dicy.dicyrecords.AndroidUtils;
import thomas.koenig.dicy.dicyrecords.R;
import thomas.koenig.dicy.dicyrecords.model.RealmRecordsDB;
import thomas.koenig.dicy.dicyrecords.model.Record;

import static com.github.developerpaul123.filepickerlibrary.FilePickerActivity.REQUEST_FILE;

public class StatisticsActivity extends AppCompatActivity {


    public static final String GAME_NAME = "GAME_NAME";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private ListFragmentPagerAdapter adapter;
    private ViewPager viewPager;
    private RealmRecordsDB recordsDB;
    private int next_action = -1;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String gameName = intent.getStringExtra(GAME_NAME);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, AddRecordActivity.class);
                startActivity(intent);
            }
        });

        recordsDB = new RealmRecordsDB(this);


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ListFragmentPagerAdapter(getSupportFragmentManager(),
                StatisticsActivity.this, recordsDB);
        viewPager.setAdapter(adapter);


        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        showGamestatistics(gameName);

    }

    private void showGamestatistics(String gameName) {
        int pos = adapter.getPositionOfGame(gameName);
        viewPager.setCurrentItem(pos);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (id == R.id.action_backup) {
            next_action = R.id.action_backup;
            checkReadWritePermission();
            return true;
        }

        if (id == R.id.action_restore) {
            next_action = R.id.action_restore;
            checkReadWritePermission();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkReadWritePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.backup_exp_title);
                builder.setMessage(R.string.backup_exp);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(StatisticsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
                builder.show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            doNextAction();
        }

    }

    private void doNextAction() {
        switch(next_action)
        {
            case R.id.action_backup:
                backup();
                break;
            case R.id.action_restore:
                restore();
                break;
        }
    }


    private void showFileChooser()
    {
        /**Intent filePickerIntent = new Intent(this, FilePickerActivity.class);
        filePickerIntent.putExtra(FilePickerActivity.THEME_TYPE, ThemeType.DIALOG);
        filePickerIntent.putExtra(FilePickerActivity.REQUEST, Request.FILE);
        startActivityForResult(filePickerIntent, FilePickerActivity.REQUEST_FILE);**/

        new FilePickerBuilder(this)
                .withColor(android.R.color.holo_blue_bright)
                .withRequest(Request.FILE)
                .withScope(Scope.ALL)
                .useMaterialActivity(true)
                .launch(REQUEST_FILE);
    }

    private void restore() {
        logAll();

        showFileChooser();


    }

    private void logAll() {
        for(Record record : recordsDB.getAllRecords())
        {
            Log.d("Statistics", record.toString());
        }
    }



    private void backup() {
        File file = new File(Environment.getExternalStorageDirectory(), DateTime.now().toString("YY_MM_DD HH_mm"));
        recordsDB.backupDatabase(file);
        AndroidUtils.sendFilePerMail(this,file, "thomashorn87@gmail.com");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_FILE && resultCode == RESULT_OK)
        {
            String filePath = data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            if (filePath != null)
            {
                recordsDB.restoreDatabase(new File(filePath));
                recordsDB = new RealmRecordsDB(this);
                //updateFragments();
                finish();
                startActivity(getIntent());
                Log.d("Statistics", "AFTER RESTORE");
                logAll();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateFragments() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        for(Fragment frg : fragmentList) {
            /*final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commitAllowingStateLoss();*/
            ListFragment listFragment = (ListFragment) frg;
            listFragment.update();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    doNextAction();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
