package com.example.caramel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

public class NewPositionActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText price;
    EditText quantity;
    List<Position> positions;
    double revenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_position);

        positions = (List<Position>) getIntent().getSerializableExtra("positions");
        revenue = getIntent().getDoubleExtra("revenue", 0);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);

        Button button = findViewById(R.id.add_position_btn);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_position_btn) {

            try {
                String name = this.name.getText().toString();
                double price = Double.parseDouble(this.price.getText().toString());
                int quantity = Integer.parseInt(this.quantity.getText().toString());

                Position position = new Position(name, price, quantity);
                validateFields(position);
                validateName(position);

                Intent intent = new Intent(NewPositionActivity.this, MainActivity.class);
                intent.putExtra("newPosition", (Serializable) position);
                intent.putExtra("revenue", revenue);

                startActivity(intent);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Пожалуйста, проверьте введенные данные", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateFields(Position position) {
        if (position.getName().length() == 0) {
            throw new IllegalArgumentException("Название не может быть пустое");
        }
        if (position.getPrice() < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        if (position.getQuantity() < 1) {
            throw new IllegalArgumentException("Количество должно быть, как минимум, 1");
        }
    }

    private void validateName(Position position) {
        if (positions != null && positions.size() > 0) {
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).getName().equals(position.getName())) {
                    throw new IllegalArgumentException("Позиция с таким названием уже существует");
                }
            }
        }
    }
}
