package com.example.stackinggame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ObjectAnimator animatorX, animatorY;
    private int hotelCountStackingActivity;
    private int currentNumberOfBoxes =0;
    private final int yAxisTime = 2000;
    private float startingLocationX, endingLocationX, endingLocationY, boxWidthInPx,
            boxHeightInPx, upperConstraint;
    private double numberBoxesSoFar=0;
    private final double[] centreOfMassPoint = new double[2];
    private final double startOfGroundFloor =660, endOfGroundFloor=1260;
    private double lastBlockLeftX, lastBlockRightX;
    private AlertDialog alertDialog;
    private final List<ImageView> middleImageViews = new ArrayList<>();
    private TextView scoreTV;
    private ConstraintLayout constraintLayout;
    private ImageView hotelMiddleIV, hotelFloorIV, grassBackgroundIV, skyBackgroundIV, skyBackground2IV;
    private boolean doingFirstPass = true;
    private SoundPool soundPool;
    private int soundEffect;
    private MediaPlayer mediaPlayer;

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

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5) // Five simulataneous sounds
                .build();

        soundEffect = soundPool.load(MainActivity.this, R.raw.coin, 1);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


        boxWidthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());
        boxHeightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics());
        upperConstraint = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, getResources().getDisplayMetrics());

        startingLocationX = 0f;
        endingLocationX = getResources().getDisplayMetrics().widthPixels - boxWidthInPx;
        endingLocationY = getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - boxHeightInPx;

        constraintLayout = findViewById(R.id.main);
        Button addNewHotelLayerButton = findViewById(R.id.addFloorButton);
        Button placeHotelLayerButton = findViewById(R.id.stopFloorButton);
        hotelMiddleIV = findViewById(R.id.hotelMiddleIV);
        hotelFloorIV = findViewById(R.id.hotelFloorIV);
        scoreTV = findViewById(R.id.scoreTV);

        grassBackgroundIV = findViewById(R.id.grassBackgroundIV);
        skyBackgroundIV = findViewById(R.id.skyBackgroundIV);
        skyBackground2IV = findViewById(R.id.skyBackground2IV);

        placeHotelLayerButton.setEnabled(false);
        //First rectangle
        /*
        Center of gravity for first block is 960, 108, it just the central point
        The actual pixel would be (960,916)
         */
        centreOfMassPoint[0] = 960;
        centreOfMassPoint[1] = 916;
        lastBlockLeftX = 660;
        lastBlockRightX = 1260;


        addNewHotelLayerButton.setOnClickListener(view -> {
            addNewImage();
            addNewHotelLayerButton.setEnabled(false);
            placeHotelLayerButton.setEnabled(true);
        });

        placeHotelLayerButton.setOnClickListener(view -> {
            placeHotelLayerButton.setEnabled(false);
            if(animatorX!=null){
                animatorX.pause();

                double leftX = hotelMiddleIV.getX();
                double rightX = leftX + 600;

                calculateNewCenterOfMass(hotelMiddleIV.getX(), hotelMiddleIV.getY());

                if(rightX<lastBlockLeftX || leftX>lastBlockRightX){
                    showAlertDialogue(false);
                    resetActivity();
                } else if (centreOfMassPoint[0] < startOfGroundFloor || centreOfMassPoint[0] > endOfGroundFloor) {
                    showAlertDialogue(false);
                    resetActivity();
                }else{
                    lastBlockLeftX = leftX;
                    lastBlockRightX = rightX;
                    float extraMinus = currentNumberOfBoxes * boxHeightInPx;
                    endingLocationY = getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - extraMinus;
                    animatorY = ObjectAnimator.ofFloat(hotelMiddleIV, "translationY", 0f, endingLocationY);
                    animatorY.setDuration(yAxisTime /currentNumberOfBoxes);
                    Log.d("COMP3018", "Current number of boxes: "+ currentNumberOfBoxes);
                    animatorY.start();
                    scoreTV.setText(""+hotelCountStackingActivity);
                }

                animatorY.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {

                        playSound();


                        //Moves it to the right location
                        addNewHotelLayerButton.setEnabled(true);
                        placeHotelLayerButton.setEnabled(false);
                        if(currentNumberOfBoxes ==3){

                            AnimatorSet animatorSet = new AnimatorSet();
                            List<Animator> animators = new ArrayList<>();

                            float newEndingLocation = endingLocationY + (boxHeightInPx*6);

                            if(doingFirstPass){
                                float holder = (float) (boxHeightInPx*3.2);
                                ObjectAnimator animator1 = ObjectAnimator.ofFloat(hotelFloorIV, "translationY", newEndingLocation-holder);
                                animator1.setDuration(5000);
                                animators.add(animator1);
                                newEndingLocation -= boxHeightInPx;
                            }

                            ObjectAnimator animator1;
                            for(ImageView imageView : middleImageViews){
                                Log.d("COMP3018","This is the location of them "+ imageView.getY());
                                Log.d("COMP3018","Freddie checking new ending location "+ newEndingLocation);
                                //We go from the first image at the bottom
                                animator1 = ObjectAnimator.ofFloat(imageView, "translationY", newEndingLocation);
                                animator1.setDuration(5000);
                                animators.add(animator1);

                                newEndingLocation -= boxHeightInPx;
                            }

                            Log.d("COMP3018","Doing the first pass: "+ doingFirstPass);

                            if(doingFirstPass){
                                ObjectAnimator slideDownSkyBackground = ObjectAnimator.ofFloat(skyBackgroundIV, "translationY", -1080f, 0f);
                                slideDownSkyBackground.setDuration(5000);
                                animators.add(slideDownSkyBackground);
                                ObjectAnimator slideDownGrassBackground = ObjectAnimator.ofFloat(grassBackgroundIV, "translationY", 0f, 1080f);
                                slideDownGrassBackground.setDuration(5000);
                                animators.add(slideDownGrassBackground);
                                doingFirstPass = false;
                            }else{
                                ObjectAnimator slideDownSkyBackground = ObjectAnimator.ofFloat(skyBackground2IV, "translationY", -1080f, 0f);
                                slideDownSkyBackground.setDuration(5000);
                                animators.add(slideDownSkyBackground);
                                ObjectAnimator slideDownGrassBackground = ObjectAnimator.ofFloat(skyBackgroundIV, "translationY", 0f, 1080f);
                                slideDownGrassBackground.setDuration(5000);
                                animators.add(slideDownGrassBackground);

                            }

                            animatorSet.playTogether(animators);
                            animatorSet.start();



                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(@NonNull Animator animator) {}

                                @Override
                                public void onAnimationEnd(@NonNull Animator animator) {

                                    ImageView lastElement = middleImageViews.get(middleImageViews.size() - 1);

                                    int numberOfIVs = middleImageViews.size();
                                    for(int i=0 ; i<numberOfIVs;i++){
                                        if(i!=numberOfIVs-1){
                                            constraintLayout.removeView(middleImageViews.get(i));
                                        }
                                    }
                                    middleImageViews.clear();
                                    middleImageViews.add(0, lastElement);
                                    if(doingFirstPass){
                                        constraintLayout.removeView(hotelFloorIV);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animator) {}

                                @Override
                                public void onAnimationRepeat(@NonNull Animator animator) {}
                            });


                            currentNumberOfBoxes =0;
                        }
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {}
                });
            }
        });
    }





    private void addNewImage(){
        hotelCountStackingActivity++;
        currentNumberOfBoxes++;

        hotelMiddleIV = new ImageView(this);

        hotelMiddleIV.setId(View.generateViewId());
        hotelMiddleIV.setImageResource(R.drawable.apartment_window);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics())
        );

        hotelMiddleIV.setLayoutParams(layoutParams);
        constraintLayout.addView(hotelMiddleIV);
        middleImageViews.add(hotelMiddleIV);


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



    private void playSound() {
        soundPool.play(soundEffect, 1.0f, 1.0f, 1, 0, 1.0f);
    }





    private void resetActivity(){
        for(int i=0 ; i<middleImageViews.size();i++){
            constraintLayout.removeView(middleImageViews.get(i));
        }
        middleImageViews.clear(); // Clear the list

        hotelCountStackingActivity = 0;
        currentNumberOfBoxes =0;

        centreOfMassPoint[0] = 960;
        centreOfMassPoint[1] = 916;
        lastBlockLeftX = 660;
        lastBlockRightX = 1260;

        numberBoxesSoFar = 0;

        startingLocationX = 0f;
        endingLocationX = getResources().getDisplayMetrics().widthPixels - boxWidthInPx;
        endingLocationY = getResources().getDisplayMetrics().heightPixels - upperConstraint - boxHeightInPx - boxHeightInPx;

        scoreTV.setText("0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(soundPool!= null){
            soundPool.release();
            soundPool = null;
        }
    }
}