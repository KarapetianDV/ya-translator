package ru.karapetiandav.ya_translator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ru.karapetiandav.ya_translator.R;


public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        TextView bigTranslateView = (TextView) findViewById(R.id.bigTranslateView);

        Intent intent = getIntent();
        if (intent != null)
            bigTranslateView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
    }
}
