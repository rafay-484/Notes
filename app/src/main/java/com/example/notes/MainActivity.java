package com.example.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteListener {
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Note> notesList;
    private boolean darkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load notes
        notesList = new ArrayList<>();
        loadNotes();

        // Setup adapter
        adapter = new NoteAdapter(this, notesList, this);
        recyclerView.setAdapter(adapter);

        // Setup search view
        setupSearchView();

        // Add note FAB
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> showNoteDialog(null));
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);

        // Make sure the hint is visible
        int searchHintId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchHint = searchView.findViewById(searchHintId);
        if (searchHint != null) {
            searchHint.setTextColor(getResources().getColor(android.R.color.black));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterNotes(newText);
                return true;
            }
        });

        // Force show the keyboard when search view is focused
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchView.setIconified(false);
            }
        });
    }

    private void loadNotes() {
        new Thread(() -> {
            List<Note> loadedNotes = dbHelper.getAllNotes();
            runOnUiThread(() -> {
                notesList.clear();
                notesList.addAll(loadedNotes);
                adapter.updateNotes(notesList);
            });
        }).start();
    }

    private void showNoteDialog(Note existingNote) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_note, null);
        builder.setView(view);

        EditText etTitle = view.findViewById(R.id.inputTitle);
        EditText etContent = view.findViewById(R.id.inputContent);
        CheckBox cbPin = view.findViewById(R.id.checkPin);
        CheckBox cbLock = view.findViewById(R.id.checkLock);

        if (existingNote != null) {
            etTitle.setText(existingNote.getTitle());
            etContent.setText(existingNote.getContent());
            cbPin.setChecked(existingNote.isPinned());
            cbLock.setChecked(existingNote.isLocked());
        }

        builder.setTitle(existingNote == null ? "Add Note" : "Edit Note")
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    boolean pinned = cbPin.isChecked();
                    boolean locked = cbLock.isChecked();

                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(this, "Title and content required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Note note = existingNote != null ? existingNote : new Note();
                    note.setTitle(title);
                    note.setContent(content);
                    note.setPinned(pinned);
                    note.setLocked(locked);

                    if (locked) {
                        showPinDialog(note, existingNote == null);
                    } else {
                        saveNote(note, existingNote == null);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPinDialog(Note note, boolean isNew) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pin, null);
        builder.setView(view);

        EditText etPin = view.findViewById(R.id.inputPin);
        TextView tvAttempts = view.findViewById(R.id.txtAttempts);

        builder.setTitle(isNew ? "Set PIN" : "Update PIN")
                .setPositiveButton("OK", (dialog, which) -> {
                    String pin = etPin.getText().toString();
                    if (pin.length() != 3) {
                        Toast.makeText(this, "PIN must be 3 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    note.setPin(pin);
                    saveNote(note, isNew);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveNote(Note note, boolean isNew) {
        new Thread(() -> {
            if (isNew) {
                dbHelper.addNote(note);
            } else {
                dbHelper.updateNote(note);
            }
            runOnUiThread(() -> {
                loadNotes();
                Toast.makeText(this, "Note " + (isNew ? "added" : "updated"), Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public void onNoteClick(int position) {
        Note note = notesList.get(position);
        if (note.isLocked()) {
            showPinVerificationDialog(note, position);
        } else {
            showNoteDialog(note);
        }
    }

    private void showPinVerificationDialog(Note note, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pin, null);
        builder.setView(view);

        EditText etPin = view.findViewById(R.id.inputPin);
        TextView tvAttempts = view.findViewById(R.id.txtAttempts);
        final int[] attempts = {3};
        tvAttempts.setText("Attempts left: " + attempts[0]);

        AlertDialog dialog = builder.setTitle("Enter PIN")
                .setPositiveButton("Verify", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnVerify = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnVerify.setOnClickListener(v -> {
                String enteredPin = etPin.getText().toString();
                if (enteredPin.length() != 3) {
                    Toast.makeText(this, "PIN must be 3 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (enteredPin.equals(note.getPin())) {
                    note.setLocked(false);
                    dbHelper.updateNote(note);
                    showNoteDialog(note);
                    dialog.dismiss();
                } else {
                    attempts[0]--;
                    tvAttempts.setText("Attempts left: " + attempts[0]);
                    if (attempts[0] <= 0) {
                        Toast.makeText(this, "No attempts left", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
                        etPin.setText("");
                    }
                }
            });
        });
        dialog.show();
    }

    @Override
    public void onNoteLongClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        dbHelper.deleteNote(notesList.get(position).getId());
                        runOnUiThread(() -> {
                            loadNotes();
                            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            darkMode = !darkMode;
            AppCompatDelegate.setDefaultNightMode(
                    darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}