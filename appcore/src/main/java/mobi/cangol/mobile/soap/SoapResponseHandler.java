/**
 * Copyright (c) 2013 Cangol.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.soap;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author Cangol
 */
public class SoapResponseHandler {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;

    private Handler handler;

    public SoapResponseHandler() {
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    SoapResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

    public void onStart() {
        //do nothings
    }

    public void onFinish() {
        //do nothings
    }

    public void onSuccess(String content) {
        //do nothings
    }

    public void onFailure(String error) {
        //do nothings
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }

    protected void sendSuccessMessage(String responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, responseBody));
    }

    protected void sendFailureMessage(String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, responseBody));
    }

    protected void handleMessage(Message msg) {
        Object response;
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = msg.obj;
                onSuccess((String) response);
                break;
            case FAILURE_MESSAGE:
                response = msg.obj;
                onFailure((String) response);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
            default:
                break;
        }
    }

    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if (handler != null) {
            msg = this.handler.obtainMessage(responseMessage, response);
        } else {
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }

    void sendResponseMessage(String response) {
        sendSuccessMessage(response);
    }
}
