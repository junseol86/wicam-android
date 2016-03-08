package com.wicam.a_common_utils.account_related.item_detail_comment;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class CommentData extends ItemAndCommentData {
    // 댓글만을 포함하는 데이터

    private String item_comment_id, item_id, url_link, comment, writer_id, writer_nickname, write_time;
    private int likes, my_like;
    private int has_photo;

    public CommentData(String item_comment_id, String item_id, String url_link, String comment, String writer_id, String writer_nickname, String write_time,
                       int likes, int my_like, int report, int has_photo,
                       boolean toShowLoading, boolean empty) {
        this.item_comment_id = item_comment_id;
        this.item_id = item_id;
        this.url_link = url_link;
        this.comment = comment;
        this.writer_id = writer_id;
        this.writer_nickname = writer_nickname;
        this.write_time = write_time;
        this.likes = likes;
        this.my_like = my_like;
        this.report = report;
        this.has_photo = has_photo;
        this.toShowLoading = toShowLoading;
        this.empty = empty;
    }

    public String getItem_comment_id() {
        return item_comment_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getUrl_link() {
        return url_link;
    }

    public String getComment() {
        return comment;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public String getWriter_nickname() {
        return writer_nickname;
    }

    public String getWrite_time() {
        return write_time;
    }

    public int getHas_photo() {
        return has_photo;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getMy_like() {
        return my_like;
    }

    public void setMy_like(int my_like) {
        this.my_like = my_like;
    }

    public int getReport() {
        return report;
    }

    public boolean isToShowLoading() {
        return toShowLoading;
    }

    public boolean isEmpty() {
        return empty;
    }
}
