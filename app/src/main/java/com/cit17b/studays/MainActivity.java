package com.cit17b.studays;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

import com.cit17b.studays.lesson.CreateLessonActivity;
import com.cit17b.studays.note.NoteListActivity;

import java.util.List;

/**
 * Этот класс является главным Activity, которое вызывается при запуске приложения.
 * Класс отображает расписание занятий.
 *
 * @author Ruslan Satarov
 * @version 1.2
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * Константа указывает количество отображаемых страниц расписания.
     * Должна оставаться равной 2, так как расписание основано на системе
     * четных-нечетных недель.
     */
    private static final int PAGE_COUNT = 2;

    /**
     * Объект используется для отображения страниц расписания.
     */
    private ViewPager timetablePager;

    /**
     * Объект используется для генерации страниц для ViewPager.
     */
    private FragmentPagerAdapter pagerAdapter;

    /**
     * Полоса вкладок, позволяющая переключаться между страницами.
     */
    private PagerTabStrip timetablePagerTabStrip;

    /**
     * Кнопка вызова меню создания занятия.
     */
    private FloatingActionButton createLessonButton;

    /**
     * Кнопка, открывающая список заметок.
     */
    private FloatingActionButton noteButton;

    /**
     * Вызывается при создании Activity.
     *
     * @param savedInstanceState Если Activity было заново инициализировано после того, как
     *                           было закрыто, тогда этот Bundle содержит, которые он получил
     *                           в onSaveInstanceState. В другом случае это null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timetablePager = findViewById(R.id.timetablePager);
        pagerAdapter = new TimetableFragmentPagerAdapter(getSupportFragmentManager());
        timetablePager.setAdapter(pagerAdapter);
        timetablePagerTabStrip = timetablePager.findViewById(R.id.timetablePagerTabStrip);

        timetablePagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        createLessonButton = findViewById(R.id.createLessonButton);
        createLessonButton.setOnClickListener(this);

        noteButton = findViewById(R.id.noteButton);
        noteButton.setOnClickListener(this);
    }

    /**
     * Вызывается, когда View было нажато.
     *
     * @param v View, которое было нажато.
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.createLessonButton:
                intent = new Intent(this, CreateLessonActivity.class);
                intent.putExtra("requestCode", CreateLessonActivity.REQUEST_CODE_CREATE_LESSON);
                startActivityForResult(intent, CreateLessonActivity.REQUEST_CODE_CREATE_LESSON);
                break;
            case R.id.noteButton:
                intent = new Intent(this, NoteListActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Вызывается, когда вызванное Activity завершает работу, давая requestCode, с которым оно
     * было вызвано, resultCode и, возможно, дополнительные данные.
     *
     * @param requestCode Код, с которым было вызвано Activity.
     * @param resultCode Код, идентифицирующий результат работы дочернего Activity.
     * @param data Intent, который может содержать результирующие данные.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CreateLessonActivity.REQUEST_CODE_CREATE_LESSON:
                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    for (Fragment fragment : fragments) {
                        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                    }
                    break;
            }
        }
    }

    /**
     * Класс, представляющий собственную реализацию абстрактного класса FragmentPagerAdapter.
     * Используется для генерации страниц расписания.
     */
    private class TimetableFragmentPagerAdapter extends FragmentPagerAdapter {

        /**
         * Массив с заголовками страниц.
         */
        String[] titles = getResources().getStringArray(R.array.timetable_week_labels);

        /**
         * Конструктор, принимающий FragmentManager.
         *
         * @param fm FragmentManager.
         */
        public TimetableFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        /**
         * Возвращает заговолок страницы по ее номеру в списке.
         *
         * @param position Номер страницы в списке.
         *
         * @return Заголовок страницы.
         */
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position <= titles.length) {
                return titles[position];
            }
            return super.getPageTitle(position);
        }

        /**
         * Возвращает Fragment-страницу по ее номеру в списке.
         *
         * @param position Номер страницы в списке.
         *
         * @return Fragment-страница.
         */
        @Override
        public Fragment getItem(int position) {
            return TimetablePageFragment.newInstance(position);
        }

        /**
         * Возвращает количество страниц в списке.
         *
         * @return Количество страниц в списке.
         */
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}
