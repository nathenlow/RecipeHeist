package sg.edu.np.mad.recipeheist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.number.FormattedNumber;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Preference updatefrequency;
        private Preference clearupdates;
        private Preference editDefaultUpdateDate;
        private Preference clearhistory;
        private Preference logout;
        private DatePickerDialog.OnDateSetListener editDefaultUpdateDateListner;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            updatefrequency = findPreference("updatefrequency");
            clearupdates = findPreference("clearupdates");
            editDefaultUpdateDate = findPreference("defaultupdatedate");
            clearhistory = findPreference("clearhistory");
            logout = findPreference("logout");

            if (FirebaseAuth.getInstance().getCurrentUser() == null){
                updatefrequency.setEnabled(false);
                clearupdates.setEnabled(false);
                editDefaultUpdateDate.setEnabled(false);
                logout.setEnabled(false);
            }

            //get data from shared preferences
            SharedPreferences datesharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            //display date data from shared preferences in summary
            editDefaultUpdateDate.setSummary(datesharedPreferences.getString("defaultupdatedate", "2000-01-01"));

            // on clearupdates button click
            clearupdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().deleteDatabase(DataBaseHandler.DATABASE_NAME);
                    SharedPreferences chefsharedPreferences = getActivity().getSharedPreferences("chefUpdates", Context.MODE_PRIVATE);
                    SharedPreferences.Editor chefeditor = chefsharedPreferences.edit();
                    chefeditor.clear();
                    chefeditor.apply();
                    Toast.makeText(getActivity(), "Updates cleared", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            // on logout button click
            clearhistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences historySharedPreferences = getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
                    SharedPreferences.Editor historyeditor = historySharedPreferences.edit();
                    historyeditor.clear();
                    historyeditor.apply();

                    Toast.makeText(getActivity(), "History cleared", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            // on logout button click
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // logout user
                    FirebaseAuth.getInstance().signOut();
                    getActivity().deleteDatabase(DataBaseHandler.DATABASE_NAME);

                    Toast.makeText(getActivity(), "You are logout", Toast.LENGTH_SHORT).show();
                    // redirect back to main activity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    return false;
                }
            });

            //display DatePicker Dialog
            editDefaultUpdateDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Calendar cal = Calendar.getInstance();
                    int nyear = cal.get(Calendar.YEAR);
                    int nmonth = cal.get(Calendar.MONTH);
                    int nday = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getActivity(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            editDefaultUpdateDateListner,
                            nyear,nmonth,nday);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                    return false;
                }
            });

            editDefaultUpdateDateListner = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    //format data from DatePicker Dialog
                    month += 1;
                    DecimalFormat d4 = new DecimalFormat("0000");
                    DecimalFormat d2 = new DecimalFormat("00");
                    String syear = d4.format(year);
                    String smonth = d2.format(month);
                    String sday = d2.format(dayOfMonth);
                    String sdate = syear + "-" + smonth + "-" + sday;
                    //put data in shared prefs
                    SharedPreferences.Editor editor = datesharedPreferences.edit();
                    editor.putString("defaultupdatedate", sdate);
                    editor.apply();
                    //update summary
                    editDefaultUpdateDate.setSummary(datesharedPreferences.getString("defaultupdatedate", "2000-01-01"));
                }
            };

        }
    }
}