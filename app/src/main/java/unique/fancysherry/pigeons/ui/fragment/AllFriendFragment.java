package unique.fancysherry.pigeons.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.socket.client.Socket;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.util.config.SApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFriendFragment extends Fragment {
    private Socket mSocket = SocketIOUtil.getSocket();

    public AllFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_friend, container, false);
    }


}
