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
import com.example.scheduleapp.adapters.TaskAdapter;
import com.example.scheduleapp.database.DatabaseHelper;

public class PastFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private TaskAdapter adapter;
    private Cursor currentCursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past, container, false);

        recyclerView = view.findViewById(R.id.pastTasksRecyclerView);
        databaseHelper = new DatabaseHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskAdapter(null);
        recyclerView.setAdapter(adapter);

        loadPastTasks();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPastTasks();
    }

    private void loadPastTasks() {
        if (currentCursor != null) {
            currentCursor.close();
        }
        currentCursor = databaseHelper.getPastTasks();
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