package com.daniel.alexa.baker.stream;


import com.daniel.alexa.baker.InfoItem;


public class StreamInfoItem extends InfoItem {
    private final StreamType streamType;

    private String uploaderName;
    private String uploadDate;
    private long viewCount = -1;
    private long duration = -1;

    private String uploaderUrl = null;

    public StreamInfoItem(int serviceId, String url, String name, StreamType streamType) {
        super(InfoType.STREAM, serviceId, url, name);
        this.streamType = streamType;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploader_name) {
        this.uploaderName = uploader_name;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String upload_date) {
        this.uploadDate = upload_date;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long view_count) {
        this.viewCount = view_count;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public void setUploaderUrl(String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
    }

    @Override
    public String toString() {
        return "StreamInfoItem{" +
                "streamType=" + streamType +
                ", uploaderName='" + uploaderName + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", viewCount=" + viewCount +
                ", duration=" + duration +
                ", uploaderUrl='" + uploaderUrl + '\'' +
                ", infoType=" + getInfoType() +
                ", serviceId=" + getServiceId() +
                ", url='" + getUrl() + '\'' +
                ", name='" + getName() + '\'' +
                ", thumbnailUrl='" + getThumbnailUrl() + '\'' +
                '}';
    }
}