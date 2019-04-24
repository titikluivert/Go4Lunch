package com.ngtiofack.go4lunch.api;


import com.google.firebase.firestore.Query;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;
import com.ngtiofack.go4lunch.model.Message;
import com.ngtiofack.go4lunch.utils.mainUtils;

public class MessageHelper {
    // --- GET ---

    public static Query getAllMessageForChat(String uIdBetween2Chats) {
        return ChatHelper.getChatCollection()
                .document(mainUtils.COLLECTION_NAME)
                .collection(uIdBetween2Chats)
                .orderBy("dateCreated")
                .limit(50);
    }

    public static void createMessageForChat(String textMessage, Go4LunchUsers userSender, String uIdBetween2Chats) {

        // 1 - Create the Message object
        Message message = new Message(textMessage, userSender, uIdBetween2Chats);

        // 2 - Store Message to Firestore
        ChatHelper.getChatCollection()
                .document(mainUtils.COLLECTION_NAME)
                .collection(uIdBetween2Chats)
                .add(message);
    }


}
