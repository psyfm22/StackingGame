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
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ObjectAnimator animatorX;
    private ObjectAnimator animator2;
    private int hotelCountStackingActivity;
    ConstraintLayout layout;
    ImageView hotelMiddleIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.main);

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        hotelCountStackingActivity = sharedPreferences.getInt("hotelCountStackingActivity", 0);


        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        hotelMiddleIV = findViewById(R.id.hotelMiddleIV);


        float boxWidthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());
        float boxHeightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics());
        float upperConstraint = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, getResources().getDisplayMetrics());

        final float[] startingLocationX = {0f};
        final float[] endingLocationX = {getResources().getDisplayMetrics().widthPixels - boxWidthInPx};
        final float[] endingLocationY = {getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - boxHeightInPx};

        animatorX = ObjectAnimator.ofFloat(hotelFloor, "translationX", startingLocationX[0], endingLocationX[0]);
        animatorX.setDuration(3000);

        button.setOnClickListener(view -> {
//            animatorX.start();
        });

        button2.setOnClickListener(view -> {
//            animatorX.pause();
            addNewFloor();

//            animator2 = ObjectAnimator.ofFloat(hotelFloor, "translationY", 0f, endingLocationY[0]);
//            animator2.setDuration(3000);
//            animator2.start();
        });

        animatorX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                float holder;
                holder = startingLocationX[0];
                startingLocationX[0] = endingLocationX[0];
                endingLocationX[0] = holder;
                animatorX.setFloatValues(startingLocationX[0], endingLocationX[0]);
                animatorX.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void addNewFloor(){
        hotelCountStackingActivity++;

        ImageView newFloor = new ImageView(this);
        newFloor.setImageResource(R.drawable.apartment_window);
//
//        float width =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());
//        float height =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics());
//
//        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams((int) width, (int) height);
//
//        // Set constraints for the new ImageView
//        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
//        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
//        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // Adjust this as needed
//        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET; // Adjust this to position the new floor as needed
//
//        newFloor.setLayoutParams(layoutParams);
//        newFloor.setImageResource(R.drawable.apartment_window);
//
//        layout.addView(newFloor, 0);
    }
}