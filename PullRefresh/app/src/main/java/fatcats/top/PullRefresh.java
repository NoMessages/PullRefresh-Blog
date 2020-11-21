package fatcats.top;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PullRefresh extends ListView {

    private static final int STATE_PULL_REFRESH = 0;      //下拉刷新状态
    private static final int STATE_RELEASE_REFRESH = 1;       //松开刷新的状态
    private static final int STATE_REFRESHING_REFRESH = 2;        //正在刷新
    @BindView(R.id.pull_pre_img)
    ImageView pullPreImg;
    @BindView(R.id.pull_tv)
    TextView pullTv;
    @BindView(R.id.pull_next_img)
    ProgressBar pullNextImg;
    private OnRefreshListener onRefreshListener;

    public OnRefreshListener getOnRefreshListener() {
        return onRefreshListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private int CurrentStateCode = STATE_PULL_REFRESH;
    private View headView;
    private int measuredHeight, startY;
    private RotateAnimation imageAnimaRotate, imageAnimaReset;

    public PullRefresh(Context context) {
        super(context);
        initHead();
    }

    public PullRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHead();
    }

    public PullRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHead();
    }


    private void initHead() {
        headView = View.inflate(getContext(), R.layout.pull_lay, null);
        ButterKnife.bind(this, headView);
        this.addHeaderView(headView);
        headView.measure(0, 0); //测量头部布局
        measuredHeight = headView.getMeasuredHeight();
        headView.setPadding(0, -measuredHeight, 0, 0);
        initAnimation();
    }

    public void initAnimation() {
        imageAnimaRotate = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        imageAnimaRotate.setFillAfter(true);
        imageAnimaRotate.setDuration(200);

        imageAnimaReset = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        imageAnimaReset.setFillAfter(true);
        imageAnimaReset.setDuration(200);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    startY = (int) ev.getY();
                }

                if (CurrentStateCode == STATE_REFRESHING_REFRESH) {
                    break;
                }
                int endY = (int) ev.getRawY();
                int des = endY - startY;
                if (des > 0 && CurrentStateCode != STATE_RELEASE_REFRESH) {
                    CurrentStateCode = STATE_RELEASE_REFRESH;
                    headView.setPadding(0, des, 0, 0);
                    refresh();
                } else if (des < 0 && CurrentStateCode != STATE_PULL_REFRESH) {
                    CurrentStateCode = STATE_PULL_REFRESH;
                    refresh();
                }
                return true;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (CurrentStateCode == STATE_RELEASE_REFRESH) {
                    CurrentStateCode = STATE_REFRESHING_REFRESH;
                    refresh();
                } else if (CurrentStateCode == STATE_PULL_REFRESH) {
                    headView.setPadding(0, -measuredHeight, 0, 0);
                    refresh();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    public void refresh() {
        switch (CurrentStateCode) {
            case STATE_PULL_REFRESH:
                pullPreImg.setVisibility(View.VISIBLE);
                pullNextImg.setVisibility(View.GONE);
                pullTv.setText("下拉刷新");
                pullPreImg.startAnimation(imageAnimaReset);
                break;
            case STATE_RELEASE_REFRESH:
                pullPreImg.setVisibility(View.VISIBLE);
                pullNextImg.setVisibility(View.GONE);
                pullTv.setText("释放刷新");
                pullPreImg.startAnimation(imageAnimaRotate);
                break;
            case STATE_REFRESHING_REFRESH:
                pullPreImg.setVisibility(View.GONE);
                pullNextImg.setVisibility(View.VISIBLE);
                pullTv.setText("正在刷新");
                pullPreImg.clearAnimation();
                if(onRefreshListener != null){
                    onRefreshListener.refreshData(); //回调
                }
                break;
        }
    }

    //初始化
    public void onRefreshComplate(){
            CurrentStateCode = STATE_PULL_REFRESH;
            headView.setPadding(0,-measuredHeight,0,0);
    }

}
