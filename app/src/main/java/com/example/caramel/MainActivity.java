package com.example.caramel;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText itemET;
    private Button button;
    private ListView itemsList;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemET = findViewById(R.id.item_edit_text);
        button = findViewById(R.id.add_btn);
        itemsList = findViewById(R.id.items_list);

        items = FileHelper.readData(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        itemsList.setAdapter(adapter);

        button.setOnClickListener(this);
        itemsList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                String itemName = itemET.getText().toString();
                if (itemName.length() > 0) {
                    adapter.add(itemName);
                    itemET.setText("");
                    try {
                        FileHelper.writeData(items, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        items.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
    }
}
