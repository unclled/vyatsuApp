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
        String timetableEntry = String.valueOf(timetableList.get(position));
        String[] parts = timetableEntry.split("\n\n", 2); // разделяем на дату и расписание

        // Проверяем, есть ли у нас обе части: дата и расписание.
        if (parts.length == 2) {
            holder.dateTextView.setText(parts[0]); // Дата
            holder.classInfoTextView.setText(parts[1]); // Расписание
        } else {
            holder.dateTextView.setText(parts[0]); // Только дата
            holder.classInfoTextView.setText(""); // Пустое расписание, если второй части нет
        }
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
