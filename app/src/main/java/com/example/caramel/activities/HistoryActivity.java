package com.example.caramel.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caramel.persist.Category;
import com.example.caramel.adapters.HistoryAdapter;
import com.example.caramel.persist.Position;
import com.example.caramel.R;
import com.example.caramel.interfaces.Refundable;
import com.example.caramel.interfaces.StateManager;
import com.example.caramel.interfaces.Subscriber;

import java.util.ArrayList;

import static com.example.caramel.util.Constants.CARAMEL_DATA;
import static com.example.caramel.util.Constants.POSITIONS;
import static com.example.caramel.util.Constants.SOLD_POSITIONS;
import static com.example.caramel.util.DataService.loadPositions;
import static com.example.caramel.util.DataService.savePositions;
import static com.example.caramel.util.Utils.updatePositionsList;
import static java.lang.Math.round;


public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, Refundable, Subscriber, StateManager, PopupMenu.OnMenuItemClickListener {

    SharedPreferences sharedPreferences;
    ImageButton resetBtn;
    private Button categoryBtn;
    ArrayList<Position> soldPositions = new ArrayList<>();
    ArrayList<Position> allSoldPositions = new ArrayList<>();
    ArrayList<Position> positions = new ArrayList<>();
    Button sellsBtn;
    ListView listView;
    TextView revenueText;
    HistoryAdapter adapter;
    String revenue;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        category = Category.ALL;

        loadData();
        countRevenue();

        revenueText = findViewById(R.id.revenue_history);
        revenueText.setText(revenue);

        sellsBtn = findViewById(R.id.sells_button);
        sellsBtn.setOnClickListener(this);

        resetBtn = findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(this);

        categoryBtn = findViewById(R.id.category_btn_history);
        categoryBtn.setOnClickListener(this);

        adapter = new HistoryAdapter(this, R.layout.history_position_adapter, soldPositions);
        listView = findViewById(R.id.sold_position_list);
        listView.setAdapter(adapter);
    }

    private void countRevenue() {
        double result = 0;
        if (category.equals(Category.ALL)) {
            for (Position soldPosition : soldPositions) {
                result += soldPosition.getPrice();
            }
        } else if (category.equals(Category.KOREA)) {
            for (Position soldPosition : soldPositions) {
                if (Category.getCategoryById(soldPosition.getCategoryId()).equals(Category.KOREA)) {
                    result += soldPosition.getPrice();
                }
            }
        }
        revenue = String.valueOf(round(result));
    }

    @Override
    public void loadData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        positions = loadPositions(sharedPreferences, POSITIONS);
        ArrayList<Position> loadedSoldPositions = loadPositions(sharedPreferences, SOLD_POSITIONS);
        soldPositions.clear();
        allSoldPositions.clear();
        soldPositions.addAll(loadedSoldPositions);
        allSoldPositions.addAll(loadedSoldPositions);
    }

    @Override
    public void saveData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        savePositions(editor, positions, POSITIONS);
        savePositions(editor, allSoldPositions, SOLD_POSITIONS);

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
                resetHistoryAndRevenue();
                break;
            case R.id.category_btn_history:
                changeCategory(v);
                break;
            default:
                break;
        }
    }

    private void changeCategory(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.category_menu);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        categoryBtn.setText(item.getTitle());
        loadData();
        switch (item.getItemId()) {
            case R.id.category_all: {
                category = Category.ALL;
                break;
            }
            case R.id.category_korea: {
                category = Category.KOREA;
                updatePositionsList(soldPositions, Category.KOREA.getId());
                break;
            }
        }
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void subscribe() {
        countRevenue();
        revenueText.setText(revenue);
    }

    //reset Data
    private void resetHistoryAndRevenue() {
        new AlertDialog.Builder(HistoryActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Сбросить Данные?")
                .setMessage("История продаж и выручка будут очищены")
                .setNegativeButton("Нет", null)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        soldPositions.clear();
                        allSoldPositions.clear();
                        adapter.notifyDataSetChanged();
                        saveData();
                        Toast.makeText(HistoryActivity.this, "Данные о продажах и выручке сброшены", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
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

    private void removeSoldPositions(Position positionToRemove) {
        for (Position position : soldPositions) {
            if (position.equals(positionToRemove) && position.getSoldTime().equals(positionToRemove.getSoldTime())) {
                soldPositions.remove(position);
                break;
            }
        }
        for (Position position : allSoldPositions) {
            if (position.equals(positionToRemove) && position.getSoldTime().equals(positionToRemove.getSoldTime())) {
                allSoldPositions.remove(position);
                break;
            }
        }
    }

    @Override
    public void refund(int position) {
        Position positionToRefund = adapter.getItem(position);
        if (positionToRefund != null) {
            boolean isRefundSuccessful = refundPosition(positionToRefund);
            if (isRefundSuccessful) {
                removeSoldPositions(positionToRefund);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Продажа отменена", Toast.LENGTH_LONG).show();
                saveData();
            } else {
                Toast.makeText(this, "Невозможно отменить продажу данного товара", Toast.LENGTH_LONG).show();
            }
        }
    }
}
