package com.example.stackinggame;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View movingBox = findViewById(R.id.movingBox);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            // Animate the view to move across the screen from left to right
            ObjectAnimator animator = ObjectAnimator.ofFloat(movingBox, "translationX", 0f, screenWidth -movingBox.getWidth());

            // Set the duration (in milliseconds)
            animator.setDuration(3000); // 3 seconds

            // Start the animation
            animator.start();
        });
    }
}