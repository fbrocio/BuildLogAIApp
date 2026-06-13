package com.example.buildlogai.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.R;
import com.example.buildlogai.model.RecordDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecordValidationAdapter
        extends RecyclerView.Adapter<RecordValidationAdapter.ViewHolder> {

    private final List<RecordDTO> records;
    private final OnRecordActionListener listener;

    public interface OnRecordActionListener {
        void onAccept(RecordDTO record, int position);
        void onReject(RecordDTO record, int position);
        void onAddImage(RecordDTO record, int position);
    }

    public RecordValidationAdapter(
            List<RecordDTO> records,
            OnRecordActionListener listener
    ) {
        this.records = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_validation, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        holder.bind(records.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvType;

        private final MaterialButton btnAccept;
        private final MaterialButton btnReject;

        private final FloatingActionButton btnAddImage;

        private final ImageView imgPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);

            btnAddImage = itemView.findViewById(R.id.btnAddImage);

            imgPreview = itemView.findViewById(R.id.imgPreview);
        }

        public void bind(
                RecordDTO record,
                OnRecordActionListener listener
        ) {
            tvTitle.setText(record.getTitle());
            tvDescription.setText(record.getDescription());
            tvType.setText(record.getType());

            bindImage(record);
            bindButtons(record, listener);
        }

        private void bindImage(RecordDTO record) {

            Uri imageUri = record.getLocalImageUri();

            if (imageUri != null) {
                imgPreview.setVisibility(View.VISIBLE);
                imgPreview.setImageURI(imageUri);
            } else {
                imgPreview.setVisibility(View.GONE);
                imgPreview.setImageDrawable(null);
            }
        }

        private void bindButtons(
                RecordDTO record,
                OnRecordActionListener listener
        ) {

            btnAddImage.setOnClickListener(v -> {

                int pos = getBindingAdapterPosition();

                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {

                    listener.onAddImage(record, pos);
                }
            });

            btnAccept.setOnClickListener(v -> {

                int pos = getBindingAdapterPosition();

                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {

                    listener.onAccept(record, pos);
                }
            });

            btnReject.setOnClickListener(v -> {

                int pos = getBindingAdapterPosition();

                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {

                    listener.onReject(record, pos);
                }
            });
        }
    }
}