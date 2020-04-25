package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class HistoryAdapter extends ArrayAdapter<Position> {

    private Context context;
    private int resource;
    private Refundable refundable;

    public HistoryAdapter(@NonNull Context context, int resource, @NonNull List<Position> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.refundable = (Refundable) context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = Objects.requireNonNull(getItem(position)).getName();
        double price = Objects.requireNonNull(getItem(position)).getPrice();
        String time = Objects.requireNonNull(getItem(position)).getSoldTime();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        TextView tvName = convertView.findViewById(R.id.history_adapter_name);
        TextView tvPrice = convertView.findViewById(R.id.history_adapter_price);
        TextView tvTime = convertView.findViewById(R.id.history_adapter_time);
        Button buttonRemove = convertView.findViewById(R.id.remove_button);

        tvPrice.setTextColor(Color.BLACK);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refundable.refund(position);
            }
        });

        tvName.setText(name);
        tvPrice.setText(String.format("%s руб/шт", price));
        tvTime.setText(String.format("дата: %s", time));

        return convertView;
    }
}
