package app.paseico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.paseico.data.Discount;
import app.paseico.data.DiscountObj;
import app.paseico.data.Route;
import app.paseico.login.LogInActivity;
import app.paseico.login.RegisterActivity;
import app.paseico.mainMenu.userCreatedRoutes.CreateNewRouteActivity;

public class OrganizationCouponsActivity extends AppCompatActivity {
    private DatabaseReference myDiscounts = FirebaseDatabase.getInstance().getReference("discounts");
    private FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<Discount> discounts;
    private List<DiscountObj> listDiscounts = new ArrayList<DiscountObj>();
    private ListView discountsList;
    private int actualSelected;
    private  ArrayAdapter<DiscountObj> adapter;
    private boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_coupons);

        discountsList = findViewById(R.id.listViewOrganiCoupons);

        initializeList();

        Button createCoupon = findViewById(R.id.buttonCreateNewCoupon);
        createCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewRouteIntent = new Intent(getApplicationContext(), CreateCouponActivity.class);
                startActivity(createNewRouteIntent);
                finish();
            }
        });

        discountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                view.setSelected(true);
                selected = true;
                actualSelected = position;
            }
        });

        Button deleteCoupon = findViewById(R.id.buttonDeleteCoupon);
        deleteCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected){
                if(!adapter.isEmpty()){showDeleteAlert();}
                else{                    Toast.makeText(OrganizationCouponsActivity.this, "No tienes cupones que borrar! ",
                        Toast.LENGTH_SHORT).show();}

            }else{Toast.makeText(OrganizationCouponsActivity.this, "No has seleccionado un cupón! ",
                        Toast.LENGTH_SHORT).show();}
            }

        });
    }

    private void initializeList(){
        Query q = myDiscounts.orderByChild("organiID").equalTo(fbUser.getUid());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String,Discount>> t=  new GenericTypeIndicator<HashMap<String,Discount>>() { };
                HashMap<String,Discount> hashMap= (HashMap<String,Discount>)snapshot.getValue(t);
                if(hashMap!=null) {
                    discounts = new ArrayList<Discount>(hashMap.values());
                    for (Discount disc: discounts){
                        listDiscounts.add(new DiscountObj(disc.getName(), disc.getPercentage(), disc.getPoints()));
                    }
                }
                adapter = new ArrayAdapter<DiscountObj>(OrganizationCouponsActivity.this, R.layout.cupon_item, listDiscounts);
                discountsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDeleteAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OrganizationCouponsActivity.this);
        builder.setMessage("¿Seguro que quieres borrar este cupón?")
                .setTitle("Borrando cupón")
                .setPositiveButton("Sí", (dialog, which) -> {
                    deleteCoupon();
                })
                .setNegativeButton("No",(dialog, which) -> {
                });
        builder.show();
    }

    private void deleteCoupon(){
        Discount d = discounts.get(actualSelected);
        myDiscounts.child(d.getNodeId()).removeValue();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { //Wait 2 secs to load the next activity (LoginScreen)
            @Override
            public void run() {
                try {
                    adapter.clear();
                    initializeList();
                    selected = false;
                    Toast.makeText(OrganizationCouponsActivity.this, "Cupon eliminado! ",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }
        }, 1000);
    }
}