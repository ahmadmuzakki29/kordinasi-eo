/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.muzakki.ahmad.lib.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.muzakki.ahmad.lib.Helper;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String tag = data.get("tag");
        if(tag==null){
            Log.e("jeki","messsage tag null, id : "+data.get("id"));
            return;
        }

        Log.d("jeki","message received with tag:"+tag);
        Intent in = new Intent(tag);
        in.putExtras(Helper.mapToBundle(data));
        sendBroadcast(in);
    }

}
