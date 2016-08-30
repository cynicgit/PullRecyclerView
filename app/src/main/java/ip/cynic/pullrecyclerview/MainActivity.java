package ip.cynic.pullrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void nomarl(View v) {
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    public void gank(View v) {
        startActivity(new Intent(this, GankActivity.class));
    }

}
