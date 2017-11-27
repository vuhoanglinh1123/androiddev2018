package xyz.sonbn.ircclient.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.sonbn.ircclient.R;
import xyz.sonbn.ircclient.activity.AddServerActivity;
import xyz.sonbn.ircclient.activity.ClientActivity;
import xyz.sonbn.ircclient.adapter.ServersAdapter;
import xyz.sonbn.ircclient.irc.IRCBinder;
import xyz.sonbn.ircclient.model.Server;

public class OverviewFragment extends Fragment implements View.OnClickListener, ServersAdapter.ClickListener {
    public static final String TRANSACTION_TAG = "fragment_overview";

    private ClientActivity activity;
    private ServersAdapter adapter;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof ClientActivity)) {
            throw new IllegalArgumentException("Activity has to implement YaaicActivity interface");
        }

        this.activity = (ClientActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        adapter = new ServersAdapter(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        FloatingActionButton addServerBtn = (FloatingActionButton) view.findViewById(R.id.fab);
        addServerBtn.setOnClickListener(this);
        addServerBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.onServerSelected(new Server());
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        activity.setToolbarTitle(getString(R.string.app_name));
        adapter.loadServers();
    }

    @Override
    public void onClick(View view) {
        final Context context = view.getContext();
        Log.d(TRANSACTION_TAG, "Click");
        Intent intent = new Intent(context, AddServerActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onServerSelected(Server server) {
        activity.onServerSelected(server);
    }

    @Override
    public void onConnectToServer(Server server) {
        IRCBinder binder = activity.getBinder();

        if (binder != null){
            binder.connect(server);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDisconnectFromServer(Server server) {

    }

    @Override
    public void onEditServer(Server server) {

    }

    @Override
    public void onDeleteServer(Server server) {

    }
}
