package com.example.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteListener {
    RecyclerView recyclerView;
    NoteAdapter adapter;
    public static ArrayList<Note> noteList = new ArrayList<>();
    int editingIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
       recyclerView = findViewById(R.id.recyclerView);
       findViewById(R.id.btnAddNote).setOnClickListener(v -> showNoteDialog(null, -1));
       adapter = new NoteAdapter(this, noteList, this);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);


    }
    private void showNoteDialog(Note existingNote, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_note, null);
        builder.setView(dialogView);

        EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        EditText inputContent = dialogView.findViewById(R.id.inputContent);

        if (existingNote != null) {
            inputTitle.setText(existingNote.getTitle());
            inputContent.setText(existingNote.getContent());
        }
        builder.setTitle(existingNote == null ? "Add Note" : "Edit Note")
                .setPositiveButton("Save", (dialog, which) ->{
                    String title = inputTitle.getText().toString().trim();
                    String content = inputContent.getText().toString().trim();

                    if(title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(this,"Title and Content cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (existingNote == null) {
                        noteList.add(new Note(title, content));
                    }
                    else {
                        existingNote.setTitle(title);
                        existingNote.setContent(content);
                        noteList.set(position, existingNote);
                    }

                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }
    @Override
    public void onNoteClick(int position) {
        showNoteDialog(noteList.get(position), position);

    }
    @Override
    public void onNoteLongClick(int position) {
        noteList.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(this, "Note Deleted",Toast.LENGTH_SHORT).show();
    }
}