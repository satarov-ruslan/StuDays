package com.cit17b.studays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cit17b.studays.lesson.CreateLessonActivity;
import com.cit17b.studays.lesson.Lesson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

/**
 * Класс представляет собой Fragment-страницу расписания, которая содержит занятия за неделю.
 *
 * @author Ruslan Satarov
 * @version 1.4
 */
public class TimetablePageFragment extends Fragment {

    /**
     * Параметр, использующийся для задания номера страницы в списке.
     */
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    /**
     * Идентификаторы элементов контекстного меню страницы.
     */
    public static final int CONTEXT_MENU_EDIT = 10001;
    public static final int CONTEXT_MENU_DELETE = 10002;

    /**
     * Номер страницы (играет роль учебной недели)
     */
    int pageNumber;

    /**
     * Список, содержащий занятия, что будут отображены.
     */
    private ArrayList<Lesson> dataList;

    /**
     * Layout, располагающий в себе View с занятиями.
     */
    private LinearLayout lessonList;

    /**
     * Массив с названиями дней недели.
     */
    private String[] daysOfTheWeekLabels;

    /**
     * ID выбранного занятия для контекстного меню.
     */
    private static int lessonIdSelected;

    /**
     * Объект для работы с базой данных.
     */
    private DBHelper dbHelper;

    public static TimetablePageFragment newInstance(int page) {
        Bundle args = new Bundle();
        TimetablePageFragment fragment = new TimetablePageFragment();
        args.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        dbHelper = new DBHelper(getContext());

        dataList = new ArrayList<>();
        daysOfTheWeekLabels = getResources().getStringArray(R.array.days_of_the_week);
    }

