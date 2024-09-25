package com.example.stackinggame;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        hotelCountStackingActivity = sharedPreferences.getInt("hotelCountStackingActivity", 0);

        float boxWidthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());
        float boxHeightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics());
        float upperConstraint = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, getResources().getDisplayMetrics());

        startingLocationX = 0f;
        endingLocationX = getResources().getDisplayMetrics().widthPixels - boxWidthInPx;
        endingLocationY = getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - boxHeightInPx;

        constraintLayout = findViewById(R.id.main);
        Button addNewHotelLayerButton = findViewById(R.id.button);
        Button placeHotelLayerButton = findViewById(R.id.button2);
        hotelMiddleIV = findViewById(R.id.hotelMiddleIV);



        addNewHotelLayerButton.setOnClickListener(view -> {
            addNewImage();
        });

        placeHotelLayerButton.setOnClickListener(view -> {
            if(animatorX!=null){
                animatorX.pause();
                animatorY = ObjectAnimator.ofFloat(hotelMiddleIV, "translationY", 0f, endingLocationY);
                animatorY.setDuration(3000);
                animatorY.start();
            }
        });
    }

    private void addNewImage(){
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
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                float holder;
                holder = startingLocationX;
                startingLocationX = endingLocationX;
                endingLocationX = holder;
                animatorX.setFloatValues(startingLocationX, endingLocationX);
                animatorX.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
}