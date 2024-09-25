package com.example.vyatsuapp.Utils.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vyatsuapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassViewHolder> {
    private final List<String> classes;

    public ClassesAdapter(List<String> classes) {
        this.classes = classes;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String classInfo = classes.get(position).trim();

        Pattern pairPattern = Pattern.compile("Пара: \\d+ \\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}");
        Matcher pairMatcher = pairPattern.matcher(classInfo);

        Pattern roomPattern = Pattern.compile("\\b\\d{1,2}-\\d{3}\\b");
        Matcher roomMatcher = roomPattern.matcher(classInfo);

        if (pairMatcher.find()) {
            holder.classNumber.setText(pairMatcher.group());
            classInfo = classInfo.replace(pairMatcher.group(), "").trim();
        } else {
            holder.classNumber.setText("");
        }

        // Извлечь несколько кабинетов, если они есть, и объединить их с помощью разделителя
        List<String> roomNumbers = new ArrayList<>();
        while (roomMatcher.find()) {
            roomNumbers.add(roomMatcher.group());
        }

        // Разделить кабинеты с разделителем "/" в classroomInfo
        if (!roomNumbers.isEmpty()) {
            holder.classroomInfo.setText(String.join("/\n", roomNumbers));

            // Удалить все номера кабинетов из classInfo
            for (String room : roomNumbers) {
                classInfo = classInfo.replace(room, "").trim();
            }
        } else {
            holder.classroomInfo.setText("");
        }

        // Разделить информацию о кабинете запятыми и обрабатывать случаи с несколькими кабинетами
        String[] classDetails = classInfo.split(",(?!\\s?Лабораторная работа|Лекция|Практическое занятие)"); // Split on comma but not within class types
        StringBuilder classDetailsText = new StringBuilder();
        for (String detail : classDetails) {
            classDetailsText.append(detail.trim()).append("\n"); // Add each class detail in a new line
        }

        String formattedClassDetails = classDetailsText.toString().trim().replace("_", "");
        String formattedClassDetails2 = formattedClassDetails.trim().replace("\n\n", "\n");

        // Установить данные пары (имя, тип, учитель) без информации о кабинете и подчеркиваний
        holder.classInfoTextView.setText(formattedClassDetails2.trim().replace("\n ", "\n"));
    }


    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView classNumber;
        TextView classInfoTextView;
        TextView classroomInfo;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classNumber = itemView.findViewById(R.id.classNumber);
            classInfoTextView = itemView.findViewById(R.id.classInfoTextView);
            classroomInfo = itemView.findViewById(R.id.classroomInfo);
        }
    }
}