    @Override
    public void onResume() {
        fillDataListFromDB();
        fillLessonList();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable_fragment, null);
        lessonList = view.findViewById(R.id.lessonList);
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CONTEXT_MENU_EDIT, 0, getString(R.string.edit));
        menu.add(0, CONTEXT_MENU_DELETE, 0, getString(R.string.delete));
        lessonIdSelected = v.getId();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                Intent intent = new Intent(getContext(), CreateLessonActivity.class);
                intent.putExtra("requestCode", CreateLessonActivity.REQUEST_CODE_EDIT_LESSON);

                intent.putExtra("id", lessonIdSelected);

                startActivityForResult(intent, CreateLessonActivity.REQUEST_CODE_EDIT_LESSON);
                return true;
            //break;
            case CONTEXT_MENU_DELETE:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.question_delete_element);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(getString(R.string.table_lessons_name), "id = ?", new String[]{String.valueOf(lessonIdSelected)});
                        database.close();
                        dbHelper.close();
                        if (getFragmentManager() != null) {
                            for (Fragment fragment : getFragmentManager().getFragments()) {
                                getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                            }
                        }
                        fillDataListFromDB();
                        fillLessonList();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CreateLessonActivity.REQUEST_CODE_CREATE_LESSON:
                case CreateLessonActivity.REQUEST_CODE_EDIT_LESSON:
                    fillDataListFromDB();
                    fillLessonList();
                    break;
            }
        }
    }

    /**
     * Заполняет Layout элементами из списка с данными.
     */
    void fillLessonList() {
        lessonList.removeAllViews();

        LinearLayout[] daysOfTheWeekLayouts = new LinearLayout[7];

        for (int i = 0; i < daysOfTheWeekLayouts.length; i++) {
            ArrayList<Lesson> filteredByDayOfWeek = new ArrayList<>();

            for (Lesson lesson : dataList) {
                if (lesson.getOddEvenWeek() == pageNumber || lesson.getOddEvenWeek() == Lesson.BOTH_WEEKS) {
                    if (lesson.getDayOfTheWeek() == i) {
                        filteredByDayOfWeek.add(lesson);
                    }
                }
            }

            if (filteredByDayOfWeek.size() != 0) {
                TextView dayLabel = (TextView) getLayoutInflater().inflate(R.layout.day_label_item, null);
                dayLabel.setText(daysOfTheWeekLabels[i]);
                lessonList.addView(dayLabel);

                filteredByDayOfWeek.sort(new Comparator<Lesson>() {
                    @Override
                    public int compare(Lesson o1, Lesson o2) {
                        if (o1.getHourBeginning() == o2.getHourBeginning()) {
                            return o1.getMinuteBeginning() - o2.getMinuteBeginning();
                        }
                        return o1.getHourBeginning() - o2.getHourBeginning();
                    }
                });

                daysOfTheWeekLayouts[i] = new LinearLayout(getContext());
                daysOfTheWeekLayouts[i].setOrientation(LinearLayout.VERTICAL);

                for (int j = 0; j < filteredByDayOfWeek.size(); j++) {
                    Lesson lesson = filteredByDayOfWeek.get(j);
                    View lessonItem = getLayoutInflater().inflate(R.layout.lesson_item, daysOfTheWeekLayouts[i], false);
                    lessonItem.setId(lesson.getId());
                    fillLessonItem(lessonItem, lesson, j + 1);
                    registerForContextMenu(lessonItem);

                    Calendar currentTime = Calendar.getInstance();
                    int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK) - 2;
                    if (dayOfWeek < 0) dayOfWeek = 6;
                    Calendar lessonBeginning = Calendar.getInstance();
                    lessonBeginning.set(Calendar.HOUR_OF_DAY, lesson.getHourBeginning());
                    lessonBeginning.set(Calendar.MINUTE, lesson.getMinuteBeginning());
                    Calendar lessonEnding = Calendar.getInstance();
                    lessonEnding.set(Calendar.HOUR_OF_DAY, lesson.getHourEnding());
                    lessonEnding.set(Calendar.MINUTE, lesson.getMinuteEnding());

                    if (dayOfWeek == lesson.getDayOfTheWeek()
                            && currentTime.compareTo(lessonBeginning) > 0
                            && currentTime.compareTo(lessonEnding) < 0) {
                        lessonItem.setBackgroundResource(android.R.color.holo_blue_light);
                    }

                    daysOfTheWeekLayouts[i].addView(lessonItem);
                }

                lessonList.addView(daysOfTheWeekLayouts[i]);
            }
        }
    }

    /**
     * Заполняет список данными из базы данных.
     */
    void fillDataListFromDB() {
        dataList.clear();

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(getString(R.string.table_lessons_name), null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int nameColIndex = cursor.getColumnIndex("name");
            int lectureHallColIndex = cursor.getColumnIndex("lectureHall");
            int hourBeginningColIndex = cursor.getColumnIndex("hourBeginning");
            int minuteBeginningColIndex = cursor.getColumnIndex("minuteBeginning");
            int hourEndingColIndex = cursor.getColumnIndex("hourEnding");
            int minuteEndingColIndex = cursor.getColumnIndex("minuteEnding");
            int lecturerColIndex = cursor.getColumnIndex("lecturer");
            int lessonTypeColIndex = cursor.getColumnIndex("lessonType");
            int dayOfTheWeekColIndex = cursor.getColumnIndex("dayOfTheWeek");
            int oddEvenWeekColIndex = cursor.getColumnIndex("oddEvenWeek");

            do {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(idColIndex));
                lesson.setName(cursor.getString(nameColIndex));
                lesson.setLectureHall(cursor.getString(lectureHallColIndex));
                lesson.setHourBeginning(cursor.getInt(hourBeginningColIndex));
                lesson.setMinuteBeginning(cursor.getInt(minuteBeginningColIndex));
                lesson.setHourEnding(cursor.getInt(hourEndingColIndex));
                lesson.setMinuteEnding(cursor.getInt(minuteEndingColIndex));
                lesson.setLecturer(cursor.getString(lecturerColIndex));
                lesson.setLessonType(cursor.getString(lessonTypeColIndex));
                lesson.setDayOfTheWeek(cursor.getInt(dayOfTheWeekColIndex));
                lesson.setOddEvenWeek(cursor.getInt(oddEvenWeekColIndex));

                dataList.add(lesson);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        dbHelper.close();
    }

    /**
     * Заполняет View-єлемент данными из объекта.
     *
     * @param lessonItem     View для заполнения.
     * @param lesson         Занятие.
     * @param positionInList Позиция в списке.
     */
    private void fillLessonItem(View lessonItem, Lesson lesson, int positionInList) {
        ((TextView) lessonItem.findViewById(R.id.tvLessonPositionInList)).setText(String.format(Locale.getDefault(), "%d", positionInList));
        ((TextView) lessonItem.findViewById(R.id.tvLessonName)).setText(lesson.getName());
        ((TextView) lessonItem.findViewById(R.id.tvLessonLectureHall)).setText(lesson.getLectureHall());
        ((TextView) lessonItem.findViewById(R.id.tvLessonHourBeginning)).setText(String.format(Locale.getDefault(), "%02d", lesson.getHourBeginning()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonMinuteBeginning)).setText(String.format(Locale.getDefault(), "%02d", lesson.getMinuteBeginning()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonHourEnding)).setText(String.format(Locale.getDefault(), "%02d", lesson.getHourEnding()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonMinuteEnding)).setText(String.format(Locale.getDefault(), "%02d", lesson.getMinuteEnding()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonLecturer)).setText(lesson.getLecturer());
        ((TextView) lessonItem.findViewById(R.id.tvLessonType)).setText(lesson.getLessonType());
    }
}
