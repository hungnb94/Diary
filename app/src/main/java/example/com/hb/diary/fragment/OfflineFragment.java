package example.com.hb.diary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.hb.diary.App;
import example.com.hb.diary.R;

public class OfflineFragment extends Fragment {
    @BindView(R.id.rootLayout)
    View rootLayout;

    public OfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_offline, container, false);
        ButterKnife.bind(this, view);
        App app = (App) getActivity().getApplication();
        rootLayout.setBackgroundColor(app.getBackgroundColor());
        return view;
    }


    @OnClick(R.id.ivClose)
    void clickImageClose(View view){
        closeFragment();
    }

    @OnClick(R.id.btnClose)
    void clickButtonClose(View view){
        closeFragment();
    }

    private void closeFragment() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
