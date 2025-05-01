package com.example.scheduleapp.fragments;

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
import com.example.scheduleapp.adapters.NotificationAdapter;
import com.example.scheduleapp.database.DatabaseHelper;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private NotificationAdapter adapter;
    private Cursor currentCursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        databaseHelper = new DatabaseHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(null);
        recyclerView.setAdapter(adapter);

        // Add some dummy notifications if none exist
        addDummyNotifications();
        loadNotifications();

        return view;
    }

    private void addDummyNotifications() {
        Cursor cursor = databaseHelper.getAllNotifications();
        if (cursor.getCount() == 0) {
            databaseHelper.addNotification(
                "Welcome to Schedule App!",
                "2024-03-20 10:00:00"
            );
            databaseHelper.addNotification(
                "Don't forget to add your tasks",
                "2024-03-20 10:01:00"
            );
            databaseHelper.addNotification(
                "You can switch between light and dark themes",
                "2024-03-20 10:02:00"
            );
        }
        cursor.close();
    }

    private void loadNotifications() {
        if (currentCursor != null) {
            currentCursor.close();
        }
        currentCursor = databaseHelper.getAllNotifications();
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