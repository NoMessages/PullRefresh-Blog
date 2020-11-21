package fatcats.top;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.mySelfListView)
    PullRefresh mySelfListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    public void initData(){

        final List<String> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            dataList.add("Item"+i);
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,dataList);
        mySelfListView.setAdapter(arrayAdapter);
        mySelfListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void refreshData() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 10; i < 20; i++) {
                            dataList.add("Item"+i);
                        }
                        arrayAdapter.notifyDataSetChanged();
                        mySelfListView.onRefreshComplate();
                    }

                },2000);
            }
        });
    }

}