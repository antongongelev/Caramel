package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class PositionAdapter extends ArrayAdapter<Position> {

    private Context context;
    private int resource;

    public PositionAdapter(@NonNull Context context, int resource, @NonNull List<Position> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = Objects.requireNonNull(getItem(position)).getName();
        double price = Objects.requireNonNull(getItem(position)).getPrice();
        int quantity = Objects.requireNonNull(getItem(position)).getQuantity();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        TextView tvName = convertView.findViewById(R.id.adapter_name);
        TextView tvPrice = convertView.findViewById(R.id.adapter_price);
        TextView tvQuantity = convertView.findViewById(R.id.adapter_quantity);

        tvName.setText(name);
        tvPrice.setText(String.format("%s руб/шт", price));
        tvQuantity.setText(String.format("%s шт", quantity));

        return convertView;
    }
}
