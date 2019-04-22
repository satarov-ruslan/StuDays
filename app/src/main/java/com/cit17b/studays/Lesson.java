package com.cit17b.studays;

import java.io.Serializable;

public class Lesson implements Serializable {

    /** По нечетным неделям */
    public static final int ODD_WEEK = 0;

    /** По четным неделям */
    public static final int EVEN_WEEK = 1;

    /** По четным и нечетным неделям */
    public static final int BOTH_WEEKS = 2;

    ////////////////////////////////////////////////

    /** ID предмета (генерируется автоматически) */
    private int id;

    /** Название */
    private String name;

    /** Аудитория */
    private String lectureHall;

    /** Часы начала */
    private int hourBeginning;

    /** Минуты начала */
    private int minuteBeginning;

    /** Часы конца */
    private int hourEnding;

    /** Минуты конца */
    private int minuteEnding;

    /** Преподаватель */
    private String lecturer;

    /** Вид занятия (лекция, практика и т.д.) */
    private String lessonType;

    /** День недели */
    private int dayOfTheWeek;

    /** Четная/нечетная неделя */
    private int oddEvenWeek;

    public Lesson() {
    }

    public Lesson(int id, String name, String lectureHall, int hourBeginning, int minuteBeginning, int hourEnding, int minuteEnding, String lecturer, String lessonType, int dayOfTheWeek, int oddEvenWeek) {
        this.id = id;
        this.name = name;
        this.lectureHall = lectureHall;
        this.hourBeginning = hourBeginning;
        this.minuteBeginning = minuteBeginning;
        this.hourEnding = hourEnding;
        this.minuteEnding = minuteEnding;
        this.lecturer = lecturer;
        this.lessonType = lessonType;
        this.dayOfTheWeek = dayOfTheWeek;
        this.oddEvenWeek = oddEvenWeek;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLectureHall() {
        return lectureHall;
    }

    public void setLectureHall(String lectureHall) {
        this.lectureHall = lectureHall;
    }

    public int getHourBeginning() {
        return hourBeginning;
    }

    public void setHourBeginning(int hourBeginning) {
        this.hourBeginning = hourBeginning;
    }

    public int getMinuteBeginning() {
        return minuteBeginning;
    }

    public void setMinuteBeginning(int minuteBeginning) {
        this.minuteBeginning = minuteBeginning;
    }

    public int getHourEnding() {
        return hourEnding;
    }

    public void setHourEnding(int hourEnding) {
        this.hourEnding = hourEnding;
    }

    public int getMinuteEnding() {
        return minuteEnding;
    }

    public void setMinuteEnding(int minuteEnding) {
        this.minuteEnding = minuteEnding;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public int getOddEvenWeek() {
        return oddEvenWeek;
    }

    public void setOddEvenWeek(int oddEvenWeek) {
        this.oddEvenWeek = oddEvenWeek;
    }
}
