package com.example.caramel.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caramel.util.CaptureAct;
import com.example.caramel.persist.Category;
import com.example.caramel.persist.Position;
import com.example.caramel.adapters.PositionAdapter;
import com.example.caramel.R;
import com.example.caramel.interfaces.Saleable;
import com.example.caramel.interfaces.StateManager;
import com.example.caramel.interfaces.Subscriber;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import static com.example.caramel.util.Constants.CARAMEL_DATA;
import static com.example.caramel.util.Constants.CURRENT_POSITION;
import static com.example.caramel.util.Constants.POSITIONS;
import static com.example.caramel.util.Constants.SOLD_POSITIONS;
import static com.example.caramel.util.DataService.loadPositions;
import static com.example.caramel.util.DataService.savePositions;
import static com.example.caramel.util.Utils.updatePositionsList;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Saleable, StateManager, Subscriber, PopupMenu.OnMenuItemClickListener {

    SharedPreferences sharedPreferences;
    private ImageButton addPositionBtn;
    private ImageButton scannerBtn;
    private Button historyBtn;
    private Button categoryBtn;
    private ListView listView;
    static ArrayList<Position> positions = new ArrayList<>();
    static ArrayList<Position> allPositions = new ArrayList<>();
    static ArrayList<Position> soldPositions = new ArrayList<>();
    private PositionAdapter adapter;
    private TextView revenueText;
    private TextView totalText;
    private String total;
    private String revenue;
    private Category category;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        category = Category.ALL;

        loadData();
        countRevenue();
        countTotal();

        //UI data binding
        revenueText = findViewById(R.id.revenue);
        revenueText.setText(revenue);

        totalText = findViewById(R.id.total);
        totalText.setText(total);

        addPositionBtn = findViewById(R.id.add_btn);
        historyBtn = findViewById(R.id.history_button);
        scannerBtn = findViewById(R.id.scanner_btn);
        categoryBtn = findViewById(R.id.category_btn_main);

        addPositionBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        scannerBtn.setOnClickListener(this);
        categoryBtn.setOnClickListener(this);

        adapter = new PositionAdapter(this, R.layout.sell_position_adapter, positions);
        listView = findViewById(R.id.position_list);

        //updating
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PositionActivity.class);
                intent.putExtra(CURRENT_POSITION, (Serializable) adapter.getItem(position));
                startActivity(intent);
            }
        });

        //deletion
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Position positionToDelete = adapter.getItem(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Удалить товар?")
                        .setMessage("Товар будет удален из списка")
                        .setNegativeButton("Нет", null)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeById(positionToDelete.getId());
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, String.format("Товар \'%s\' успешно удален", positionToDelete.getName()), Toast.LENGTH_LONG).show();
                                saveData();
                            }
                        })
                        .show();
                return true;
            }
        });
        listView.setAdapter(adapter);
    }

    //remove position from both lists
    private void removeById(String id) {
        Iterator<Position> iteratorOne = positions.iterator();
        while (iteratorOne.hasNext()) {
            if (iteratorOne.next().getId().equals(id)) {
                iteratorOne.remove();
                break;
            }
        }
        Iterator<Position> iteratorTwo = allPositions.iterator();
        while (iteratorTwo.hasNext()) {
            if (iteratorTwo.next().getId().equals(id)) {
                iteratorTwo.remove();
                break;
            }
        }
    }

    private void countTotal() {
        double result = 0;
        for (Position position : positions) {
            result += position.getQuantity() * position.getPrice();
        }
        total = String.valueOf(round(result));
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
    public void saveData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        savePositions(editor, allPositions, POSITIONS);
        savePositions(editor, soldPositions, SOLD_POSITIONS);

        editor.apply();
    }

    @Override
    public void loadData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        ArrayList<Position> loadedPositions = loadPositions(sharedPreferences, POSITIONS);
        positions.clear();
        allPositions.clear();
        positions.addAll(loadedPositions);
        allPositions.addAll(loadedPositions);
        soldPositions = loadPositions(sharedPreferences, SOLD_POSITIONS);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent toPositionMenuIntent = new Intent(MainActivity.this, PositionActivity.class);
                startActivity(toPositionMenuIntent);
                break;
            case R.id.history_button:
                Intent toHistoryMenuIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(toHistoryMenuIntent);
                break;
            case R.id.scanner_btn:
                scanCode();
                break;
            case R.id.category_btn_main:
                changeCategory(v);
                break;
            default:
                break;
        }
    }

    //popup category menu
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
                updatePositionsList(positions, Category.KOREA.getId());
                break;
            }
        }
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void subscribe() {
        countTotal();
        countRevenue();
        totalText.setText(total);
        revenueText.setText(revenue);
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Отсканируйте штриход");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Position position = findPositionByBarcode(result.getContents());
                sellPosition(position);
            } else {
                Toast.makeText(this, "Штрихкод не найден", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Position findPositionByBarcode(String barcode) {
        for (int i = 0; i < allPositions.size(); i++) {
            if (barcode.equals(allPositions.get(i).getBarcode())) {
                return allPositions.get(i);
            }
        }
        return null;
    }

    @Override
    public void sellPosition(Position positionToSell) {
        if (positionToSell != null) {
            int quantityBeforeSelling = positionToSell.getQuantity();
            if (quantityBeforeSelling > 0) {
                positionToSell.setQuantity(quantityBeforeSelling - 1);
                positionToSell.setSoldTime(Position.getTime());
                soldPositions.add(positionToSell);
                adapter.notifyDataSetChanged();
                saveData();
                Toast.makeText(this, String.format("Продана позиция: %s", positionToSell.getName()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, String.format("Нет в наличии: %s", positionToSell.getName()), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ошибка: товар не найден", Toast.LENGTH_SHORT).show();
        }
    }
}
