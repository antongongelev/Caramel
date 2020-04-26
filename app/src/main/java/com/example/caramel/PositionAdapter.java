package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;
import java.util.Objects;

public class PositionAdapter extends ArrayAdapter<Position> {

    private Context context;
    private int resource;
    private Saleable saleable;

    public PositionAdapter(@NonNull Context context, int resource, @NonNull List<Position> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.saleable = (Saleable) context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = Objects.requireNonNull(getItem(position)).getName();
        double price = Objects.requireNonNull(getItem(position)).getPrice();
        int quantity = Objects.requireNonNull(getItem(position)).getQuantity();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        TextView tvName = convertView.findViewById(R.id.sell_adapter_name);
        TextView tvPrice = convertView.findViewById(R.id.sell_adapter_price);
        TextView tvQuantity = convertView.findViewById(R.id.sell_adapter_quantity);
        Button buttonSell = convertView.findViewById(R.id.sell_button);

        tvPrice.setTextColor(Color.BLACK);
        tvQuantity.setTextColor(quantity > 0 ? Color.BLUE : Color.RED);
        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSellDialog(position);
            }
        });

        tvName.setText(name);
        tvPrice.setText(String.format("%s руб/шт", price));
        tvQuantity.setText(String.format("%s шт", quantity));

        return convertView;
    }

    private void showSellDialog(final int position) {
        String positionName = getItem(position).getName();
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_mylocation)
                .setTitle("Продать товар?")
                .setMessage(String.format("Товар \'%s\' будет продан", positionName))
                .setNegativeButton("Нет", null)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saleable.sellPosition(position);
                    }
                })
                .show();
    }
}
