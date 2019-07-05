package com.example.sqlitecrud;
import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener{

    private String email, gender, hobbies, blood;
    SqliteHelper sqliteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqliteHelper = new SqliteHelper(this);
        ((TextView) findViewById(R.id.textView)).setText(getResources().getString(R.string.app_name));

        email = gender = hobbies = blood = "";

        RadioGroup radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupGender.setOnCheckedChangeListener(this);

        Spinner spinnerZodiac = (Spinner) findViewById(R.id.spinnerZodiac);
        // Populate the spinner with data source
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZodiac.setAdapter(adapter);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioButtonId);
        gender = radioButton.getText().toString();
    }

    public void onCheckboxClicked(View view) {

        CheckBox chkJogging = (CheckBox) findViewById(R.id.chkJogging);
        CheckBox chkCoding = (CheckBox) findViewById(R.id.chkCoding);
        CheckBox chkWriting = (CheckBox) findViewById(R.id.chkWriting);

        StringBuilder sb = new StringBuilder();

        if (chkJogging.isChecked()) {
            sb.append(", " + chkJogging.getText());
        }

        if (chkCoding.isChecked()) {
            sb.append(", " + chkCoding.getText());
        }

        if (chkWriting.isChecked()) {
            sb.append(", " + chkWriting.getText());
        }

        if (sb.length() > 0) { // No toast if the string is empty
            // Remove the first comma
            hobbies = sb.deleteCharAt(sb.indexOf(",")).toString();
        } else {
            hobbies = "";
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        blood = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // An interface callback
    }

    public void save(View view) {

        email = ((EditText)findViewById(R.id.txtEmail)).getText().toString();

        if (email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email cannot be empty!", Toast.LENGTH_LONG).show();
            return;
        }

        boolean result = sqliteHelper.saveUser(email, gender,  hobbies,  blood);
        if (result){
            Toast.makeText(getApplicationContext(), "Successfully saved!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to save!", Toast.LENGTH_LONG).show();
        }
    }

    public void retrieve(View view) {

        email = ((EditText)findViewById(R.id.txtEmail)).getText().toString();

        Cursor cursor = sqliteHelper.getUser(email);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            email = cursor.getString(cursor.getColumnIndex("email"));
            gender = cursor.getString(cursor.getColumnIndex("gender"));
            hobbies = cursor.getString(cursor.getColumnIndex("hobbies"));
            blood = cursor.getString(cursor.getColumnIndex("blood"));
            if (!cursor.isClosed()) {
                cursor.close();
            }
        } else {
            email = "";
            gender = "";
            hobbies = "";
            blood = "";
        }

        setupUI();


    }

    public void delete(View view) {

        email = ((EditText)findViewById(R.id.txtEmail)).getText().toString();

        sqliteHelper.deleteUser(email);

        email = gender = hobbies = blood = "";

        setupUI();


    }
    private SQLiteDatabase getReadableDatabase(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM users WHERE email=?";
        Cursor cursor =  db.rawQuery(sql, new String[] { email });
        return db;
    }

    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "email=?", new String[] { email });
        return db;
    }


    protected void setupUI(){
        ((EditText)findViewById(R.id.txtEmail)).setText(email);

        RadioButton radMale = (RadioButton)findViewById(R.id.radMale);
        RadioButton radFemale = (RadioButton)findViewById(R.id.radFemale);

        if (gender.equals("Male")){
            radMale.setChecked(true);
        } else if (gender.equals("Female")){
            radFemale.setChecked(true);
        } else {
            radMale.setChecked(false);
            radFemale.setChecked(false);
        }

        CheckBox chkCoding = (CheckBox)findViewById(R.id.chkCoding);
        CheckBox chkWriting = (CheckBox)findViewById(R.id.chkWriting);
        CheckBox chkJogging = (CheckBox)findViewById(R.id.chkJogging);

        chkCoding.setChecked(false);
        chkWriting.setChecked(false);
        chkJogging.setChecked(false);

        if (hobbies.contains("Coding")) {
            chkCoding.setChecked(true);
        }

        if (hobbies.contains("Writing")) {
            chkWriting.setChecked(true);
        }

        if (hobbies.contains("Jogging")) {
            chkJogging.setChecked(true);
        }

        Resources resource = getResources();
        String[] zodiacArray = resource.getStringArray(R.array.blood);
        for(int i = 0; i < zodiacArray.length; i++){
            if(zodiacArray[i].equals(blood)){
                ((Spinner)findViewById(R.id.spinnerZodiac)).setSelection(i);
            }
        }

    }
    @Override
    protected void onDestroy() {
        sqliteHelper.close();
        super.onDestroy();
    }
}
