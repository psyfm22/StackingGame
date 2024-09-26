package com.example.stackinggame;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ObjectAnimator animatorX;
    private ObjectAnimator animatorY;
    private int hotelCountStackingActivity;
    private ConstraintLayout constraintLayout;
    private ImageView hotelMiddleIV;
    private float startingLocationX, endingLocationX, endingLocationY;
    private final int yAxisTime = 2000;
    private double numberBoxesSoFar=0;
    private double[] centreOfMassPoint = new double[2];
    private final double startOfGroundFloor =660, endOfGroundFloor=1260;
    private double lastBlockLeftX, lastBlockRightX;
    private AlertDialog alertDialog;


    //We divide number of pixels by 24

    /**
     * Length is 600 and Height is 216
     * Divided Length is 25 and Height is 9
     *
     * 960 is the center of start block
     * 660 is the start and
     * 1260 is the end
     *
     * So if the center of mass falls within this then it will stay
     */


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
        Button addNewHotelLayerButton = findViewById(R.id.addFloorButton);
        Button placeHotelLayerButton = findViewById(R.id.stopFloorButton);
        hotelMiddleIV = findViewById(R.id.hotelMiddleIV);
        
        //First rectangle
        /*
        Center of gravity for first block is 960, 108, it just the central point
        The actual pixel would be (960,916)
         */
        centreOfMassPoint[0] = 960;
        centreOfMassPoint[1] = 916;
        lastBlockLeftX = 660;
        lastBlockRightX = 1260;


        addNewHotelLayerButton.setOnClickListener(view -> addNewImage());

        placeHotelLayerButton.setOnClickListener(view -> {
            if(animatorX!=null){
                animatorX.pause();

                Log.d("COMP3018", "x of image is : "+hotelMiddleIV.getX());
                Log.d("COMP3018", "y of image is : "+hotelMiddleIV.getY());

                double leftX = hotelMiddleIV.getX();
                double rightX = leftX + 600;

                if(rightX<lastBlockLeftX || leftX>lastBlockRightX){
                    showAlertDialogue(false);
                }
                lastBlockLeftX = leftX;
                lastBlockRightX = rightX;


                calculateNewCenterOfMass(hotelMiddleIV.getX(), hotelMiddleIV.getY());
                Log.d("COMP3018", "Center of mass x: "+ centreOfMassPoint[0]);
                Log.d("COMP3018", "Start of ground: "+ startOfGroundFloor);
                Log.d("COMP3018", "end of ground: "+ endOfGroundFloor);

                if (centreOfMassPoint[0] < startOfGroundFloor || centreOfMassPoint[0] > endOfGroundFloor) {
                    showAlertDialogue(false);
                }


                //Moves it to the right location
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


    private void calculateNewCenterOfMass(double xInput, double yInput){
        //Calculate centre of mass, each block weighs 1kg

        double[] newCOM = new double[2];//x and y
        newCOM[0] = xInput + 300;
        newCOM[1] = yInput - 108;
        //We do this as this works out the centre of the block

        centreOfMassPoint[0] = ((numberBoxesSoFar * centreOfMassPoint[0]) + newCOM[0])/(numberBoxesSoFar+1);
        centreOfMassPoint[1] = ((numberBoxesSoFar * centreOfMassPoint[1]) + newCOM[1])/(numberBoxesSoFar+1);

        numberBoxesSoFar++;
    }


    /**
     * showAlertDialogue, Shows the success of adding the alert dialogue
     */
    private void showAlertDialogue(boolean successful) {
        //Initialise the layouts and views
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_layout, null, false);
        ConstraintLayout alertLayout = view.findViewById(R.id.alertLayout);
        TextView alertTitle = view.findViewById(R.id.alertTitleTV);
        TextView alertDescription = view.findViewById(R.id.alertDescriptionTV);
        Button alertButton = view.findViewById(R.id.alertDoneButton);
        ImageView alertLogo = view.findViewById(R.id.alertLogoIV);

        if (successful) {
            alertTitle.setText(R.string.success_title_alert);
            alertDescription.setText(R.string.emotion_added_text_alert);
            alertLogo.setImageResource(R.drawable.success);
            alertButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        } else {
            alertTitle.setText(R.string.game_over_title_alert);
            alertDescription.setText(R.string.game_over_text_alert);
            alertLogo.setImageResource(R.drawable.error);
            alertButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red_alert));
        }

        //Initialise the builder and the alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        alertDialog = builder.create();

        //Set the button dismiss
        alertButton.setOnClickListener(view1 -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        //Show the actual alert
        alertDialog.show();
    }
}