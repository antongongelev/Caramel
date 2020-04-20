package com.example.caramel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton addPositionBtn;
    private ListView listView;
    static ArrayList<Position> positions = new ArrayList<>();
    private PositionAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Position position = (Position) getIntent().getSerializableExtra("newPosition");
        if (position != null) {
            if (isPositionUnique(position)) {
                positions.add(position);
                Toast.makeText(this, position.getCreatedMessage(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, String.format("Позиция с именем \'%s\' уже существует!", position.getName()), Toast.LENGTH_LONG).show();
            }
        }

        addPositionBtn = findViewById(R.id.add_btn);
        addPositionBtn.setOnClickListener(this);

        adapter = new PositionAdapter(this, R.layout.position_adapter, positions);
        listView = findViewById(R.id.position_list);
        listView.setAdapter(adapter);
    }

    public static boolean isPositionUnique(final Position position) {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getName().equals(position.getName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent intent = new Intent(MainActivity.this, NewPositionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
