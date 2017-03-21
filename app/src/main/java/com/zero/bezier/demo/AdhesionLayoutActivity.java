package com.zero.bezier.demo;

import android.app.Activity;
import android.os.Bundle;

import com.zero.bezier.R;
import com.zero.bezier.widget.adhesion.AdhesionLayout;

/**
 * @author linzewu
 * @date 2016/6/5
 */
public class AdhesionLayoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhesion);
        AdhesionLayout adhesionLayout = (AdhesionLayout) findViewById(R.id.adhesion);
        adhesionLayout.setOnAdherentListener(new AdhesionLayout.OnAdherentListener() {
            @Override
            public void onDismiss() {
                
            }
        });
    }
}
