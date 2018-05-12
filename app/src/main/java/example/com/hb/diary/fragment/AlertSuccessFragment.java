package example.com.hb.diary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.activity.MainActivity;

public class AlertSuccessFragment extends Fragment {
    public static final String ARG_TITLE = "title";
    public static final String ARG_CONTENT = "content";
    String title, content;

    @BindView(R.id.rootLayout)
    View rootLayout;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvContent)
    TextView tvContent;

    public AlertSuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE);
            content = args.getString(ARG_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_success, container, false);
        ButterKnife.bind(this, view);
        App app = (App) getActivity().getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        if (title!= null && content!=null) {
            tvTitle.setText(title);
            tvContent.setText(content);
        }
        return view;
    }

    @OnClick(R.id.ivClose)
    void clickImageClose(View view){
        closeActivity();
    }

    @OnClick(R.id.btnClose)
    void clickButtonClose(View view){
        closeActivity();
    }

    private void closeActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
        getActivity().finish();
    }
}
