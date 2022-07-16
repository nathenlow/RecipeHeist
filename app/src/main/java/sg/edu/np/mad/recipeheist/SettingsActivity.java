package sg.edu.np.mad.recipeheist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.Calendar;

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

        private ListPreference theme;
        private ListPreference updatefrequency;
        private Preference clearupdates;
        private Preference editDefaultUpdateDate;
        private Preference clearhistory;
        private Preference logout;
        private DatePickerDialog.OnDateSetListener editDefaultUpdateDateListner;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            theme = findPreference("theme");
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

            //on theme change --> display Toast message
            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), "Restart app to apply changes", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            updatefrequency.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), "Restart device to apply changes", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

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

        public void setAlarm(int numhours) {
            Calendar calendar = Calendar.getInstance();
            Intent intent0 = new Intent(getActivity(), UpdateService.class);
            PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent0, 0);
            AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pintent);
        }

        public void removeAlarm(){
            Intent intent0 = new Intent(getActivity(), UpdateService.class);
            PendingIntent sender = PendingIntent.getService(getActivity(), 0, intent0, 0);
            AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            if (alarm != null) {
                alarm.cancel(sender);
            }
        }
    }
}
