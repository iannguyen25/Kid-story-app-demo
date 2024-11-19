package com.g06.kidsstoryapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class SelectChildDialogFragment extends DialogFragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<String> childNames;
    private List<String> childIds;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_child_dialog, null);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        childNames = new ArrayList<>();
        childIds = new ArrayList<>();

        ListView childListView = view.findViewById(R.id.childListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, childNames);
        childListView.setAdapter(adapter);

        childListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedChildId = childIds.get(position);
            AdjustTimeLimitDialogFragment dialogFragment = AdjustTimeLimitDialogFragment.newInstance(selectedChildId);
            dialogFragment.show(getParentFragmentManager(), "AdjustTimeLimit");
            dismiss();
        });

        loadChildren(adapter);

        builder.setView(view)
                .setTitle("Select Child");

        return builder.create();
    }

    private void loadChildren(ArrayAdapter<String> adapter) {
        String parentId = mAuth.getCurrentUser().getUid();
        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        String childName = queryDocumentSnapshots.getDocuments().get(i).getString("name");
                        String childId = queryDocumentSnapshots.getDocuments().get(i).getId();
                        childNames.add(childName);
                        childIds.add(childId);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}