/**
 * Copyright (c) 2013 Cangol
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
package mobi.cangol.mobile.http.polling;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;

import mobi.cangol.mobile.logging.Log;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PollingResponseHandler {
    protected static final int START_MESSAGE = -1;
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int FINISH_MESSAGE = 2;
    private Handler handler;

    public PollingResponseHandler() {
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    PollingResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

    /**
     * 是否结束请求
     *
     * @param content
     * @return
     */
    public boolean isFailResponse(String content) {
        return false;
    }

    /**
     * 启动开始
     */
    public void onStart() {
        //do nothings
    }

    /**
     * 轮询结束
     *
     * @param execTimes
     * @param content
     */
    public void onPollingFinish(int execTimes, String content) {
        //do nothings
    }

    /**
     * 轮询成功
     *
     * @param statusCode
     * @param content
     */
    public void onSuccess(int statusCode, String content) {
        //do nothings
    }

    /**
     * 轮询失败
     *
     * @param error
     * @param content
     */
    public void onFailure(Throwable error, String content) {
        //do nothings
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, new Object[]{-1, "exec start"}));
    }

    protected void sendFinishMessage(int execTimes) {
        sendMessage(obtainMessage(FINISH_MESSAGE, new Object[]{execTimes, "exec finish"}));
    }

    protected void sendSuccessMessage(int statusCode, String responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, responseBody}));
    }

    protected void sendFailureMessage(IOException e, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    protected boolean sendResponseMessage(Response response) {
        boolean result = false;
        final ResponseBody responseBody = response.body();
        String content = null;
        if (response.isSuccessful()) {
            if (responseBody != null) {
                try {
                    content = responseBody.string();
                } catch (IOException e) {
                    Log.e(e.getMessage());
                }
            }
            if (isFailResponse(content)) {
                sendFailureMessage(new IOException("code=" + response.code()), content);
                result = false;
            } else {
                sendSuccessMessage(response.code(), content);
                result = true;
            }
        } else {
            sendFailureMessage(new IOException("code=" + response.code()), content);
            result = false;
        }

        return result;
    }

    protected void handleMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[]) msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (String) response[1]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((Throwable) response[0], (String) response[1]);
                break;
            case FINISH_MESSAGE:
                response = (Object[]) msg.obj;
                handleFinishMessage(((Integer) response[0]).intValue(), (String) response[1]);
                break;
            case START_MESSAGE:
                handleStartMessage();
                break;
                default:break;
        }
    }

    protected void handleStartMessage() {
        onStart();
    }

    protected void handleFinishMessage(int execTimes, String responseBody) {
        onPollingFinish(execTimes, responseBody);
    }

    protected void handleSuccessMessage(int statusCode, String responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        onFailure(e, responseBody);
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
}