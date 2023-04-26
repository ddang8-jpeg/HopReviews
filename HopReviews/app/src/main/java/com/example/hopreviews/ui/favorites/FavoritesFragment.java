package com.example.hopreviews.ui.favorites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hopreviews.LocationActivity;
import com.example.hopreviews.MainActivity;
import com.example.hopreviews.databinding.FragmentFavoritesBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private ListView listView;
    ArrayList<String> favorites;
    DatabaseReference ref;
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        FavoritesViewModel favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        setHasOptionsMenu(true);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.favoritesList;
        favorites = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, favorites);
        SharedPreferences sp = getActivity().getSharedPreferences("email", Context.MODE_PRIVATE);
        String userEmail = sp.getString("email", "");
        ref.child(encodeEmail(userEmail)).child("favorites").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String location = snapshot.getKey();
                if (snapshot.getValue(Boolean.class)) {
                    String item = createListItem(location);
                    favorites.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String location = snapshot.getKey();
                if (!snapshot.getValue(Boolean.class)) {
                    String item = createListItem(location);
                    favorites.remove(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                String location = item.split("\n")[1];
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                intent.putExtra("name", location);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }

    private String createListItem(String location) {
        return "\n" + location + "\n";
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setVisible(false);
    }
}