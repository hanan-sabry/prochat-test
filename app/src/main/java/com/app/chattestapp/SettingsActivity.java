package com.app.chattestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.availableTimeLimitSwitch)
    SwitchCompat availableTimeLimitSwitch;
    @BindView(R.id.isAvailableSwitch)
    SwitchCompat isAvailableSwitch;
    @BindView(R.id.availablefrom)
    TextView availablefrom;
    @BindView(R.id.availableTo)
    TextView availableTo;

    boolean isFromClicked = false;
    boolean isToClicked = false;

    private User currentUser;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        ButterKnife.bind(this);

        currentUser = getIntent().getParcelableExtra("USER1");

        //get current settings
        usersRef.child(currentUser.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                boolean timeLimit = user.isAvailable_limit();
                boolean available = user.isAvailable();
                long from = user.getAvailable_from();
                long to = user.getAvailable_to();

                availableTimeLimitSwitch.setChecked(timeLimit);
                isAvailableSwitch.setChecked(available);
                CharSequence time = android.text.format.DateFormat.format("HH:mm", from);
                availablefrom.setText("Alarm set for: " + (android.text.format.DateFormat.format("hh:mm a", from)));
                availableTo.setText("Alarm set for: " + (android.text.format.DateFormat.format("hh:mm a", to)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        availableTimeLimitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //get user in database
                Map<String, Object> updates = new HashMap<>();
                updates.put("available_limit", isChecked);
                usersRef.child(currentUser.getId()).updateChildren(updates);
            }
        });
    }

    @OnClick(R.id.availablefrom)
    public void onSetFromTime() {
        isFromClicked = true;
        isToClicked = false;
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @OnClick(R.id.availableTo)
    public void onSetToTime() {
        isToClicked = true;
        isFromClicked = false;
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        //update in database - to do
        if (isFromClicked && !isToClicked) {
            updateTimeText(c, availablefrom);
            updateFromTimeInDB(c);
            startAvailableAlarm(c);
        }
        if (!isFromClicked && isToClicked) {
            updateTimeText(c, availableTo);
            updateToTimeInDB(c);
            startNotAvailableAlarm(c);
        }
    }

    private void updateFromTimeInDB(Calendar c) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("available_from", c.getTimeInMillis());
        usersRef.child(currentUser.getId()).updateChildren(updates);
    }

    private void updateToTimeInDB(Calendar c) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("available_to", c.getTimeInMillis());
        usersRef.child(currentUser.getId()).updateChildren(updates);
    }

    private void updateTimeText(Calendar c, TextView mTextView) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        mTextView.setText(timeText);
    }

    private void startAvailableAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AvailableAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    private void startNotAvailableAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotAvailableAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AvailableAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}