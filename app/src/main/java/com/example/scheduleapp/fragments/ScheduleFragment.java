package com.example.scheduleapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scheduleapp.R;
import com.example.scheduleapp.adapters.TaskAdapter;
import com.example.scheduleapp.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.EditText;
import java.util.Calendar;

public class ScheduleFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTask;
    private DatabaseHelper databaseHelper;
    private TaskAdapter adapter;
    private Cursor currentCursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        
        recyclerView = view.findViewById(R.id.tasksRecyclerView);
        fabAddTask = view.findViewById(R.id.fabAddTask);
        databaseHelper = new DatabaseHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskAdapter(null);
        recyclerView.setAdapter(adapter);

        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        loadTasks();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null);
        EditText titleInput = dialogView.findViewById(R.id.editTextTitle);
        EditText descriptionInput = dialogView.findViewById(R.id.editTextDescription);

        final Calendar calendar = Calendar.getInstance();
        final int[] year = {calendar.get(Calendar.YEAR)};
        final int[] month = {calendar.get(Calendar.MONTH)};
        final int[] day = {calendar.get(Calendar.DAY_OF_MONTH)};
        final int[] hour = {calendar.get(Calendar.HOUR_OF_DAY)};
        final int[] minute = {calendar.get(Calendar.MINUTE)};

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Select Date & Time", (dialog, which) -> {
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        year[0] = selectedYear;
                        month[0] = selectedMonth;
                        day[0] = selectedDay;

                        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                            (timePicker, selectedHour, selectedMinute) -> {
                                hour[0] = selectedHour;
                                minute[0] = selectedMinute;

                                calendar.set(year[0], month[0], day[0], hour[0], minute[0]);
                                String datetime = String.format("%04d-%02d-%02d %02d:%02d:00",
                                    year[0], month[0] + 1, day[0], hour[0], minute[0]);

                                databaseHelper.addTask(title, description, datetime, "pending");
                                loadTasks();
                            }, hour[0], minute[0], true);
                        timePickerDialog.show();
                    }, year[0], month[0], day[0]);
                datePickerDialog.show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void loadTasks() {
        if (currentCursor != null) {
            currentCursor.close();
        }
        currentCursor = databaseHelper.getFutureTasks();
        adapter.swapCursor(currentCursor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentCursor != null) {
            currentCursor.close();
        }
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }
} 