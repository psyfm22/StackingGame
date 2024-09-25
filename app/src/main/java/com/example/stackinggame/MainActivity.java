package com.example.stackinggame;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class MainActivity extends AppCompatActivity {

    private ObjectAnimator animatorX;
    private ObjectAnimator animatorY;
    private int hotelCountStackingActivity;
    private ConstraintLayout constraintLayout;
    private ImageView hotelMiddleIV;
    private float startingLocationX, endingLocationX, endingLocationY;
    private final int yAxisTime = 2000;
    private float centreOfMass, cumulativeWeight;
    private final float weightOfBlock = 225, startOfGroundFloor = ;

    //We divide number of pixels by 24

    /**
     * 960 is the center of pixel
     * 660 is the start and
     * 1260 is the end
     *
     * So if the center of mass falls within this then it will stay
     *
     * divide by 24
     *
     * 40 is the middle
     * 27.5 is the start
     * 52.5 is the end
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView5);


        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        hotelCountStackingActivity = sharedPreferences.getInt("hotelCountStackingActivity", 0);

        float boxWidthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());
        float boxHeightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics());
        float upperConstraint = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, getResources().getDisplayMetrics());

        startingLocationX = 0f;
        endingLocationX = getResources().getDisplayMetrics().widthPixels - boxWidthInPx;
        endingLocationY = getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - boxHeightInPx;

        constraintLayout = findViewById(R.id.main);
        Button addNewHotelLayerButton = findViewById(R.id.addFloorButton);
        Button placeHotelLayerButton = findViewById(R.id.stopFloorButton);
        hotelMiddleIV = findViewById(R.id.hotelMiddleIV);
        ImageView hotelFloorIV = findViewById(R.id.hotelFloorIV);

        addNewHotelLayerButton.setOnClickListener(view -> addNewImage());

        placeHotelLayerButton.setOnClickListener(view -> {
            if(animatorX!=null){
                animatorX.pause();
                Log.d("COMP3018", "Floor x "+ hotelFloorIV.getX());
                Log.d("COMP3018", "Middle x "+ hotelMiddleIV.getX());
                Log.d("COMP3018", "Here is anotherflkaj;dsjfa: "+imageView.getX());
//                Floor x 660.0
//                Middle x 774.1541

                float extraMinus = hotelCountStackingActivity * boxHeightInPx;
                endingLocationY = getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - extraMinus;

                animatorY = ObjectAnimator.ofFloat(hotelMiddleIV, "translationY", 0f, endingLocationY);
                animatorY.setDuration(yAxisTime /hotelCountStackingActivity);
                animatorY.start();
            }
        });
    }

    private void addNewImage(){
        hotelCountStackingActivity++;
        hotelMiddleIV = new ImageView(this);

        hotelMiddleIV.setId(View.generateViewId());
        hotelMiddleIV.setImageResource(R.drawable.apartment_window);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics())
        );

        hotelMiddleIV.setLayoutParams(layoutParams);
        constraintLayout.addView(hotelMiddleIV);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(hotelMiddleIV.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 120);
        constraintSet.connect(hotelMiddleIV.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 0);

        constraintSet.applyTo(constraintLayout);

        animatorX = ObjectAnimator.ofFloat(hotelMiddleIV, "translationX", startingLocationX, endingLocationX);
        animatorX.setDuration(3000);
        animatorX.start();

        animatorX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {}

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                float holder;
                holder = startingLocationX;
                startingLocationX = endingLocationX;
                endingLocationX = holder;
                animatorX.setFloatValues(startingLocationX, endingLocationX);
                animatorX.start();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {}

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {}
        });
    }


    private void calculateNewWeightStack(){
        cumulativeWeight += weightOfBlock;
    }

    private void calculateNewCenterOfMass(float offset){
        centreOfMass = ((centreOfMass * cumulativeWeight) + (offset * weightOfBlock))/ (cumulativeWeight + weightOfBlock);
    }
}