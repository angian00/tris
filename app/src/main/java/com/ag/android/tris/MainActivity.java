package com.ag.android.tris;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return GameFragment.newInstance();
    }
}