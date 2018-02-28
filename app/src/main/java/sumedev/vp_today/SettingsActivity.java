package sumedev.vp_today;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private boolean changed = false;

    Spinner spin;
    Button btnApply;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spin = findViewById(R.id.spinStufen);
        btnApply = findViewById(R.id.btnApply);
        btnCancel = findViewById(R.id.btnCancel);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hasChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spin.setSelection(0);
                hasChanged();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                onBackPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                SharedLogic.getStufen()
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = findViewById(R.id.spinStufen);
        sItems.setAdapter(adapter);
    }

    private void save() {
        SharedPreferences settings = getSharedPreferences("sumedev.vp_today.app", Context.MODE_PRIVATE);
        if(settings.edit().putString("stufe", spin.getSelectedItem().toString()).commit())
            Toast.makeText(getApplicationContext(), "Einstellungen gesichert!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Speichern fehlgeschlagen!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (changed) {
            if (Util.ShowYesNoDialog(getApplicationContext(), "Möchten Sie die ungespeicherten Änderungen speichern?") == DialogInterface.BUTTON_POSITIVE) {
                save();
            }
        }
        onBackPressed();
        return true;
    }

    private void hasChanged() {
        changed = true;
    }
}