package com.example.caramel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static java.lang.Math.round;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, Removable {

    double revenue;
    ArrayList<Position> soldPositions = new ArrayList<>();
    Button sellsBtn;
    ListView listView;
    TextView revenueText;
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //get data from MainActivity
        revenue = getIntent().getDoubleExtra("revenue", 0);
        soldPositions = (ArrayList<Position>) getIntent().getSerializableExtra("soldPositions");

        revenueText = findViewById(R.id.revenue_history);
        revenueText.setText(String.valueOf(round(revenue)));

        sellsBtn = findViewById(R.id.sells_button);
        sellsBtn.setOnClickListener(this);

        adapter = new HistoryAdapter(this, R.layout.history_position_adapter, soldPositions);
        listView = findViewById(R.id.sold_position_list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void removePosition(int position) {

    }
}
