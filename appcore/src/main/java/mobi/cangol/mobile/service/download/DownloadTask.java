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
package mobi.cangol.mobile.service.download;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.Future;

import mobi.cangol.mobile.http.download.DownloadHttpClient;
import mobi.cangol.mobile.http.download.DownloadResponseHandler;
import mobi.cangol.mobile.service.PoolManager.Pool;

public class DownloadTask {
    private Pool pool;
    private DownloadResource downloadResource;
    private DownloadHttpClient downloadHttpClient;
    private Future<?> future;
    private Handler handler;
    private boolean running;
    private DownloadNotification downloadNotification;
    private DownloadResponseHandler responseHandler = new DownloadResponseHandler() {
        @Override
        public void onWait() {
            super.onWait();
            if (downloadNotification != null) {
                downloadNotification.createNotification();
            }
        }

        @Override
        public void onStart(long start, long length) {
            super.onStart(start, length);
            downloadResource.setStatus(Download.STATUS_START);
            downloadResource.setFileLength(length);
            sendDownloadMessage(Download.ACTION_DOWNLOAD_START, downloadResource);
        }

        @Override
        public void onStop(long end) {
            super.onStop(end);
            downloadResource.setCompleteSize(end);
            sendDownloadMessage(Download.ACTION_DOWNLOAD_STOP, downloadResource);
            if (downloadNotification != null) {
                downloadNotification.cancelNotification();
            }

        }

        @Override
        public void onFinish(long end) {
            super.onFinish(end);
            downloadResource.setStatus(Download.STATUS_FINISH);
            downloadResource.setCompleteSize(end);
            sendDownloadMessage(Download.ACTION_DOWNLOAD_FINISH, downloadResource);
            if (downloadNotification != null) {
                downloadNotification.finishNotification();
            }

        }

        @Override
        public void onProgressUpdate(long end, int progress, int speed) {
            super.onProgressUpdate(end, progress, speed);
            downloadResource.setSpeed(speed);
            downloadResource.setProgress(progress);
            downloadResource.setCompleteSize(end);
            sendDownloadMessage(Download.ACTION_DOWNLOAD_UPDATE, downloadResource);
            if (downloadNotification != null) {
                downloadNotification.updateNotification(progress, speed);//speed 转换
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            downloadResource.setException(content);
            downloadResource.setStatus(Download.STATUS_FAILURE);
            sendDownloadMessage(Download.ACTION_DOWNLOAD_FAILED, downloadResource);
            if (downloadNotification != null) {
                downloadNotification.failureNotification();
            }

        }

    };

    public DownloadTask(DownloadResource downloadResource, Pool pool, Handler handler) {
        this(downloadResource, pool, handler, true);
    }

    public DownloadTask(DownloadResource downloadResource, Pool pool, Handler handler, boolean safe) {
        this.downloadResource = downloadResource;
        this.pool = pool;
        this.handler = handler;
        downloadHttpClient = DownloadHttpClient.build(pool.getName(), safe);
        DownloadHttpClient.setThreadPool(pool);
    }

    public void setDownloadNotification(DownloadNotification downloadNotification) {
        this.downloadNotification = downloadNotification;
    }

    protected Future<?> exec(DownloadResource downloadResource, DownloadResponseHandler responseHandler) {
        return downloadHttpClient.send(downloadResource.getKey(), downloadResource.getUrl(), responseHandler, downloadResource.getCompleteSize(), downloadResource.getSourceFile());
    }

    protected void start() {
        downloadResource.setStatus(Download.STATUS_WAIT);
        future = exec(downloadResource, responseHandler);
        running = true;
    }

    protected void restart() {
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
        downloadResource.reset();
        start();
        sendDownloadMessage(Download.ACTION_DOWNLOAD_CONTINUE, downloadResource);
    }

    public void resume() {
        downloadResource.setStatus(Download.STATUS_WAIT);
        future = exec(downloadResource, responseHandler);
        sendDownloadMessage(Download.ACTION_DOWNLOAD_CONTINUE, downloadResource);
        running = true;
    }

    protected void stop() {
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
        future = null;
        downloadResource.setStatus(Download.STATUS_STOP);
        sendDownloadMessage(Download.ACTION_DOWNLOAD_STOP, downloadResource);
        running = false;
    }

    public void interrupt() {
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
        future = null;
        downloadResource.setStatus(Download.STATUS_RERUN);
        sendDownloadMessage(Download.ACTION_DOWNLOAD_STOP, downloadResource);
        running = false;
    }

    protected void remove() {
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
        future = null;
        sendDownloadMessage(Download.ACTION_DOWNLOAD_DELETE, downloadResource);
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void sendDownloadMessage(int what, DownloadResource obj) {
        final Message msg = handler.obtainMessage(what);
        msg.obj = obj;
        msg.sendToTarget();
    }

}
