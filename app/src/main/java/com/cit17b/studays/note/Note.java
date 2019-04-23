package com.cit17b.studays.note;

/**
 * Класс представляет собой воплощение сущности "Заметка".
 *
 * @author Ruslan Satarov
 * @version 1.0
 */
public class Note {

    /**
     * ID заметки.
     */
    private int id;

    /**
     * Название.
     */
    private String title;

    /**
     * Текст.
     */
    private String noteText;

    public Note() {
    }

    public Note(int id, String title, String noteText) {
        this.id = id;
        this.title = title;
        this.noteText = noteText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    @Override
    public String toString() {
        return title;
    }
}
