package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.caramel.DataService.loadPositions;
import static com.example.caramel.DataService.savePositions;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Saleable, StateManager {

    SharedPreferences sharedPreferences;
    private ImageButton addPositionBtn;
    private Button historyBtn;
    private ListView listView;
    static ArrayList<Position> positions = new ArrayList<>();
    static ArrayList<Position> soldPositions = new ArrayList<>();
    private PositionAdapter adapter;
    private TextView revenueText;
    private double revenue;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();
        populatePositionsWithTestData();

        //UI data binding
        revenueText = findViewById(R.id.revenue);
        revenueText.setText(String.valueOf(round(revenue)));

        addPositionBtn = findViewById(R.id.add_btn);
        historyBtn = findViewById(R.id.history_button);
        addPositionBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);

        adapter = new PositionAdapter(this, R.layout.sell_position_adapter, positions);
        listView = findViewById(R.id.position_list);

        //updating
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PositionActivity.class);
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
                                saveData();
                            }
                        })
                        .show();
                return true;
            }
        });

        listView.setAdapter(adapter);
    }

    @Override
    public void saveData() {
        sharedPreferences = getSharedPreferences("Caramel_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        savePositions(editor, positions);
        editor.putString("revenue", String.valueOf(revenue));

        editor.apply();
    }

    @Override
    public void loadData() {
        sharedPreferences = getSharedPreferences("Caramel_data", MODE_PRIVATE);
        String revenue = sharedPreferences.getString("revenue", "0");
        positions = loadPositions(sharedPreferences);
        this.revenue = Double.parseDouble(revenue);
    }

    //population positions with test data
    private void populatePositionsWithTestData() {
        if (positions.size() == 0) {
            positions.add(new Position(UUID.randomUUID().toString(), "Lipstick", 25.1, 4));
            positions.add(new Position(UUID.randomUUID().toString(), "Cream", 500, 7));
            positions.add(new Position(UUID.randomUUID().toString(), "Shampoo", 800.50, 10));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent toPositionMenuIntent = new Intent(MainActivity.this, PositionActivity.class);
                toPositionMenuIntent.putExtra("positions", positions);
                toPositionMenuIntent.putExtra("revenue", revenue);
                startActivity(toPositionMenuIntent);
                break;
            case R.id.history_button:
                Intent toHistoryMenuIntent = new Intent(MainActivity.this, HistoryActivity.class);
                toHistoryMenuIntent.putExtra("soldPositions", soldPositions);
                toHistoryMenuIntent.putExtra("revenue", revenue);
                startActivity(toHistoryMenuIntent);
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

                //add to history
                positionToSell.setSoldTime(Position.getTime());
                soldPositions.add(positionToSell);

                adapter.notifyDataSetChanged();
                saveData();

                Toast.makeText(this, String.format("Продана позиция: %s", positionToSell.getName()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, String.format("Нет в наличии: %s", positionToSell.getName()), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
