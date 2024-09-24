package com.example.stackinggame;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View movingBox = findViewById(R.id.movingBox);
        Button button = findViewById(R.id.button);

        float boxWidthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 174, getResources().getDisplayMetrics());

        final float[] startingLocation = {0f};
        final float[] endingLocation = {getResources().getDisplayMetrics().widthPixels - boxWidthInPx};

        animator = ObjectAnimator.ofFloat(movingBox, "translationX", startingLocation[0], endingLocation[0]);
        animator.setDuration(3000);

        button.setOnClickListener(view -> {
            animator.start();
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                float holder;
                holder = startingLocation[0];
                startingLocation[0] = endingLocation[0];
                endingLocation[0] = holder;
                animator.setFloatValues(startingLocation[0], endingLocation[0]);
                animator.start(); // Restart the animation
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
}