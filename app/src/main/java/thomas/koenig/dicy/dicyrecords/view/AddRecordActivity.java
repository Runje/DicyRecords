package thomas.koenig.dicy.dicyrecords.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.UUID;

import thomas.koenig.dicy.dicyrecords.R;
import thomas.koenig.dicy.dicyrecords.model.Game;
import thomas.koenig.dicy.dicyrecords.model.Person;
import thomas.koenig.dicy.dicyrecords.model.RealmRecordsDB;
import thomas.koenig.dicy.dicyrecords.model.Record;
import thomas.koenig.dicy.dicyrecords.model.Utils;

import static thomas.koenig.dicy.dicyrecords.model.Record.dateFormat;

public class AddRecordActivity extends AppCompatActivity {

    private LinearLayout personsLayout;
    private EditText editDate;
    private RealmRecordsDB db;

    private Spinner spinnerGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_record);
        editDate = (EditText) findViewById(R.id.editTextDate);
        editDate.setText(DateTime.now().toString(dateFormat));
        spinnerGame = (Spinner) findViewById(R.id.spinnerGame);
        db = new RealmRecordsDB(this);
        updateSpinnerGame();

        personsLayout = (LinearLayout) findViewById(R.id.personsLayout);
        setupPersonsLayout((Button) personsLayout.findViewById(R.id.button_newPerson));
        updateSpinnerPersons();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                showStatistics("");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSpinnerGame() {
        SpinnerAdapter spinnerGameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Utils.gamesToStrings(db.getAllGames()));
        spinnerGame.setAdapter(spinnerGameAdapter);
        spinnerGame.invalidate();
    }

    private void updateSpinnerPersons() {
        SpinnerAdapter spinnerPersonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Utils.personsToStrings(db.getAllPersons()));

        for (int i = 0; i < personsLayout.getChildCount(); i++) {
            Spinner personsSpinner = (Spinner) personsLayout.getChildAt(i).findViewById(R.id.spinnerPerson);
            personsSpinner.setAdapter(spinnerPersonAdapter);
            personsSpinner.invalidate();
        }
    }


    public void editDate(View view)
    {
        new DatePickerDialog(this, new DatePickerDialog.DatePickerListener() {
            @Override
            public void onConfirm(DateTime dateTime) {
                editDate.setText(dateTime.toString(dateFormat));
            }
        }).show();
    }

    private void setupPersonsLayout(Button button)
    {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddEntryDialog(AddRecordActivity.this, Utils.personsToStrings(db.getAllPersons()), new AddEntryDialog.AddEntryDialogListener() {
                    @Override
                    public boolean checkName(String name) {
                        return !db.nameExists(name);
                    }

                    @Override
                    public void onSuccess(String name) {
                        db.addPerson(new Person(UUID.randomUUID().toString(), name));
                        updateSpinnerPersons();
                    }

                    @Override
                    public void onFailure(String name) {
                        Toast.makeText(AddRecordActivity.this, R.string.game_exists_already, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void deleteName(String name) {
                        db.deletePerson(name);
                    }
                }, R.string.addPerson).show();
            }
        });
    }
    public void addGame(View v)
    {
        new AddEntryDialog(this, Utils.gamesToStrings(db.getAllGames()), new AddEntryDialog.AddEntryDialogListener() {
            @Override
            public boolean checkName(String name) {
                return !db.gameExists(name);
            }

            @Override
            public void onSuccess(String name) {
                db.addGame(new Game(UUID.randomUUID().toString(), name));
                updateSpinnerGame();
            }

            @Override
            public void onFailure(String name) {
                Toast.makeText(AddRecordActivity.this, R.string.game_exists_already, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteName(String name) {
                db.deleteGame(name);
            }
        }, R.string.addGame).show();
    }

    public void addNextPerson(View v)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View nextPerson = inflater.inflate(R.layout.person, null);
        TextView personText = (TextView) nextPerson.findViewById(R.id.textViewPerson);
        personText.setText("Person " + (personsLayout.getChildCount() + 1));
        setupPersonsLayout((Button) nextPerson.findViewById(R.id.button_newPerson));
        personsLayout.addView(nextPerson);
        updateSpinnerPersons();
    }

    public void deleteLastPerson(View v)
    {
        if (personsLayout.getChildCount() > 0) {
            personsLayout.removeViewAt(personsLayout.getChildCount() - 1);
        }
    }

    public void addRecord(View v)
    {
        if (!checkValues())
        {
            Toast.makeText(this, R.string.invalid_data, Toast.LENGTH_SHORT).show();
            return;
        }
        DateTime dateTime = DateTime.parse(editDate.getText().toString(), DateTimeFormat.forPattern(dateFormat));
        Game[] games = db.getAllGames();
        Game game = games[spinnerGame.getSelectedItemPosition()];

        Person[] persons = db.getAllPersons();
        int personCount = personsLayout.getChildCount();
        Person[] involvedPersons = new Person[personCount];
        for (int i = 0; i < personCount; i++) {
            Spinner personsSpinner = (Spinner) personsLayout.getChildAt(i).findViewById(R.id.spinnerPerson);
            involvedPersons[i] = persons[personsSpinner.getSelectedItemPosition()];
        }

        EditText editValue = (EditText) findViewById(R.id.editTextRecordValue);
        try{
            int value = Integer.valueOf(editValue.getText().toString());
            Record record = new Record(UUID.randomUUID().toString(), value, game, involvedPersons, dateTime);
            db.addRecord(record);
            showStatistics(game.getName());
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this, R.string.invalid_record_value, Toast.LENGTH_SHORT).show();
        }


    }

    private boolean checkValues() {
        if (Spinner.INVALID_POSITION == spinnerGame.getSelectedItemPosition())
            return false;

        int count = personsLayout.getChildCount();
        int[] selected = new int[count];
        for (int i = 0; i < count; i++) {
            Spinner personsSpinner = (Spinner) personsLayout.getChildAt(i).findViewById(R.id.spinnerPerson);

            int selectedItemPosition = personsSpinner.getSelectedItemPosition();
            selected[i] = selectedItemPosition;
            for (int j = 0; j < i; j++) {
                if (selected[j] == selectedItemPosition)
                {
                    return false;
                }
            }

            if (Spinner.INVALID_POSITION == selectedItemPosition)
                return false;
        }

        return true;
    }

    private void showStatistics(String gameName) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra(StatisticsActivity.GAME_NAME, gameName);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
