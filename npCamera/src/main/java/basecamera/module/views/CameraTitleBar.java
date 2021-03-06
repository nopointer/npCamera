package basecamera.module.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import basecamera.module.lib.R;


/**
 * Created by wangquan on 18/6/23.
 */

public class CameraTitleBar extends RelativeLayout {
    //根组件
    private View rootView;
    //左右区域
    private View leftView, rightView;
    //左右图标
    private ImageView leftImageView, rightImageView;
    //左右文字和标题
    private TextView leftTxtView, titleTxtView, rightTxtView;

    public CameraTitleBar(Context context) {
        super(context);
        initView();
    }

    public CameraTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CameraTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        //加载根布局
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.basecamera_title_bar_layout, this, true);
        //初始化各组件
        leftView = rootView.findViewById(R.id.leftView);
        rightView = rootView.findViewById(R.id.rightView);

        leftImageView = rootView.findViewById(R.id.left_icon_view);
        rightImageView = rootView.findViewById(R.id.right_icon_view);

        leftTxtView = rootView.findViewById(R.id.leftText);
        titleTxtView = rootView.findViewById(R.id.title_txtView);
        rightTxtView = rootView.findViewById(R.id.rightText);

    }

    /**
     * 设置背景颜色
     *
     * @param argColor
     */
    public void setBackgroundColor(int argColor) {
        View view = (View) getParent();
        view.setBackgroundColor(argColor);
    }

    /**
     * 设置背景图片
     *
     * @param argBgId
     */
    public void setBackgroundResource(int argBgId) {
        View view = (View) getParent();
        view.setBackgroundResource(argBgId);
    }

    /**
     * 设置标题
     *
     * @param argTitle
     */
    public void setTitle(String argTitle) {
        titleTxtView.setText(argTitle);
    }

    /**
     * 设置标题
     *
     * @param argTitleId
     */
    public void setTitle(int argTitleId) {
        titleTxtView.setText(argTitleId);
    }

    /**
     * 设置标题文字颜色
     *
     * @param argColor
     */
    public void setTitleColor(int argColor) {
        titleTxtView.setTextColor(getContext().getResources().getColor(argColor));
    }

    /**
     * 设置标题文字颜色
     *
     * @param argColor
     */
    public void setTitleColor(String argColor) {
        titleTxtView.setTextColor(Color.parseColor(argColor));
    }


    /**
     * 设置左边图标
     *
     * @param argRID
     */
    public void setLeftImage(int argRID) {
        if (argRID == -1) {
            leftImageView.setVisibility(GONE);
        } else {
            leftImageView.setVisibility(View.VISIBLE);
            leftImageView.setImageResource(argRID);
        }
    }

    public void setTitleBg(int resId){
        rootView.setBackgroundResource(resId);
    }

    /**
     * 设置左边图标
     *
     * @param argRID
     */
    public void setRightImage(int argRID) {
        rightImageView.setVisibility(View.VISIBLE);
        rightImageView.setImageResource(argRID);
    }

    /**
     * 设置左边文字
     *
     * @param argText
     */
    public void setLeftText(String argText) {
        leftTxtView.setVisibility(View.VISIBLE);
        leftTxtView.setText(argText);
    }

    /**
     * 设置左边文字
     *
     * @param argRID
     */
    public void setLeftText(int argRID) {
        leftTxtView.setVisibility(View.VISIBLE);
        leftTxtView.setText(argRID);
    }


    /**
     * 设置左边文字
     *
     * @param argText
     */
    public void setRightText(String argText) {
        rightTxtView.setVisibility(View.VISIBLE);
        rightTxtView.setText(argText);
    }

    /**
     * 设置左边文字
     *
     * @param argRID
     */
    public void setRightText(int argRID) {
        rightTxtView.setVisibility(View.VISIBLE);
        rightTxtView.setText(argRID);
    }

    /**
     * 设置左边区域点击事件
     *
     * @param argOnClickListener
     */
    public void setLeftViewOnClickListener(OnClickListener argOnClickListener) {
        leftView.setOnClickListener(argOnClickListener);
    }

    /**
     * 设置左边区域点击事件
     *
     * @param argOnClickListener
     */
    public void setRightViewOnClickListener(OnClickListener argOnClickListener) {
        rightView.setOnClickListener(argOnClickListener);
    }
}

