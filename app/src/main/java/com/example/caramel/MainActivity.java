package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Saleable {

    private ImageButton addPositionBtn;
    private ListView listView;
    static ArrayList<Position> positions = new ArrayList<>();
    private PositionAdapter adapter;
    private TextView revenueText;
    private double revenue;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populatePositionsWithTestData();

        //get data from PositionActivity
        revenue = getIntent().getDoubleExtra("revenue", 0);
        Position position = (Position) getIntent().getSerializableExtra("newPosition");
        boolean wasUpdated = getIntent().getBooleanExtra("wasUpdated", false);
        //think about this moment
        if (position != null && (isPositionUnique(position) || wasUpdated)) {
            if (wasUpdated) {
                updatePosition(position);
                Toast.makeText(this, String.format("Товар \'%s\' был успешно изменен", position.getName()), Toast.LENGTH_LONG)
                     .show();
            } else {
                positions.add(position);
                Toast.makeText(this, String.format("Товар \'%s\' был успешно добавлен", position.getName()), Toast.LENGTH_LONG)
                     .show();
            }
        }

        //UI data binding
        revenueText = findViewById(R.id.revenue);
        revenueText.setText(String.valueOf(round(revenue)));

        addPositionBtn = findViewById(R.id.add_btn);
        addPositionBtn.setOnClickListener(this);

        adapter = new PositionAdapter(this, R.layout.position_adapter, positions);
        listView = findViewById(R.id.position_list);

        //updating
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PositionActivity.class);
                intent.putExtra("positions", positions);
                intent.putExtra("revenue", revenue);
                intent.putExtra("currentPosition", (Serializable) adapter.getItem(position));
                startActivity(intent);
            }
        });

        //deletion
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Удалить товар?")
                        .setMessage("Товар будет удален из списка")
                        .setNegativeButton("Нет", null)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positions.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();
                return true;
            }
        });

        listView.setAdapter(adapter);
    }

    private void updatePosition(Position position) {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getId().equals(position.getId())) {
                positions.set(i, position);
                break;
            }
        }
    }

    //population positions with test data
    private void populatePositionsWithTestData() {
        if (positions.size() == 0) {
            positions.add(new Position(UUID.randomUUID().toString(), "Lipstick", 25.1, 4));
            positions.add(new Position(UUID.randomUUID().toString(), "Cream", 500, 7));
            positions.add(new Position(UUID.randomUUID().toString(), "Shampoo", 800.50, 10));
        }
    }

    //todo:add selling history
    //todo:restore data after minimizing application
    //todo:add ability to remove position

    private boolean isPositionUnique(Position position) {
        for (int i = 0; i < positions.size(); i++) {
            if (position.equals(positions.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("positions", positions);
        outState.putDouble("revenue", revenue);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        positions = savedInstanceState.getParcelableArrayList("positions");
        revenue = savedInstanceState.getDouble("revenue");
        revenueText.setText(String.valueOf(round(revenue)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent intent = new Intent(MainActivity.this, PositionActivity.class);
                intent.putExtra("positions", positions);
                intent.putExtra("revenue", revenue);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void sellPosition(int position) {
        Position positionToSell = adapter.getItem(position);

        if (positionToSell != null) {
            int quantityBeforeSelling = positionToSell.getQuantity();
            if (quantityBeforeSelling > 0) {

                revenue += positionToSell.getPrice();
                long roundedRevenue = round(revenue);
                revenueText.setText(String.valueOf(roundedRevenue));
                positionToSell.setQuantity(quantityBeforeSelling - 1);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, String.format("Продана позиция: %s", positionToSell.getName()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, String.format("Нет в наличии: %s", positionToSell.getName()), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
