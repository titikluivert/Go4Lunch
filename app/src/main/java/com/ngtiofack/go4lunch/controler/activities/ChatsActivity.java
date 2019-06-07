package com.ngtiofack.go4lunch.controler.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.api.Go4LunchUserHelper;
import com.ngtiofack.go4lunch.api.MessageHelper;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;
import com.ngtiofack.go4lunch.model.Message;
import com.ngtiofack.go4lunch.utils.mainUtils;
import com.ngtiofack.go4lunch.view.ChatAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatsActivity extends BaseActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.activity_chat_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.activity_mentor_chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;


    @BindView(R.id.activity_mentor_chat_message_edit_text)
    TextInputEditText editTextMessage;

    // FOR DATA
    // 2 - Declaring Adapter and data
    private ChatAdapter chatAdapter;
    @Nullable
    private Go4LunchUsers modelCurrentUser;
    private String uniqueIdBetween2Chats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        ButterKnife.bind(this);

        uniqueIdBetween2Chats = Long.toString(mainUtils.convertHexToDecimal(getIntent().getStringExtra(getString(R.string.receiverId)), this.getCurrentUser().getUid()));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.configureRecyclerView();
        this.configureToolbar();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra(getString(R.string.name_chat_person)));
        this.getCurrentUserFromFirestore();

    }

    // --------------------
    // ACTIONS
    // --------------------

    private void configureToolbar() {
        // Get the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.activity_mentor_chat_add_file_button)
    public void onClickAddFile() { }

    @OnClick(R.id.activity_mentor_chat_send_button)
    public void onClickSendMessage() {
        // 1 - Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null) {
            // 2 - Create a new Message to Firestore
            MessageHelper.createMessageForChat(Objects.requireNonNull(editTextMessage.getText()).toString(), modelCurrentUser,  uniqueIdBetween2Chats);
            // 3 - Reset text field
            this.editTextMessage.setText("");
        }
    }
    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore() {
        Go4LunchUserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> modelCurrentUser = documentSnapshot.toObject(Go4LunchUsers.class));
    }

    // - Configure RecyclerView with a Query
    private void configureRecyclerView() {

        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(uniqueIdBetween2Chats)), Glide.with(this), this, this.getCurrentUser().getUid(),  uniqueIdBetween2Chats);
        /**/
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.chatAdapter);
    }
    // - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {

          return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }
    // --------------------
    // CALLBACK
    // --------------------
    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}