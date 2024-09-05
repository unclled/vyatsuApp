package com.example.vyatsuapp.Pages.Timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vyatsuapp.R;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {
    private final List<String> timetableList;

    public TimetableAdapter(List<String> timetableList) {
        this.timetableList = timetableList;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timetable, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        String timetableEntry = timetableList.get(position);
        String[] parts = timetableEntry.split("\n\n", 2); // разделяем на дату и расписание
        holder.dateTextView.setText(parts[0]); // дата
        holder.classInfoTextView.setText(parts[1]); // расписание
    }

    @Override
    public int getItemCount() {
        return timetableList.size();
    }

    public static class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView classInfoTextView;

        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            classInfoTextView = itemView.findViewById(R.id.classInfoTextView);
        }
    }
}
