package com.github.maxopoly.artemis.rabbit.requests;

public class SendPlayerTextComponent {

    public static final String REQUEST_ID = "art_req_send_player_text_comp";
    public static final String REPLY_ID = "art_ans_send_player_text_comp";

    private SendPlayerTextComponent() {}


    public enum FailureReason {
        PLAYER_DOES_NOT_EXIST;
    }

}
