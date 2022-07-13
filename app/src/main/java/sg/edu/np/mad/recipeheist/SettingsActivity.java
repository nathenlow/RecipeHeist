package sg.edu.np.mad.recipeheist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.number.FormattedNumber;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }






    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Preference editDefaultUpdateDate;
        private DatePickerDialog.OnDateSetListener editDefaultUpdateDateListner;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            editDefaultUpdateDate = findPreference("defaultupdatedate");

            //get data from shared preferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            //display date data from shared preferences in summary
            editDefaultUpdateDate.setSummary(sharedPreferences.getString("defaultupdatedate", "2000-01-01"));

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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("defaultupdatedate", sdate);
                    editor.apply();
                    //update summary
                    editDefaultUpdateDate.setSummary(sharedPreferences.getString("defaultupdatedate", "2000-01-01"));
                }
            };
        }
    }
}