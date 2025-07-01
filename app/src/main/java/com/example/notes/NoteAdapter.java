package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList;
    private List<Note> filteredList;
    private OnNoteListener listener;

    public interface OnNoteListener {
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }

    public NoteAdapter(Context context, List<Note> noteList, OnNoteListener listener) {
        this.context = context;
        this.noteList = new ArrayList<>(noteList);
        this.filteredList = new ArrayList<>(noteList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = filteredList.get(position);

        holder.textTitle.setText(note.isLocked() ? "🔒 Locked Note" : note.getTitle());
        holder.textContent.setText(note.isLocked() ? "[Content protected]" : note.getContent());
        holder.textDateTime.setText(note.getFormattedTimestamp());

        // Set pinned/locked indicators
        holder.iconPin.setVisibility(note.isPinned() ? View.VISIBLE : View.GONE);
        holder.iconLock.setVisibility(note.isLocked() ? View.VISIBLE : View.GONE);

        // Set note color
        if (note.getColor() != -1) {
            holder.noteLayout.setBackgroundColor(note.getColor());
        } else {
            holder.noteLayout.setBackgroundResource(android.R.color.transparent);
        }

        // Adjust opacity for locked notes
        holder.noteLayout.setAlpha(note.isLocked() ? 0.7f : 1f);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterNotes(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(noteList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Note note : noteList) {
                if (note.getTitle().toLowerCase().contains(lowerQuery) ||
                        (!note.isLocked() && note.getContent().toLowerCase().contains(lowerQuery))) {
                    filteredList.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateNotes(List<Note> newNotes) {
        noteList.clear();
        noteList.addAll(newNotes);
        filteredList.clear();
        filteredList.addAll(newNotes);
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textContent, textDateTime;
        View iconPin, iconLock, noteLayout;

        public NoteViewHolder(@NonNull View itemView, OnNoteListener listener) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.textTitle);
            textContent = itemView.findViewById(R.id.textContent);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            iconPin = itemView.findViewById(R.id.iconPin);
            iconLock = itemView.findViewById(R.id.iconLock);
            noteLayout = itemView.findViewById(R.id.noteLayout);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNoteClick(position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNoteLongClick(position);
                }
                return true;
            });
        }
    }
}