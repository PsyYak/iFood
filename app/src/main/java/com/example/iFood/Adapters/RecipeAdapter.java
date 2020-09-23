package com.example.iFood.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iFood.Activities.RecipeActivity;
import com.example.iFood.Classes.Recipes;
import com.example.iFood.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**

 * This adapter hold all the information related to the recipe
 * and allow the user to move to Recipe Activity to view all the information regarding the recipe.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyHolder> {
    String userName,userRole,check;
    private Context mContext;
    private List<Recipes> mData;
    private String activity;

    public RecipeAdapter(Context mContext, List<Recipes> mData,String activity){
        this.mContext = mContext;
        this.mData = mData;
        this.activity = activity;
    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view ;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.cardview_recipe,viewGroup,false);
        return  new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
        Intent  intent = ((Activity) mContext).getIntent();
        userName = intent.getStringExtra("username");
        userRole = intent.getStringExtra("userRole");
        check = String.valueOf(mData.get(i).isApproved());
        if(mData.get(i).getRecipePicture()!=null && !mData.get(i).getRecipePicture().isEmpty()) {
            Picasso.get().load(mData.get(i).getRecipePicture()).into(myHolder.img_recipe);

        }
        myHolder.recipeTitle.setText(mData.get(i).getRecipeName());
        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecipeActivity.class);
                intent.putExtra("activity",activity);
                intent.putExtra("addedBy",mData.get(i).getAddedBy());
                intent.putExtra("approved",check);
                intent.putExtra("username",userName);
                intent.putExtra("userRole",userRole);
                intent.putExtra("RecipeName",mData.get(i).getRecipeName());
                intent.putExtra("RecipeIngredients",mData.get(i).getRecipeIngredients());
                intent.putExtra("RecipeMethodTitle",mData.get(i).getRecipeMethodTitle());
                intent.putExtra("Recipe",mData.get(i).getRecipe());
                intent.putExtra("id",mData.get(i).getId());
                intent.putExtra("Thumbnail",mData.get(i).getRecipePicture());


                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView recipeTitle;
        CardView cardView;
        ImageView img_recipe;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            recipeTitle = itemView.findViewById(R.id.recipe_text);
            img_recipe = itemView.findViewById(R.id.recipe_img_id);
            cardView = itemView.findViewById(R.id.cardview_id);


        }
    }
}
