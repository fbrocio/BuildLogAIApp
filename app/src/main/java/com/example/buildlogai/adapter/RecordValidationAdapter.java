package com.example.buildlogai.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.R;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.StructuredData;
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

    public RecordValidationAdapter(List<RecordDTO> records,
                                   OnRecordActionListener listener) {
        this.records = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_validation, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        holder.bind(records.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvType;

        LinearLayout layoutMetadata;
        View layoutMetadataContainer;

        ImageButton btnRemoveMetadata;

        MaterialButton btnAccept, btnReject;

        FloatingActionButton btnAddImage;

        ImageView imgPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);

            layoutMetadata = itemView.findViewById(R.id.layoutMetadata);
            layoutMetadataContainer =
                    itemView.findViewById(R.id.layoutMetadataContainer);

            btnRemoveMetadata =
                    itemView.findViewById(R.id.btnRemoveMetadata);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);

            btnAddImage = itemView.findViewById(R.id.btnAddImage);

            imgPreview = itemView.findViewById(R.id.imgPreview);
        }

        public void bind(RecordDTO record,
                         OnRecordActionListener listener) {

            tvTitle.setText(record.getTitle());
            tvDescription.setText(record.getDescription());
            tvType.setText(record.getType());

            bindStructuredData(record);

            bindImage(record);

            bindButtons(record, listener);
        }

        private void bindStructuredData(RecordDTO record) {

            layoutMetadata.removeAllViews();

            StructuredData sd = record.getStructuredData();

            if (sd == null || isStructuredDataEmpty(sd)) {
                layoutMetadataContainer.setVisibility(View.VISIBLE);
                return;
            }

            layoutMetadataContainer.setVisibility(View.VISIBLE);

            addMetadataIfNotNull("Empresa", sd.getCompany());

            addMetadataIfNotNull("Asunto", sd.getSubject());

            if (sd.getQuantity() != null) {

                String quantityText =
                        sd.getQuantity() +
                                (sd.getUnit() != null
                                        ? " " + sd.getUnit()
                                        : "");

                addMetadataIfNotNull("Cantidad", quantityText);
            }

            addMetadataIfNotNull("Fecha", sd.getDueDate());

            if (sd.getPercentage() != null) {
                addMetadataIfNotNull(
                        "Progreso",
                        sd.getPercentage() + "%"
                );
            }

            if (sd.getPrice() != null) {
                addMetadataIfNotNull(
                        "Precio",
                        sd.getPrice() + "€"
                );
            }
        }

        private void addMetadataIfNotNull(String label,
                                          String value) {

            if (value == null || value.trim().isEmpty()) {
                return;
            }

            View item = LayoutInflater.from(itemView.getContext())
                    .inflate(
                            R.layout.item_metadata,
                            layoutMetadata,
                            false
                    );

            TextView tvLabel =
                    item.findViewById(R.id.tvLabel);

            TextView tvValue =
                    item.findViewById(R.id.tvValue);

            TextView btnRemove =
                    item.findViewById(R.id.btnRemoveField);

            tvLabel.setText(label);
            tvValue.setText(value);

            btnRemove.setOnClickListener(v -> {
                layoutMetadata.removeView(item);

                if (layoutMetadata.getChildCount() == 0) {
                    layoutMetadataContainer.setVisibility(View.GONE);
                }
            });

            layoutMetadata.addView(item);
        }

        private boolean isStructuredDataEmpty(StructuredData sd) {

            return sd.getCompany() == null
                    && sd.getSubject() == null
                    && sd.getQuantity() == null
                    && sd.getUnit() == null
                    && sd.getDueDate() == null
                    && sd.getPercentage() == null
                    && sd.getPrice() == null;
        }

        private void bindImage(RecordDTO record) {

            Uri imageUri = record.getLocalImageUri();

            if (imageUri != null) {
                imgPreview.setVisibility(View.VISIBLE);
                imgPreview.setImageURI(imageUri);
            } else {
                imgPreview.setVisibility(View.GONE);
            }
        }

        private void bindButtons(RecordDTO record,
                                 OnRecordActionListener listener) {

            btnAddImage.setOnClickListener(v -> {

                int pos = getAdapterPosition();

                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {

                    listener.onAddImage(record, pos);
                }
            });

            btnAccept.setOnClickListener(v -> {

                int pos = getAdapterPosition();

                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {

                    listener.onAccept(record, pos);
                }
            });

            btnReject.setOnClickListener(v -> {

                int pos = getAdapterPosition();

                if (listener != null
                        && pos != RecyclerView.NO_POSITION) {

                    listener.onReject(record, pos);
                }
            });

            btnRemoveMetadata.setOnClickListener(v -> {
                layoutMetadataContainer.setVisibility(View.GONE);
            });
        }
    }
}