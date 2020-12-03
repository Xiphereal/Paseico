package app.paseico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class OrganizationCouponsActivity extends AppCompatActivity {
    private DatabaseReference myDiscounts = FirebaseDatabase.getInstance().getReference("discounts");
    private FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<Discount> discounts;
    private List<DiscountObj> listDiscounts = new ArrayList<DiscountObj>();
    private ListView discountsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_coupons);

        discountsList = findViewById(R.id.listViewOrganiCoupons);

        initializeList();

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
                ArrayAdapter<DiscountObj> adapter = new ArrayAdapter<DiscountObj>(OrganizationCouponsActivity.this, R.layout.cupon_item, listDiscounts);
                discountsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}