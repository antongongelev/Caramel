package com.example.caramel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.UUID;

import static com.example.caramel.Constants.CARAMEL_DATA;
import static com.example.caramel.Constants.CURRENT_POSITION;
import static com.example.caramel.Constants.POSITIONS;
import static com.example.caramel.Constants.REVENUE;
import static com.example.caramel.Constants.SOLD_POSITIONS;
import static com.example.caramel.DataService.loadImageFromStorage;
import static com.example.caramel.DataService.loadPositions;
import static com.example.caramel.DataService.savePositions;
import static com.example.caramel.DataService.saveToInternalStorage;

public class PositionActivity extends AppCompatActivity implements View.OnClickListener, StateManager, PopupMenu.OnMenuItemClickListener {

    SharedPreferences sharedPreferences;
    String imageName;
    String barcode;
    TextView barcodeTv;
    ImageButton codeBtn;
    Button categoryBtn;
    TextView tvTitle;
    Button button;
    ImageButton cameraBtn;
    ImageView positionImg;
    EditText name;
    EditText price;
    EditText quantity;
    ArrayList<Position> positions;
    static ArrayList<Position> soldPositions = new ArrayList<>();
    Position currentPosition;
    double revenue;
    boolean isUpdateMode;
    boolean wasUpdated;
    boolean inScannerMode;
    boolean inPhotoMode;
    int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        loadData();

        currentPosition = (Position) getIntent().getSerializableExtra(CURRENT_POSITION);
        imageName = currentPosition == null ? null : currentPosition.getImageName();
        barcode = currentPosition == null ? "" : currentPosition.getBarcode();
        categoryId = currentPosition == null ? Category.EMPTY.getId() : currentPosition.getCategoryId();
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
        codeBtn = findViewById(R.id.code_btn);
        barcodeTv = findViewById(R.id.code_tv);
        categoryBtn = findViewById(R.id.category_btn_position);

        //data setting
        tvTitle.setText(isUpdateMode ? "Информация о товаре" : "Новый товар");
        button.setText(isUpdateMode ? "Принять" : "Добавить");
        positionImg.setImageBitmap(isUpdateMode ? loadImageFromStorage(currentPosition.getImageName(), this) : null);
        name.setText(isUpdateMode ? currentPosition.getName() : "");
        price.setText(isUpdateMode ? String.valueOf(currentPosition.getPrice()) : "");
        quantity.setText(isUpdateMode ? String.valueOf(currentPosition.getQuantity()) : "");
        barcodeTv.setText(isUpdateMode ? currentPosition.getBarcode() : "");
        categoryBtn.setText(Category.getCategoryById(categoryId).getName());

        //click listening
        button.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        codeBtn.setOnClickListener(this);
        categoryBtn.setOnClickListener(this);
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
    public void loadData() {
        sharedPreferences = getSharedPreferences(CARAMEL_DATA, MODE_PRIVATE);
        revenue = Double.parseDouble(sharedPreferences.getString(REVENUE, "0"));
        positions = loadPositions(sharedPreferences, POSITIONS);
        soldPositions = loadPositions(sharedPreferences, SOLD_POSITIONS);
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
            case R.id.code_btn: {
                scanCode();
                break;
            }
            case R.id.category_btn_position: {
                changeCategory(v);
            }
            default:
                break;
        }
    }

    private void changeCategory(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.category_position_menu);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        categoryBtn.setText(item.getTitle());
        switch (item.getItemId()) {
            case R.id.category_position_empty: {
                this.categoryId = Category.EMPTY.getId();
                break;
            }
            case R.id.category_position_korea: {
                this.categoryId = Category.KOREA.getId();
                break;
            }
        }
        return true;
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Отсканируйте штриход");
        integrator.initiateScan();
        inScannerMode = true;
    }

    private void uploadPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
        inPhotoMode = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (inPhotoMode && data != null && requestCode == 100) {
            try {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                positionImg.setImageBitmap(capturedImage);
                //save image to storage
                imageName = saveToInternalStorage(capturedImage, getApplicationContext());
                wasUpdated = true;
                inPhotoMode = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (inScannerMode) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    barcode = result.getContents();
                    barcodeTv.setText(result.getContents());
                    wasUpdated = true;
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Штрихкод не найден", Toast.LENGTH_SHORT).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            inScannerMode = false;
        }
    }

    private void addOrUpdatePosition() {
        try {
            String id = isUpdateMode ? currentPosition.getId() : UUID.randomUUID().toString();
            String name = this.name.getText().toString().trim();
            double price = Double.parseDouble(this.price.getText().toString());
            int quantity = Integer.parseInt(this.quantity.getText().toString());
            Position position = new Position(id, name, price, quantity, imageName, barcode, categoryId);

            if (isUpdateMode && !position.equals(currentPosition)) {
                wasUpdated = true;
            }

            validateFields(position);
            validateNameAndBarcode(position);

            if (isUpdateMode && wasUpdated) {
                updatePosition(position);
            } else if (!isUpdateMode) {
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
                String oldName = positions.get(i).getName();
                int oldCategoryId = positions.get(i).getCategoryId();
                // TODO: 28.04.2020 !!!!!!!!!!!!!!!!!!
                positions.set(i, position);
                //bypassing new name to sold positions
                if (!oldName.equals(position.getName())) {
                    for (int j = 0; j < soldPositions.size(); j++) {
                        if (soldPositions.get(j).getId().equals(position.getId())) {
                            soldPositions.get(j).setName(position.getName());
                        }
                    }
                }
                //bypassing new category to sold positions
                if (oldCategoryId != position.getCategoryId()) {
                    for (int j = 0; j < soldPositions.size(); j++) {
                        if (soldPositions.get(j).getId().equals(position.getId())) {
                            soldPositions.get(j).setCategoryId(position.getCategoryId());
                        }
                    }
                }
                Toast.makeText(this, String.format("Товар \'%s\' был успешно изменен", position.getName()), Toast.LENGTH_LONG).show();
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

    private void validateNameAndBarcode(Position position) {
        if (positions != null && positions.size() > 0) {
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).getName().equals(position.getName())) {
                    if (!isUpdateMode || !positions.get(i).getId().equals(currentPosition.getId())) {
                        throw new IllegalArgumentException("Позиция с таким названием уже существует");
                    }
                }
                if (position.getBarcode() != null && !position.getBarcode().isEmpty() &&
                        position.getBarcode().equals(positions.get(i).getBarcode())) {
                    if (!isUpdateMode || !positions.get(i).getId().equals(currentPosition.getId())) {
                        throw new IllegalArgumentException("Позиция с таким штрихкодом уже существует");
                    }
                }
            }
        }
    }
}
