package app.paseico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import app.paseico.data.Discount;
import app.paseico.data.Organization;
import app.paseico.data.Router;
import app.paseico.login.RegisterActivity;
import app.paseico.mainMenu.userCreatedRoutes.CreateNewRouteActivity;

public class CreateCouponActivity extends AppCompatActivity {
    private DatabaseReference myDiscountsRef = FirebaseDatabase.getInstance().getReference("discounts");
    private DatabaseReference myOrganizationsRef = FirebaseDatabase.getInstance().getReference("organizations");
    private FirebaseUser fbUser;
    private Organization currentOrganization;
    private EditText discountName, discountPercentage;
    private Button btnCreateCoupon, btnCancelCoupon;
    private TextView textViewNameCoupon, textViewPercentajeCoupon;
    private ProgressBar progressBar;
    private int cost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coupon);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        discountName = findViewById(R.id.editTextTextCuponName);
        discountPercentage = findViewById(R.id.editTextTextCouponPercentage);
        btnCreateCoupon = findViewById(R.id.buttonCreateCoupon);
        btnCancelCoupon = findViewById(R.id.buttonCancelCoupon);
        textViewNameCoupon = findViewById(R.id.textViewCuponName);
        textViewPercentajeCoupon = findViewById(R.id.textViewCouponPercentage);
        progressBar = findViewById(R.id.progressBarCouponCreator);

        hideAllComponents();

        DatabaseReference myActualOrganizationRef = myOrganizationsRef.child(fbUser.getUid());
        myActualOrganizationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentOrganization = snapshot.getValue(Organization.class);
                discountName.setText(currentOrganization.getName());
                showAllComponents();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed");
            }
        });

        btnCreateCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCouponFields()){
                    createCoupon();
                } else{
                    Toast.makeText(CreateCouponActivity.this, "Error: Hay campos vacios o incorrectos! ",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelAlert();
            }
        });
    }

    private void hideAllComponents(){
        discountName.setVisibility(View.GONE);
        discountPercentage.setVisibility(View.GONE);
        btnCreateCoupon.setVisibility(View.GONE);
        btnCancelCoupon.setVisibility(View.GONE);
        textViewNameCoupon.setVisibility(View.GONE);
        textViewPercentajeCoupon.setVisibility(View.GONE);
    }

    private void showAllComponents(){
        discountName.setVisibility(View.VISIBLE);
        discountPercentage.setVisibility(View.VISIBLE);
        btnCreateCoupon.setVisibility(View.VISIBLE);
        btnCancelCoupon.setVisibility(View.VISIBLE);
        textViewNameCoupon.setVisibility(View.VISIBLE);
        textViewPercentajeCoupon.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private boolean checkCouponFields(){
        if(!TextUtils.isEmpty(discountName.getText().toString())
            &&!TextUtils.isEmpty(discountPercentage.getText().toString())){
            try{
                int num = Integer.parseInt(discountPercentage.getText().toString());
                if(num <= 100 && num >=1){return true;}else{return false;}
            }catch(NumberFormatException e){
                return false;
            }
        } else{
            return false;
        }
    }

    private void createCoupon(){
        String discName = discountName.getText().toString();
        int percent = Integer.parseInt(discountPercentage.getText().toString());
        calculatePointCost();
        Discount discount = new Discount(discName, percent, cost, fbUser.getUid().toString());
        myDiscountsRef.child(UUID.randomUUID().toString()).setValue(discount);
        Toast.makeText(CreateCouponActivity.this, "Cupón creado! ",
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void calculatePointCost(){
        cost = Integer.parseInt(discountPercentage.getText().toString()) * 250;
    }
    private void showCancelAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCouponActivity.this);

        builder.setMessage("¿Seguro que quieres cancelar la creación de este cupón?")
                .setTitle("Cancelando cupón")
                .setPositiveButton("Sí", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("No",(dialog, which) -> {
                });
        builder.show();
    }
    @Override
    public void onBackPressed()
    {
        showCancelAlert();
    }
}