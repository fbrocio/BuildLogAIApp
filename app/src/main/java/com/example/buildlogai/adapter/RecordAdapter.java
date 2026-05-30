package com.example.buildlogai.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.R;
import com.example.buildlogai.activities.ValidationActivity;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.StructuredData;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter
        extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<RecordDTO> records;
    private OnRecordClickListener listener;


    public RecordAdapter(List<RecordDTO> records, OnRecordClickListener listener) {
        this.records = records;
        this.listener = listener;
    }

    public void updateList(List<RecordDTO> newList) {
        this.records = newList;
        notifyDataSetChanged();
    }

    public interface OnRecordClickListener {
        void onRecordClick(RecordDTO record);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvContent;
        TextView tvState;
        TextView tvMeta;

        MaterialCardView cardRecord;

        public ViewHolder(View view) {
            super(view);

            cardRecord = view.findViewById(R.id.cardRecord);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvContent = view.findViewById(R.id.tvContent);
            tvState = view.findViewById(R.id.tvState);
            tvMeta = view.findViewById(R.id.tvMeta);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,
                                 int position) {

        RecordDTO record = records.get(position);

        // -----------------------------
        // TITLE
        // -----------------------------

        holder.tvTitle.setText(record.getTitle());

        // -----------------------------
        // TYPE COLOR
        // -----------------------------

        int color = getTypeColor(holder, record.getType());

        holder.cardRecord.setStrokeColor(color);


        // -----------------------------
        // DESCRIPTION + METADATA
        // -----------------------------

        holder.tvContent.setText(
                buildContent(record)
        );

        // -----------------------------
        // STATUS
        // -----------------------------

        holder.tvState.setText(record.getStatus());

        if ("ABIERTA".equalsIgnoreCase(record.getStatus())) {

            holder.tvState.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.red_700
                    )
            );

        } else if ("CERRADA".equalsIgnoreCase(record.getStatus())) {

            holder.tvState.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.green_700
                    )
            );

        } else {

            holder.tvState.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            android.R.color.black
                    )
            );
        }

        // -----------------------------
        // DATE
        // -----------------------------

        holder.tvMeta.setText(
                buildMeta(record)
        );

        // -----------------------------
        // OPEN VALIDATION
        // -----------------------------

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            v.getContext(),
                            ValidationActivity.class
                    );

            intent.putExtra("record", record);

            v.getContext().startActivity(intent);
        });


        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onRecordClick(record);
            }
        });
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private int getTypeColor(ViewHolder holder,
                             String type) {

        if (type == null) {

            return ContextCompat.getColor(
                    holder.itemView.getContext(),
                    android.R.color.transparent
            );
        }

        switch (type.trim().toLowerCase()) {

            case "incidencia":

                return ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.chip_incidencia
                );

            case "pendiente":

                return ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.chip_pendiente
                );

            case "avance":

                return ContextCompat.getColor(
                        holder.itemView.getContext(),
                        R.color.chip_avance
                );

            default:

                return ContextCompat.getColor(
                        holder.itemView.getContext(),
                        android.R.color.transparent
                );
        }
    }

    private String buildContent(RecordDTO record) {

        StringBuilder builder =
                new StringBuilder();

        if (record.getDescription() != null) {

            builder.append(
                    record.getDescription()
            );
        }

        StructuredData sd =
                record.getStructuredData();

        if (sd == null || sd.isEmpty()) {
            return builder.toString();
        }

        builder.append("\n");

        // COMPANY

        if (sd.getCompany() != null) {

            builder.append("\n🏢 ")
                    .append(sd.getCompany());
        }

        // SUBJECT

        if (sd.getSubject() != null) {

            builder.append("\n🔧 ")
                    .append(sd.getSubject());
        }

        // QUANTITY

        if (sd.getQuantity() != null) {

            builder.append("\n🔢 ")
                    .append(sd.getQuantity());

            if (sd.getUnit() != null) {

                builder.append(" ")
                        .append(sd.getUnit());
            }
        }

        // DUE DATE

        if (sd.getDueDate() != null) {

            builder.append("\n📅 ")
                    .append(sd.getDueDate());
        }

        // PERCENTAGE

        if (sd.getPercentage() != null) {

            builder.append("\n📈 ")
                    .append(sd.getPercentage())
                    .append("%");
        }

        // PRICE

        if (sd.getPrice() != null) {

            builder.append("\n💰 ")
                    .append(sd.getPrice())
                    .append("€");
        }

        return builder.toString();
    }

    private String buildMeta (RecordDTO record) {
        String author =
                record.getCreatedBy().getName() != null
                ?record.getCreatedBy().getName()
                        :"Usuario";
        String relativeDate =
                formatRelativeTime(record.getCreatedAt());

        return "Por " +
                author +
                " . " +
                relativeDate;
    }

    private String formatRelativeTime(String createdAt) {

        if (createdAt == null) {
            return "Sin fecha";
        }

        try {

            SimpleDateFormat parser =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.US
                    );
            parser.setTimeZone(
                    java.util.TimeZone.getTimeZone("Europe/Madrid")
            );

            Date date = parser.parse(createdAt);

            if (date == null) {
                return createdAt;
            }

            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "dd MMM · HH:mm",
                            new Locale("es", "ES")
                    );

            return formatter.format(date);

        } catch (Exception e) {

            return createdAt;
        }
    }

    @Override
    public int getItemCount() {

        return records != null
                ? records.size()
                : 0;
    }

    public List<RecordDTO> getRecords() {
        return records;
    }
}