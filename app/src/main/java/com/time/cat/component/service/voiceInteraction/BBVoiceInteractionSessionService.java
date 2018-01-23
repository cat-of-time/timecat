package com.time.cat.component.service.voiceInteraction;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

/**
 * Created by penglu on 2016/12/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BBVoiceInteractionSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        return new BBVoiceInteractionSession(this);
    }


}
