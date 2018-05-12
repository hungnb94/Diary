package example.com.hb.diary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;
import example.com.hb.diary.activity.MainActivity;

public class PasscodeChangeFragment extends Fragment {
    public static final String MESSAGE = "message";
    @BindView(R.id.layoutPasscodeChange)
    RelativeLayout rootLayout;
    @BindView(R.id.tvMessage)
    TextView textView;
    @BindView(R.id.ivClose)
    ImageView imageView;
    @BindView(R.id.btnClose)
    Button button;

    // TODO: Rename and change types of parameters
    private String message;

    public PasscodeChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_passcode_change, container, false);

        ButterKnife.bind(this, view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarChangePasscode);
        App app = (App) getActivity().getApplication();
        int backgroundColor = app.getBackgroundColor();
        rootLayout.setBackgroundColor(backgroundColor);
        textView.setText(message);
        return view;
    }

    @OnClick(R.id.ivClose)
    public void clickImageClose(View view){
        close();
    }

    @OnClick(R.id.btnClose)
    public void clickButtonClose(View view){
        close();
    }

    private void close() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
