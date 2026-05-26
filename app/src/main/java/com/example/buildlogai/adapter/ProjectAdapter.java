package com.example.buildlogai.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.R;
import com.example.buildlogai.activities.ProjectDetailActivity;
import com.example.buildlogai.model.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Project> lista;

    public ProjectAdapter(List<Project> lista) {
        this.lista = lista;
    }

    public void updateList(List<Project> newList) {
        this.lista = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProjectName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Project project = lista.get(position);

        holder.tvProjectName.setText(project.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProjectDetailActivity.class);
            intent.putExtra("PROJECT_ID", project.getId());
            intent.putExtra("PROJECT_NAME", project.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}
