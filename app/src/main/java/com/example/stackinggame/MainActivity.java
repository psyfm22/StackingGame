package com.example.stackinggame;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
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
    ObjectAnimator animator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View movingBox = findViewById(R.id.movingBox);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        float boxWidthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 174, getResources().getDisplayMetrics());
        float boxHeightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 174, getResources().getDisplayMetrics());

        final float[] startingLocationX = {0f};
        final float[] endingLocationX = {getResources().getDisplayMetrics().widthPixels - boxWidthInPx};
        final float[] endingLocationY = {getResources().getDisplayMetrics().heightPixels - boxHeightInPx - boxWidthInPx};

        animator = ObjectAnimator.ofFloat(movingBox, "translationX", startingLocationX[0], endingLocationX[0]);
        animator.setDuration(3000);

        button.setOnClickListener(view -> {
            animator.start();
        });

        button2.setOnClickListener(view -> {
            animator.pause();
            Log.d("COMP3018", ""+ movingBox.getX());
            animator2 = ObjectAnimator.ofFloat(movingBox, "translationY", 0f, endingLocationY[0]);
            animator2.setDuration(3000);
            animator2.start();
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                float holder;
                holder = startingLocationX[0];
                startingLocationX[0] = endingLocationX[0];
                endingLocationX[0] = holder;
                animator.setFloatValues(startingLocationX[0], endingLocationX[0]);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
}