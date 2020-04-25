package com.example.caramel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.caramel.Constants.CARAMEL_DATA;
import static com.example.caramel.Constants.POSITIONS;
import static com.example.caramel.Constants.REVENUE;
import static com.example.caramel.Constants.SOLD_POSITIONS;
import static com.example.caramel.DataService.loadPositions;
import static com.example.caramel.DataService.savePositions;
import static java.lang.Math.round;


public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, Refundable, StateManager {

    SharedPreferences sharedPreferences;
    ImageButton resetBtn;
    ArrayList<Position> soldPositions = new ArrayList<>();
    ArrayList<Position> positions = new ArrayList<>();
    Button sellsBtn;
    ListView listView;
    TextView revenueText;
    HistoryAdapter adapter;
    double revenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        loadData();

        revenueText = findViewById(R.id.revenue_history);
        revenueText.setText(String.valueOf(round(revenue)));

        sellsBtn = findViewById(R.id.sells_button);
        sellsBtn.setOnClickListener(this);

        resetBtn = findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(this);

        adapter = new HistoryAdapter(this, R.layout.history_position_adapter, soldPositions);
        listView = findViewById(R.id.sold_position_list);
        listView.setAdapter(adapter);
    }

    @Override
    public void loadData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        revenue = Double.parseDouble(sharedPreferences.getString(REVENUE, "0"));
        positions = loadPositions(sharedPreferences, POSITIONS);
        soldPositions = loadPositions(sharedPreferences, SOLD_POSITIONS);
    }

    @Override
    public void saveData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        savePositions(editor, positions, POSITIONS);
        savePositions(editor, soldPositions, SOLD_POSITIONS);
        editor.putString(REVENUE, String.valueOf(revenue));

        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent toSellsIntent = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(toSellsIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sells_button:
                Intent toSellsIntent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(toSellsIntent);
                break;
            case R.id.reset_btn:
                // TODO: 25.04.2020 RESET ALL DATA
                break;
            default:
                break;
        }
    }

    private boolean refundPosition(Position positionToRefund) {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getId().equals(positionToRefund.getId())) {
                Position position = positions.get(i);
                position.setQuantity(position.getQuantity() + 1);
                positions.set(i, position);
                return true;
            }
        }
        return false;
    }

    @Override
    public void refund(int position) {
        Position positionToRefund = adapter.getItem(position);

        if (positionToRefund != null) {
            boolean isRefundSuccessful = refundPosition(positionToRefund);
            if (isRefundSuccessful) {
                revenue -= positionToRefund.getPrice();
                revenueText.setText(String.valueOf(round(revenue)));
                adapter.remove(positionToRefund);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Продажа отменена", Toast.LENGTH_LONG).show();
                saveData();
            } else {
                Toast.makeText(this, "Невозможно отменить продажу данного товара", Toast.LENGTH_LONG).show();
            }
        }
    }
}
