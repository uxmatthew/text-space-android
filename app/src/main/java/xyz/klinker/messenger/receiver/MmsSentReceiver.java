/*
 * Copyright (C) 2016 Jacob Klinker
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

package xyz.klinker.messenger.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.List;

import xyz.klinker.messenger.data.DataSource;
import xyz.klinker.messenger.data.model.Message;
import xyz.klinker.messenger.util.SmsMmsUtil;

/**
 * Receiver which gets a notification when an MMS message has finished sending. It will mark the
 * message as sent in the database by default. We also need to add functionality for marking it
 * as sent in our own database.
 */
public class MmsSentReceiver extends com.klinker.android.send_message.MmsSentReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Uri uri = Uri.parse(intent.getStringExtra(EXTRA_CONTENT_URI));
        Cursor message = SmsMmsUtil.getMmsMessage(context, uri, null);

        if (message != null && message.moveToFirst()) {
            List<ContentValues> mmsParts = SmsMmsUtil.processMessage(message, -1, context);
            message.close();

            DataSource source = DataSource.getInstance(context);
            source.open();

            for (ContentValues values : mmsParts) {
                Cursor messages = source.searchMessages(values.getAsString(Message.COLUMN_DATA));

                if (messages != null && messages.moveToFirst()) {
                    long id = messages.getLong(0);
                    source.updateMessageType(id, Message.TYPE_SENT);
                    messages.close();
                }
            }

            source.close();
        }
    }

}
