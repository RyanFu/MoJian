package net.roocky.moji.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;

import net.roocky.moji.Adapter.WeatherAdapter;
import net.roocky.moji.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roock on 04/02.
 * “关于”Activity
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
