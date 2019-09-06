package com.bit7skes.bake;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bit7skes.bake.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v("onReceiveWidget", "onReceiveWidget");
        if(intent.getAction().equals("update_widget")) {
            String cakeName = intent.getStringExtra("cakeName");
            Log.v("cakeName", cakeName);
            List<Ingredient> ingredientList = (List<Ingredient>) intent.getSerializableExtra("ingredientList");

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
            views.setTextViewText(R.id.widget_cake_name_tv, cakeName);
            String textIngredient = "";
            for (Ingredient ingredient: ingredientList) {
                textIngredient += ingredient.getQuantity() + " ";
                textIngredient += ingredient.getMeasure().toLowerCase() + " of ";
                textIngredient += ingredient.getIngredient() + "\n";
            }
            Log.v("textIngredient", textIngredient);
            views.setTextViewText(R.id.widget_ingredient_tv, textIngredient);
            AppWidgetManager.getInstance(context).updateAppWidget(
                    new ComponentName(context, IngredientWidget.class), views
            );
        }
    }
}

