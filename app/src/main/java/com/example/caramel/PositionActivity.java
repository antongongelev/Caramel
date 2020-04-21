package com.example.caramel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PositionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTitle;
    Button button;
    ImageButton cameraBtn;
    ImageView positionImg;
    EditText name;
    EditText price;
    EditText quantity;
    List<Position> positions;
    Position currentPosition;
    double revenue;
    boolean isUpdateMode;
    boolean wasUpdated;

    //todo: check if we receive current position and in this case toggle isUpdateMode ???
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        currentPosition = (Position) getIntent().getSerializableExtra("currentPosition");
        positions = (List<Position>) getIntent().getSerializableExtra("positions");
        revenue = getIntent().getDoubleExtra("revenue", 0);
        isUpdateMode = currentPosition != null;

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        tvTitle = findViewById(R.id.title);
        button = findViewById(R.id.add_or_update_position_btn);
        cameraBtn = findViewById(R.id.camera_btn);
        positionImg = findViewById(R.id.position_img);

        tvTitle.setText(isUpdateMode ? "Информация о товаре" : "Новый товар");
        button.setText(isUpdateMode ? "Вернуться" : "Добавить");
        positionImg.setImageBitmap(isUpdateMode ? currentPosition.getImage() : null);
        name.setText(isUpdateMode ? currentPosition.getName() : "");
        price.setText(isUpdateMode ? String.valueOf(currentPosition.getPrice()) : "");
        quantity.setText(isUpdateMode ? String.valueOf(currentPosition.getQuantity()) : "");

        if (ContextCompat.checkSelfPermission(PositionActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PositionActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        button.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
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

    //todo: sometimes get error, because data == null
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            positionImg.setImageBitmap(captureImage);
            wasUpdated = true;
        }
    }

    private void addOrUpdatePosition() {
        try {
            String id = isUpdateMode ? currentPosition.getId() : UUID.randomUUID().toString();
            String name = this.name.getText().toString();
            double price = Double.parseDouble(this.price.getText().toString());
            int quantity = Integer.parseInt(this.quantity.getText().toString());
            //mb need to invalidate ImageView first
            Bitmap positionImg = ((BitmapDrawable) this.positionImg.getDrawable()).getBitmap();
            //add image comparison
            Position position = new Position(id, name, price, quantity, positionImg);

            if (isUpdateMode && !position.equals(currentPosition)) {
                wasUpdated = true;
            }

            validateFields(position);
            validateName(position);

            Intent intent = new Intent(PositionActivity.this, MainActivity.class);
            intent.putExtra("newPosition", (Serializable) position);
            intent.putExtra("wasUpdated", wasUpdated);
            intent.putExtra("revenue", revenue);

            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Пожалуйста, проверьте введенные данные", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (!isUpdateMode || !positions.get(i).getName().equals(currentPosition.getName())) {
                        throw new IllegalArgumentException("Позиция с таким названием уже существует");
                    }
                }
            }
        }
    }
}
