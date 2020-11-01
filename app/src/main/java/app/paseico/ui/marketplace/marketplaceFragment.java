package app.paseico.ui.marketplace;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import app.paseico.R;
import app.paseico.data.UserDao;

public class marketplaceFragment extends Fragment {

    private marketplaceViewModel marketplaceViewModel;
    private UserDao userDao = new UserDao();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_marketplace, container, false);

        ImageView btnBuy1000pts = root.findViewById(R.id.imageViewBuy1000Points);
        ImageView btnBuy2000pts = root.findViewById(R.id.imageViewBuy2000Points);
        ImageView btnBuy6000pts = root.findViewById(R.id.imageViewBuy6000Points);

        btnBuy1000pts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyPoints(1000);
            }
        });

        btnBuy2000pts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyPoints(2000);
            }
        });

        btnBuy6000pts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyPoints(6000);
            }
        });


        return root;
    }

    private void buyPoints(int option){
        int pts = option;
        userDao.updatePoints(pts);
    }
}