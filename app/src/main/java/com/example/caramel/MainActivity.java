package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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

        //pass data from NewPositionActivity
        revenue = getIntent().getDoubleExtra("revenue", 0);
        Position position = (Position) getIntent().getSerializableExtra("newPosition");
        if (position != null && isPositionUnique(position)) {
            positions.add(position);
            Toast.makeText(this, position.getCreatedMessage(), Toast.LENGTH_LONG).show();
        }

        //UI data binding
        revenueText = findViewById(R.id.revenue);
        revenueText.setText(String.valueOf(round(revenue)));

        addPositionBtn = findViewById(R.id.add_btn);
        addPositionBtn.setOnClickListener(this);

        adapter = new PositionAdapter(this, R.layout.position_adapter, positions);
        listView = findViewById(R.id.position_list);
        listView.setAdapter(adapter);
    }

    //population positions with test data
    private void populatePositionsWithTestData() {
        if (positions.size() == 0) {
            positions.add(new Position("Lipstick", 25.1, 4));
            positions.add(new Position("Cream", 500, 7));
            positions.add(new Position("Shampoo", 800.50, 10));
        }
    }

    //todo:add selling history

    private boolean isPositionUnique(Position position) {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getName().equals(position.getName())) {
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
                Intent intent = new Intent(MainActivity.this, NewPositionActivity.class);
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
