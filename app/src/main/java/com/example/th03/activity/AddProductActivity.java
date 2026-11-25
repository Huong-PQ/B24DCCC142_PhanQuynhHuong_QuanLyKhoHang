package com.example.th03.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th03.R;
import com.example.th03.database.DatabaseHelper;
import com.example.th03.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtProductName, edtProductPrice, edtProductQuantity, edtDescription;
    private Spinner spnCategory;
    private Switch swActive;
    private Button btnSaveProduct;
    private TextView btnBackAdd;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        edtProductName = findViewById(R.id.edtProductName);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductQuantity = findViewById(R.id.edtProductQuantity);
        edtDescription = findViewById(R.id.edtDescription);
        spnCategory = findViewById(R.id.spnCategory);
        swActive = findViewById(R.id.swActive);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnBackAdd = findViewById(R.id.btnBackAdd);

        // Nạp danh sách 5 loại sản phẩm vào Spinner
        String[] categories = {"Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(spinnerAdapter);

        btnBackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    private void saveProduct() {
        String name = edtProductName.getText().toString().trim();
        String priceStr = edtProductPrice.getText().toString().trim();
        String quantityStr = edtProductQuantity.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();
        int active = swActive.isChecked() ? 1 : 0;
        String description = edtDescription.getText().toString().trim();

        // Kiểm tra tên sản phẩm
        if (name.isEmpty()) {
            edtProductName.setError("Tên sản phẩm không được để trống");
            edtProductName.requestFocus();
            return;
        }

        // Kiểm tra giá bán (> 0)
        if (priceStr.isEmpty()) {
            edtProductPrice.setError("Vui lòng nhập giá bán");
            edtProductPrice.requestFocus();
            return;
        }
        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                edtProductPrice.setError("Giá bán phải lớn hơn 0");
                edtProductPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtProductPrice.setError("Giá bán không hợp lệ");
            edtProductPrice.requestFocus();
            return;
        }

        // Kiểm tra số lượng (>= 0)
        if (quantityStr.isEmpty()) {
            edtProductQuantity.setError("Vui lòng nhập số lượng");
            edtProductQuantity.requestFocus();
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtProductQuantity.setError("Số lượng không được âm");
                edtProductQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtProductQuantity.setError("Số lượng không hợp lệ");
            edtProductQuantity.requestFocus();
            return;
        }

        Product newProduct = new Product(name, price, quantity, category, active, description);
        long result = dbHelper.insertProduct(newProduct);

        if (result != -1) {
            Toast.makeText(this, "Đã lưu sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish(); // Quay về MainActivity, onResume() của MainActivity sẽ tự cập nhật
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}