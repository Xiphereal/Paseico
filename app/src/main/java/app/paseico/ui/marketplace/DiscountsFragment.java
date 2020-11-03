package app.paseico.ui.marketplace;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.paseico.R;
import app.paseico.data.Discount;
import app.paseico.data.DiscountObj;
import app.paseico.data.User;


public class DiscountsFragment extends Fragment {
    private View root;

    private DatabaseReference myDiscounts = FirebaseDatabase.getInstance().getReference("discounts");
    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference
    private DatabaseReference myActualUserRef;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser fbusr = firebaseAuth.getCurrentUser();

    private User user = new User();

    private List<Discount> discounts;
    private List<DiscountObj> listDiscounts = new ArrayList<DiscountObj>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        root =inflater.inflate(R.layout.fragment_discounts, container, false);

        ListView discountsList = root.findViewById(R.id.ListViewDiscounts);
        TextView myUserPoints = root.findViewById(R.id.textViewDiscountsUserPoints);

        myActualUserRef = myUsersRef.child(fbusr.getUid());
        myActualUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                myUserPoints.setText("Tus puntos: "+user.getPoints());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed");
            }
        });
        myDiscounts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, Discount>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Discount>>() {
                };
                Map<String, Discount> map = snapshot.getValue(genericTypeIndicator);
                discounts = new ArrayList<>(map.values());



                for(int i = 0; i < discounts.size(); i++){
                    String n = discounts.get(i).getName();
                    String p = discounts.get(i).getPercentage();
                    int pts = discounts.get(i).getPoints();
                    listDiscounts.add(new DiscountObj(n,p,pts));
                }

                ArrayAdapter<DiscountObj> adapter = new ArrayAdapter<DiscountObj>(getActivity(), android.R.layout.simple_list_item_1, listDiscounts);
                discountsList.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        discountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DatabaseReference myPointsReference = myActualUserRef.child("points");
                if(discounts.get(i).getPoints()<=user.getPoints()){
                    int updatedPoints = user.getPoints() - discounts.get(i).getPoints();
                    myPointsReference.setValue(updatedPoints);
                    Toast.makeText(getActivity(), "Enhorabuena! Acabas de canjear un descuento de " + discounts.get(i).getName() , Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getActivity(), "No tienes puntos suficientes para canjear el descuento de " + discounts.get(i).getName() , Toast.LENGTH_SHORT).show();
                }
            }
        });


        return root;
    }


}

