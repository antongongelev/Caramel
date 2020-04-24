package com.example.caramel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.UUID;

import static com.example.caramel.DataService.loadImageFromStorage;
import static com.example.caramel.DataService.loadPositions;
import static com.example.caramel.DataService.savePositions;
import static com.example.caramel.DataService.saveToInternalStorage;

public class PositionActivity extends AppCompatActivity implements View.OnClickListener, StateManager {

    SharedPreferences sharedPreferences;
    String imageName;
    TextView tvTitle;
    Button button;
    ImageButton cameraBtn;
    ImageView positionImg;
    EditText name;
    EditText price;
    EditText quantity;
    ArrayList<Position> positions;
    Position currentPosition;
    double revenue;
    boolean isUpdateMode;
    boolean wasUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        loadData();

        currentPosition = (Position) getIntent().getSerializableExtra("currentPosition");
        imageName = currentPosition == null ? null : currentPosition.getImageName();
        isUpdateMode = currentPosition != null;

        //camera permission
        if (ContextCompat.checkSelfPermission(PositionActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PositionActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        //view binding
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        tvTitle = findViewById(R.id.title);
        button = findViewById(R.id.add_or_update_position_btn);
        cameraBtn = findViewById(R.id.camera_btn);
        positionImg = findViewById(R.id.position_img);

        //data setting
        tvTitle.setText(isUpdateMode ? "Информация о товаре" : "Новый товар");
        button.setText(isUpdateMode ? "Принять" : "Добавить");
        positionImg.setImageBitmap(isUpdateMode ? loadImageFromStorage(currentPosition.getImageName(), this) : null);
        name.setText(isUpdateMode ? currentPosition.getName() : "");
        price.setText(isUpdateMode ? String.valueOf(currentPosition.getPrice()) : "");
        quantity.setText(isUpdateMode ? String.valueOf(currentPosition.getQuantity()) : "");

        //click listening
        button.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_or_update_position_btn: {
                addOrUpdatePosition();
                break;
            }
            case R.id.camera_btn: {
                uploadPhoto();
                break;
            }
            default:
                break;
        }
    }

    private void uploadPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    //sometimes get error, because data == null
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            try {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                positionImg.setImageBitmap(capturedImage);
                //save image to storage
                imageName = saveToInternalStorage(capturedImage, getApplicationContext());
                wasUpdated = true;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void addOrUpdatePosition() {
        try {
            String id = isUpdateMode ? currentPosition.getId() : UUID.randomUUID().toString();
            String name = this.name.getText().toString();
            double price = Double.parseDouble(this.price.getText().toString());
            int quantity = Integer.parseInt(this.quantity.getText().toString());
            Position position = new Position(id, name, price, quantity, imageName);

            if (isUpdateMode && !position.equals(currentPosition)) {
                wasUpdated = true;
            }

            validateFields(position);
            validateName(position);

            if (isUpdateMode) {
                updatePosition(position);
            } else {
                addPosition(position);
            }
            saveData();

            Intent intent = new Intent(PositionActivity.this, MainActivity.class);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Пожалуйста, проверьте введенные данные", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addPosition(Position position) {
        positions.add(position);
        Toast.makeText(this, String.format("Товар \'%s\' был успешно добавлен", position.getName()), Toast.LENGTH_LONG).show();
    }

    private void updatePosition(Position position) {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getId().equals(position.getId())) {
                positions.set(i, position);
                if (wasUpdated) {
                    Toast.makeText(this, String.format("Товар \'%s\' был успешно изменен", position.getName()), Toast.LENGTH_LONG).show();
                }
                break;
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
        if (position.getQuantity() < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
    }

    private void validateName(Position position) {
        if (positions != null && positions.size() > 0) {
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).getName().equals(position.getName())) {
                    if (!isUpdateMode || !positions.get(i).getName().equals(currentPosition.getName())) {
                        throw new IllegalArgumentException("Позиция с таким названием уже существует");
                    }
                }
            }
        }
    }
}
