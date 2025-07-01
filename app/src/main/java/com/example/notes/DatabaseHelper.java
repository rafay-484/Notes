package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2;

    // Table and columns
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_PINNED = "pinned";
    private static final String COLUMN_LOCKED = "locked";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_PIN = "pin";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NOTES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + " TEXT," +
                    COLUMN_CONTENT + " TEXT," +
                    COLUMN_TIMESTAMP + " TEXT," +
                    COLUMN_PINNED + " INTEGER DEFAULT 0," +
                    COLUMN_LOCKED + " INTEGER DEFAULT 0," +
                    COLUMN_COLOR + " INTEGER DEFAULT -1," +
                    COLUMN_PIN + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // CRUD Operations
    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_TIMESTAMP, note.getTimestamp());
        values.put(COLUMN_PINNED, note.isPinned() ? 1 : 0);
        values.put(COLUMN_LOCKED, note.isLocked() ? 1 : 0);
        values.put(COLUMN_COLOR, note.getColor());
        values.put(COLUMN_PIN, note.getPin());

        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " ORDER BY " +
                COLUMN_PINNED + " DESC, " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            int pinnedIndex = cursor.getColumnIndex(COLUMN_PINNED);
            int lockedIndex = cursor.getColumnIndex(COLUMN_LOCKED);
            int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
            int pinIndex = cursor.getColumnIndex(COLUMN_PIN);

            do {
                Note note = new Note();
                if (idIndex != -1) note.setId(cursor.getLong(idIndex));
                if (titleIndex != -1) note.setTitle(cursor.getString(titleIndex));
                if (contentIndex != -1) note.setContent(cursor.getString(contentIndex));
                if (timestampIndex != -1) note.setTimestamp(cursor.getString(timestampIndex));
                if (pinnedIndex != -1) note.setPinned(cursor.getInt(pinnedIndex) == 1);
                if (lockedIndex != -1) note.setLocked(cursor.getInt(lockedIndex) == 1);
                if (colorIndex != -1) note.setColor(cursor.getInt(colorIndex));
                if (pinIndex != -1) note.setPin(cursor.getString(pinIndex));

                notes.add(note);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return notes;
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_TIMESTAMP, note.getTimestamp());
        values.put(COLUMN_PINNED, note.isPinned() ? 1 : 0);
        values.put(COLUMN_LOCKED, note.isLocked() ? 1 : 0);
        values.put(COLUMN_COLOR, note.getColor());
        values.put(COLUMN_PIN, note.getPin());

        db.update(TABLE_NOTES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void deleteNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Note> searchNotes(String query) {
        List<Note> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_TITLE + " LIKE ? OR " + COLUMN_CONTENT + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query(TABLE_NOTES, null, selection, selectionArgs,
                null, null, COLUMN_PINNED + " DESC, " + COLUMN_TIMESTAMP + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            int pinnedIndex = cursor.getColumnIndex(COLUMN_PINNED);
            int lockedIndex = cursor.getColumnIndex(COLUMN_LOCKED);
            int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
            int pinIndex = cursor.getColumnIndex(COLUMN_PIN);

            do {
                Note note = new Note();
                if (idIndex != -1) note.setId(cursor.getLong(idIndex));
                if (titleIndex != -1) note.setTitle(cursor.getString(titleIndex));
                if (contentIndex != -1) note.setContent(cursor.getString(contentIndex));
                if (timestampIndex != -1) note.setTimestamp(cursor.getString(timestampIndex));
                if (pinnedIndex != -1) note.setPinned(cursor.getInt(pinnedIndex) == 1);
                if (lockedIndex != -1) note.setLocked(cursor.getInt(lockedIndex) == 1);
                if (colorIndex != -1) note.setColor(cursor.getInt(colorIndex));
                if (pinIndex != -1) note.setPin(cursor.getString(pinIndex));

                results.add(note);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return results;
    }
}

