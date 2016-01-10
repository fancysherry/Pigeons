package unique.fancysherry.pigeons.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.Constants;

public abstract class BaseFragment extends Fragment {
    public Socket mSocket;
    {
        try {
            if (mSocket == null)
                mSocket = IO.socket(Constants.BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public BaseFragment() {
        // Required empty public constructor
    }

}
