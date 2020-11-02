package app.paseico.ui.marketplace;

import android.graphics.Color;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import app.paseico.R;
import app.paseico.SearchFragment;
import app.paseico.SearchFragmentDirections;
import app.paseico.data.Discount;
import app.paseico.data.User;


public class marketplaceFragment extends Fragment {

    private View root;

    private DatabaseReference myUsersRef = FirebaseDatabase.getInstance().getReference("users"); //Node users reference
    private User user = new User();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser fbusr = firebaseAuth.getCurrentUser();
    private DatabaseReference myActualUserRef;
    private Button btsDiscounts;
    private TextView tv7,tvm1000,tvm2000,tvm6000,tvme3,tvme5,tvme12,tvBoost,tvB2w,tvB5w,tvBPrice2000,tvBPrice6000,pointsView;
    private  ImageView btnBuyBoost2w,btnBuyBoost5w,btnBuy1000pts,btnBuy2000pts,btnBuy6000pts, btnFreeAd;
    private ProgressBar pbar;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_marketplace, container, false);
        btnFreeAd = root.findViewById(R.id.imageViewFreeAd);
        btnFreeAd.setVisibility(View.GONE);
        pbar = root.findViewById(R.id.progressBarMarketPlace);
        btsDiscounts =  root.findViewById(R.id.buttonMarketplaceDescuentos);
        btsDiscounts.setVisibility(View.GONE);
        tv7 = root.findViewById(R.id.textView7);
        tv7.setVisibility(View.GONE);
         tvm1000 = root.findViewById(R.id.textViewMarketplace1000);
        tvm1000.setVisibility(View.GONE);
         tvm2000 = root.findViewById(R.id.textViewMarketplace2000);
        tvm2000.setVisibility(View.GONE);
         tvm6000 = root.findViewById(R.id.textViewMarketplace6000);
        tvm6000.setVisibility(View.GONE);
         tvme3 = root.findViewById(R.id.textViewMarketplace3e);
        tvme3.setVisibility(View.GONE);
         tvme5 = root.findViewById(R.id.textViewMarketplace5e);
        tvme5.setVisibility(View.GONE);
         tvme12 = root.findViewById(R.id.textViewMarketplace12e);
        tvme12.setVisibility(View.GONE);
         tvBoost = root.findViewById(R.id.textViewMarketplaceBoostTitle);
        tvBoost.setVisibility(View.GONE);
         tvB2w = root.findViewById(R.id.textViewMarketplaceBoost2w);
        tvB2w.setVisibility(View.GONE);
         tvB5w = root.findViewById(R.id.textViewMarketplace5w);
        tvB5w.setVisibility(View.GONE);
         btnBuyBoost2w = root.findViewById(R.id.imageViewBuyBoost2w);
        btnBuyBoost2w.setVisibility(View.GONE);
         btnBuyBoost5w = root.findViewById(R.id.imageViewBuyBoost5w);
        btnBuyBoost5w.setVisibility(View.GONE);
         tvBPrice2000 = root.findViewById(R.id.textViewMarketplaceBoostPrice2000);
        tvBPrice2000.setVisibility(View.GONE);
         tvBPrice6000 = root.findViewById(R.id.textViewMarketplaceBoostPrice6000);
        tvBPrice6000.setVisibility(View.GONE);
         btnBuy1000pts = root.findViewById(R.id.imageViewBuy1000Points);
        btnBuy1000pts.setVisibility(View.GONE);
         btnBuy2000pts = root.findViewById(R.id.imageViewBuy2000Points);
        btnBuy2000pts.setVisibility(View.GONE);
         btnBuy6000pts = root.findViewById(R.id.imageViewBuy6000Points);
        btnBuy6000pts.setVisibility(View.GONE);
         pointsView = root.findViewById(R.id.textViewMarketplacePTS);
        pointsView.setVisibility(View.GONE);


        myActualUserRef = myUsersRef.child(fbusr.getUid());
        myActualUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                pointsView.setText("Tus puntos: "+user.getPoints());
                elementsVisible();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The db connection failed");
            }
        });


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

        btnBuyBoost2w.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                buyBoost(2);
            }
        });

        btnBuyBoost5w.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                buyBoost(5);
            }
        });

       /* btnFreeAd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    watchFreeAd();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });*/

       btsDiscounts.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               NavDirections action = marketplaceFragmentDirections.actionNavMarketplaceToDiscountsFragment();
               NavHostFragment.findNavController(marketplaceFragment.this)
                       .navigate(action);
           }
       });

        return root;
    }

    private void buyPoints(int option){
        final int points = option;
        int actualPoints = user.getPoints();
        int updatedPoints = points + actualPoints;
        final DatabaseReference myPointsReference = myActualUserRef.child("points");
        myPointsReference.setValue(updatedPoints);
        successAnimation();
        Toast.makeText(getActivity(), "Puntos comprados!", Toast.LENGTH_SHORT).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buyBoost(long option){
        final DatabaseReference myPointsReference = myActualUserRef.child("points");
        final DatabaseReference myBoostReference = myActualUserRef.child("boost");
        final DatabaseReference myBoostDateReference = myActualUserRef.child("boostExpires");
        if(!user.isBoost()) {
            Date actualDate = new Date();
            if(option == 2 && user.getPoints() >= 2000 || option == 5 && user.getPoints() >= 6000) {
                actualDate.setDate((int) (actualDate.getDate() + (7*option)));
                myBoostReference.setValue(true);
                String strActualDate = dateFormat.format(actualDate).toString();
                myBoostDateReference.setValue(strActualDate);
                int actualPoints = user.getPoints();
                int pointsToSubtract;
                if(option == 2){pointsToSubtract = -2000;}else{pointsToSubtract = -6000;}
                myPointsReference.setValue(actualPoints+pointsToSubtract);
                successAnimation();
            }
            else {
                //Not enough points
                Toast.makeText(getActivity(), "No tienes puntos suficientes para comprar el BOOST!", Toast.LENGTH_SHORT).show();
                failureAnimation();
                }
        } else{
            //Already a boost
            Toast.makeText(getActivity(), "No puedes comprar un BOOST! Ya tienes uno activo", Toast.LENGTH_SHORT).show();
            failureAnimation();
        }
    }

    private void elementsVisible(){
        btnFreeAd.setVisibility(View.VISIBLE);
        pbar.setVisibility(View.GONE);
        btsDiscounts.setVisibility(View.VISIBLE);
        tv7.setVisibility(View.VISIBLE);
        tvm1000.setVisibility(View.VISIBLE);
        tvm2000.setVisibility(View.VISIBLE);
        tvm6000.setVisibility(View.VISIBLE);
        tvme3.setVisibility(View.VISIBLE);
        tvme5.setVisibility(View.VISIBLE);
        tvme12.setVisibility(View.VISIBLE);
        tvBoost.setVisibility(View.VISIBLE);
        tvB2w.setVisibility(View.VISIBLE);
        tvB5w.setVisibility(View.VISIBLE);
        btnBuyBoost2w.setVisibility(View.VISIBLE);
        btnBuyBoost5w.setVisibility(View.VISIBLE);
        tvBPrice2000.setVisibility(View.VISIBLE);
        tvBPrice6000.setVisibility(View.VISIBLE);
        btnBuy1000pts.setVisibility(View.VISIBLE);
        btnBuy2000pts.setVisibility(View.VISIBLE);
        btnBuy6000pts.setVisibility(View.VISIBLE);
        pointsView.setVisibility(View.VISIBLE);
    }

    private void successAnimation(){
        int red = Color.parseColor("#e33630");
        int green = Color.parseColor("#32d959");
        int gray = Color.parseColor("#BDBDBD");
        root.setBackgroundColor(green);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                root.setBackgroundColor(gray);
            }
        },450);
    }

    private void failureAnimation(){
        int red = Color.parseColor("#e33630");
        int gray = Color.parseColor("#BDBDBD");
        root.setBackgroundColor(red);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                root.setBackgroundColor(gray);
            }
        },450);
    }


    /*private void watchFreeAd() throws ParseException {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            Date actualDate = date;
            String strDate = dateFormat.format(actualDate).toString();

            String strLastFreeAd = user.getLastFreeAd();
            Date lastFreeAd = dateFormat.parse(strLastFreeAd);
            Date plus24Date = new Date();
            plus24Date.setDate(lastFreeAd.getDate() + 1);


            if (plus24Date.compareTo(actualDate) > 0) {
                final int points = 5;
                int actualPoints = user.getPoints();
                int updatedPoints = points + actualPoints;
                final DatabaseReference myPointsReference = myActualUserRef.child("points");
                final DatabaseReference myLastFreeAdReference = myActualUserRef.child("lastFreeAd");
                myPointsReference.setValue(updatedPoints);
                myLastFreeAdReference.setValue(strDate);
                successAnimation();
                Toast.makeText(getActivity(), "Gracias por ver el video! Aqui tienes 5 puntos! Vuelve dentro de 24 horas!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Aún no puedes ver otro video, tienen que pasar 24 horas desde la ultima vez!", Toast.LENGTH_SHORT).show();
            }

    }
    */




}
