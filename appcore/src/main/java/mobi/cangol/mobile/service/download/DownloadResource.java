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

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;


public class DownloadResource implements Serializable {
    //下载的URL
    protected String url;
    //下载的文件名
    protected String fileName;
    //文件长度
    protected long fileLength;
    //已完成长度
    protected long completeSize;
    //进度
    protected int progress;
    //速度 b/s
    protected long speed;
    //开始,暂停,安装,运行
    protected int status;
    //本地文件更目录
    protected String localPath;
    //异常说明
    protected String exception;
    //源文件地址
    protected String sourceFile;
    //配置地址
    protected String confFile;
    //下载对象 json 字符串
    protected String object;
    //唯一标示
    protected String key;

    protected transient DownloadTask downloadTask;

    protected transient HashMap<Object, BaseViewHolder> viewHolders = new HashMap<>();

    public DownloadResource() {
    }

    public DownloadResource(String localPath, String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
        this.key = url;
        this.localPath = localPath;
        this.sourceFile = localPath + File.separator + fileName + Download.SUFFIX_SOURCE;
        this.confFile = localPath + File.separator + fileName + Download.SUFFIX_CONFIG;
    }

    public BaseViewHolder getViewHolder(Object obj) {
        return viewHolders.get(obj);
    }

    public void setViewHolder(Object obj, BaseViewHolder viewHolder) {
        if (viewHolders == null) {
            viewHolders = new HashMap<>();
        }
        this.viewHolders.put(obj, viewHolder);
    }

    public long getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DownloadResource that = (DownloadResource) o;

        return key.equals(that.key);

    }

    public void reset() {
        completeSize = 0;
        progress = 0;
        speed = 0;
        status = 0;
    }
}
