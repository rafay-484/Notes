package com.example.notes;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<Note> noteList;
    private OnNoteListener listener;

    public interface OnNoteListener{
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }
    public NoteAdapter(Context context, ArrayList<Note> noteList, OnNoteListener listener) {
        this.context = context;
        this.noteList = noteList;
        this.listener = listener;
    }
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view, listener);

    }
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.textTitle.setText(note.getTitle());
        holder.textContent.setText(note.getContent());
    }
    @Override
    public int getItemCount() {
        return noteList.size();
    }
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textContent;

        public NoteViewHolder(View itemView, final OnNoteListener listener) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textContent = itemView.findViewById(R.id.textContent);

            itemView.setOnClickListener(v -> listener.onNoteClick(getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                listener.onNoteLongClick(getAdapterPosition());
                return true;
            });

        }
    }
}
