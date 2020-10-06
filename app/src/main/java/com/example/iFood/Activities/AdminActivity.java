package com.example.iFood.Activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iFood.Activities.Add_Recipe.addRecipe_New;
import com.example.iFood.Activities.Inbox.Inbox_new;
import com.example.iFood.Activities.oldActivities.AddRecipe;
import com.example.iFood.Activities.oldActivities.Inbox;
import com.example.iFood.Classes.Recipes;
import com.example.iFood.Classes.Users;
import com.example.iFood.MenuFragments.AddDrawFragment;
import com.example.iFood.MenuFragments.NavDrawFragment;
import com.example.iFood.R;
import com.example.iFood.Utils.ConnectionBCR;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "AdminActivity";
    // DB Connection / related
    DatabaseReference refRecipes = FirebaseDatabase.getInstance().getReference().child("Recipes");
    DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("Users");
    int userCount =0, recipeCount =0,userTotalCount=0,recipeTotalCount=0;
    // Bottom Bar
    BottomAppBar bottomAppBar;
    FloatingActionButton addIcon;
    // Button
    Button btnSearch,btnOk,btnDismiss;

    // Date Variables
    Date to,from;
    long userTime,recipeTime;
    int mYear, mMonth, mDay;
    // PieChart
    PieChartView usersChart,recipesChart;
    List<SliceValue> usersPieData = new ArrayList<>();
    List<SliceValue> recipesPieData = new ArrayList<>();
    PieChartData usersPieChart,recipesPieChart;
    // TextView
    TextView fromDate,toDate;
    // Broadcast
    ConnectionBCR bcr = new ConnectionBCR();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        setVariables();
        setCurrentDateonOpen();
        getDB_Data();


        // onClick Listeners
        btnSearch.setOnClickListener(v -> {
            if(to==null || from==null){

                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
                try {
                    if(to==null) to = formatter.parse(toDate.getText().toString());
                    if(from==null)from = formatter.parse(toDate.getText().toString());
                    else{
                        from = formatter.parse(toDate.getText().toString());
                        to = formatter.parse(toDate.getText().toString());
                    }

                  //  Log.d(TAG, "onClick: toDate:"+to.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            else if (to.getTime() < from.getTime()) {
                Toast.makeText(AdminActivity.this,"Search credentials are not valid",Toast.LENGTH_SHORT).show();
            }
            else {
                usersPieData=null;
                recipesPieData=null;
                refUsers.orderByKey();
                refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){
                          Users u = ds.getValue(Users.class);
                          assert u != null;
                          userTime = (Long)u.timestamp;
                         //   @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format));
                         //   String c = simpleDateFormat.format(userTime);
                            //Log.d("TAG","user date: "+c);
                          Range<Long> timeRange = Range.create(from.getTime(),
                                  to.getTime());
                          if(timeRange.contains(userTime)){

                           //   c = simpleDateFormat.format(userTime);
                              //Log.d("TAG","Found user match to date: "+c);
                              userCount++;
                              Log.d("TAG","User count is:"+userCount);
                          }
                      }
                        if(userCount>0) {
                            if (userCount != userTotalCount){
                                usersPieData.add(new SliceValue(userCount, Color.GREEN).setLabel("Users :" + userCount));
                            }
                            usersPieData.add(new SliceValue(userTotalCount, Color.parseColor("#FF5252")).setLabel("Total Users :"+userTotalCount));
                            userCount=0;
                        }else{
                            usersPieData.add(new SliceValue(userTotalCount, Color.parseColor("#FF5252")).setLabel("Total Users :"+userTotalCount));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                refRecipes.orderByKey();
                refRecipes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()) {
                            for (DataSnapshot dsResult : ds.getChildren()) {
                                Recipes rec = dsResult.getValue(Recipes.class);
                                assert rec != null;

                                recipeTime = (Long) rec.timestamp;
                              //  @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format));
                                //String c = simpleDateFormat.format(recipeTime);
                               // Log.d("TAG"," recipe date: "+c);
                                Range<Long> recipeTimeRange = Range.create(from.getTime(),
                                        to.getTime());
                                if (recipeTimeRange.contains(recipeTime)) {

                                 //  c = simpleDateFormat.format(recipeTime);
                                  //  Log.d("TAG","Found recipe match to date: "+c);
                                    recipeCount++;
                                   Log.d("TAG","Recipe count is:"+recipeCount);
                                }
                            }

                        }
                        if(recipeCount>0) {
                            if (recipeCount != recipeTotalCount) {
                                recipesPieData.add(new SliceValue(recipeCount, Color.RED).setLabel("Recipes :" + recipeCount));
                            }
                            recipesPieData.add(new SliceValue(recipeTotalCount, Color.parseColor("#3F51B5")).setLabel("Total Recipes :"+recipeTotalCount));
                            recipeCount = 0;
                        }else{
                            recipesPieData.add(new SliceValue(recipeTotalCount, Color.parseColor("#3F51B5")).setLabel("Total Recipes :"+recipeTotalCount));
                        }
                        setChart();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });
        // more onClick listeners with over ride their onClick method
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
    } // onCreate ends

    private void setChart(){
        recipesPieChart = new PieChartData(recipesPieData);
        usersPieChart = new PieChartData(usersPieData);

        //
        usersPieChart.setHasLabels(true).setValueLabelTextSize(10);
        usersPieChart.setValueLabelsTextColor(Color.parseColor("#FFFFFF"));
        //usersPieChart.setHasLabelsOutside(true);
        //
        recipesPieChart.setHasLabels(true).setValueLabelTextSize(10);
        recipesPieChart.setValueLabelsTextColor(Color.parseColor("#FFFFFF"));
        //recipesPieChart.setHasLabelsOutside(true);
        //
        recipesChart.setPieChartData(recipesPieChart);
        usersChart.setPieChartData(usersPieChart);
    }
    private void setVariables() {
        usersChart = findViewById(R.id.chartUsers);
        recipesChart = findViewById(R.id.chartRecipe);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        btnSearch = findViewById(R.id.btnSearchAdmin);

    }


    private void getDB_Data(){
        Query dbQuery = refRecipes.orderByKey();
        dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    recipeTotalCount = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                    //recipeAmount.setText(String.valueOf(snapshot.getChildrenCount()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query dbUsersQuery = refUsers.orderByKey();
        dbUsersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userTotalCount= Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                   // usersAmount.setText(String.valueOf(snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Convert Date to Text.
     * @param v gets the onClickListner that was clicked
     */
    @Override
    public void onClick(View v) {

        if (v == toDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        toDate.setText( dayOfMonth+ "-" + (monthOfYear + 1) + "-" + year);
                        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
                        try {
                            to = formatter.parse(toDate.getText().toString());
                            Log.d(TAG, "onClick: toDate:"+to.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        }
        if(v == fromDate){
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        fromDate.setText( dayOfMonth+ "-" + (monthOfYear + 1) + "-" + year);
                        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
                        try {
                           from = formatter.parse(fromDate.getText().toString());
                            Log.d(TAG, "onClick: fromDate:"+from.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
    }


    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_Exit:
                final Dialog myDialog = new Dialog(AdminActivity.this);
                myDialog.setContentView(R.layout.dialog);
                btnDismiss = myDialog.findViewById(R.id.btnDismiss);
                btnOk =  myDialog.findViewById(R.id.btnOk);

                // if pressed Ok will close the App
                btnOk.setOnClickListener(v -> {

                    SharedPreferences.Editor delData = getSharedPreferences("userData",MODE_PRIVATE).edit();
                    delData.clear();
                    delData.apply();
                    finishAffinity();
                });
                // if pressed Dismiss will stay in the App
                btnDismiss.setOnClickListener(v -> myDialog.dismiss());
                myDialog.show();
                break;
            case R.id.menuProfile:
                Intent profile = new Intent(AdminActivity.this, ProfileActivity.class);
                profile.putExtra("username",getIntent().getStringExtra("username"));
                profile.putExtra("userRole",getIntent().getStringExtra("userRole"));
                startActivity(profile);
                finish();
                break;
            case R.id.menu_MyRecepies:
                Intent myRecipes = new Intent(AdminActivity.this, MyRecipes.class);
                myRecipes.putExtra("username",getIntent().getStringExtra("username"));
                myRecipes.putExtra("userRole",getIntent().getStringExtra("userRole"));
                startActivity(myRecipes);
                break;

            case R.id.menu_SearchRecepie:
                Intent search = new Intent(AdminActivity.this, SearchRecipe.class);
                search.putExtra("username",getIntent().getStringExtra("username"));
                search.putExtra("userRole",getIntent().getStringExtra("userRole"));
                startActivity(search);
                break;
            case R.id.menuInbox:
                Intent inbox = new Intent(AdminActivity.this, Inbox_new.class);
                inbox.putExtra("username",getIntent().getStringExtra("username"));
                inbox.putExtra("userRole",getIntent().getStringExtra("userRole"));
                startActivity(inbox);
                break;
            case R.id.menuHome:
                Intent main = new Intent(AdminActivity.this, MainActivity.class);
                main.putExtra("username",getIntent().getStringExtra("username"));
                main.putExtra("userRole",getIntent().getStringExtra("userRole"));
                startActivity(main);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Register our Broadcast Receiver when opening the app.
     */
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(bcr,filter);
    }

    /**
     * Stop our Broadcast Receiver when the app is closed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(bcr);
    }

    private void setCurrentDateonOpen(){
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        String date =  currentDay +"-"+ (currentMonth+1) +"-"+ currentYear;
        toDate.setText(date);
        fromDate.setText(date);
    }
}