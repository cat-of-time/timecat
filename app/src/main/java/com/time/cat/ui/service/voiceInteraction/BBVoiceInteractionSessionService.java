package com.time.cat.ui.service.voiceInteraction;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BBVoiceInteractionSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        return new BBVoiceInteractionSession(this);
    }


}
