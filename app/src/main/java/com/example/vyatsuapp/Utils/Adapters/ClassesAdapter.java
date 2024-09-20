package com.example.vyatsuapp.Utils.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vyatsuapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassViewHolder> {
    private final List<String> classes;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public ClassesAdapter(List<String> classes, String date) {
        this.classes = filterClassesForToday(classes, date);
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

        Pattern roomPattern = Pattern.compile("\\b\\d{1,2}-(\\d|[а-я]){3,4}\\b");
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
        String[] classDetails = classInfo.split(",(?!\\s?Лабораторная работа|Лекция|Практическое занятие)");
        StringBuilder classDetailsText = new StringBuilder();
        for (String detail : classDetails) {
            classDetailsText.append(detail.trim()).append("\n");
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

    private List<String> filterClassesForToday(List<String> classes, String date) {
        List<String> filteredClasses = new ArrayList<>();
        Calendar currentTime = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        try {
            today.setTime(Objects.requireNonNull(dateFormat.parse(dateFormat.format(currentTime.getTime()))));

            for (String classInfo : classes) {
                // Проверяем, если дата в расписании совпадает с сегодняшним днём
                if (isToday(date, dateFormat)) {
                    // Извлекаем время окончания пары
                    Pattern pairPattern = Pattern.compile("Пара: \\d+ \\d{1,2}:\\d{2}-(\\d{1,2}:\\d{2})");
                    Matcher matcher = pairPattern.matcher(classInfo);

                    if (matcher.find()) {
                        String endTime = matcher.group(2);  // Время окончания пары
                        try {
                            Calendar classEndTime = Calendar.getInstance();
                            if (endTime != null) {
                                classEndTime.setTime(Objects.requireNonNull(timeFormat.parse(endTime)));  // Парсим время окончания
                            }

                            // Если время окончания больше текущего времени, добавляем пару в список
                            if (classEndTime.after(currentTime)) {
                                filteredClasses.add(classInfo);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    filteredClasses.add(classInfo);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return filteredClasses;
    }

    private boolean isToday(String classDate, SimpleDateFormat dateFormat) {
        try {
            Calendar classCalendar = Calendar.getInstance();
            classCalendar.setTime(Objects.requireNonNull(dateFormat.parse(classDate)));  // Парсим дату пары

            Calendar today = Calendar.getInstance();
            today.setTime(Objects.requireNonNull(dateFormat.parse(dateFormat.format(Calendar.getInstance().getTime()))));

            return classCalendar.equals(today);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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