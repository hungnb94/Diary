package example.com.hb.diary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;

import static example.com.hb.diary.utils.Constant.ARG_MESSAGE;
import static example.com.hb.diary.utils.Constant.ARG_SMALL_TITLE;
import static example.com.hb.diary.utils.Constant.ARG_TITLE;

public class AlertFailedFragment extends Fragment {

    @BindView(R.id.rootLayout)
    View rootLayout;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSmallTitle)
    TextView tvSmallTitle;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    private String title;
    private String message;
    private String smallTitle;

    public AlertFailedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(ARG_TITLE, "");
            message = bundle.getString(ARG_MESSAGE, "");
            smallTitle = bundle.getString(ARG_SMALL_TITLE, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_failed, container, false);
        ButterKnife.bind(this, view);
        App app = (App) getActivity().getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        tvTitle.setText(title);
        tvSmallTitle.setText(smallTitle);
        tvMessage.setText(message);
        return view;
    }

    @OnClick(R.id.ivClose)
    void clickImageClose(View view) {
        closeFragment();
    }

    @OnClick(R.id.btnClose)
    void clickButtonClose(View view) {
        closeFragment();
    }

    private void closeFragment() {
        getActivity().getSupportFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
