package app.paseico.ui.marketplace;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private List<Discount> discounts;
    private List<DiscountObj> listDiscounts = new ArrayList<DiscountObj>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =inflater.inflate(R.layout.fragment_discounts, container, false);

        myDiscounts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, Discount>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Discount>>() {
                };
                Map<String, Discount> map = snapshot.getValue(genericTypeIndicator);
                discounts = new ArrayList<>(map.values());

                ListView discountsList = root.findViewById(R.id.ListViewDiscounts);

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



        return root;
    }


}

