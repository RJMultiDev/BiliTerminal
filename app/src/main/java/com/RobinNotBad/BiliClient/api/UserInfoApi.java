package com.RobinNotBad.BiliClient.api;

import com.RobinNotBad.BiliClient.model.ApiResponse;
import com.RobinNotBad.BiliClient.model.ArticleCard;
import com.RobinNotBad.BiliClient.model.LiveRoom;
import com.RobinNotBad.BiliClient.model.UserInfo;
import com.RobinNotBad.BiliClient.model.VideoCard;
import com.RobinNotBad.BiliClient.util.DmImgParamUtil;
import com.RobinNotBad.BiliClient.util.GsonUtil;
import com.RobinNotBad.BiliClient.util.NetWorkUtil;
import com.RobinNotBad.BiliClient.util.SharedPreferencesUtil;
import com.RobinNotBad.BiliClient.util.StringUtil;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UserInfoApi {

    public static class CardData {
        @SerializedName("following")
        public boolean following;
        @SerializedName("follower")
        public int follower;
        @SerializedName("card")
        public CardDetail card;
    }

    public static class CardDetail {
        @SerializedName("name")
        public String name;
        @SerializedName("face")
        public String face;
        @SerializedName("sign")
        public String sign;
        @SerializedName("level_info")
        public LevelInfo level_info;
        @SerializedName("attention")
        public int attention;
        @SerializedName("Official")
        public OfficialInfo Official;
        @SerializedName("vip")
        public VipInfo vip;
        @SerializedName("is_senior_member")
        public int is_senior_member;
    }

    public static class LevelInfo {
        @SerializedName("current_level")
        public int current_level;
    }

    public static class OfficialInfo {
        @SerializedName("role")
        public int role;
        @SerializedName("title")
        public String title;
    }

    public static class VipInfo {
        @SerializedName("status")
        public int status;
        @SerializedName("role")
        public int role;
        @SerializedName("nickname_color")
        public String nickname_color;
    }

    public static class SpaceInfoData {
        @SerializedName("sys_notice")
        public SysNotice sys_notice;
        @SerializedName("live_room")
        public SpaceLiveRoom live_room;
        @SerializedName("contract")
        public ContractInfo contract;
    }

    public static class SysNotice {
        @SerializedName("content")
        public String content;
    }

    public static class SpaceLiveRoom {
        @SerializedName("roomStatus")
        public int roomStatus;
        @SerializedName("liveStatus")
        public int liveStatus;
        @SerializedName("title")
        public String title;
        @SerializedName("cover")
        public String cover;
        @SerializedName("roomid")
        public long roomid;
    }

    public static class ContractInfo {
        @SerializedName("is_follow_display")
        public boolean is_follow_display;
    }

    public static class MyInfoData {
        @SerializedName("mid")
        public long mid;
        @SerializedName("name")
        public String name;
        @SerializedName("face")
        public String face;
        @SerializedName("sign")
        public String sign;
        @SerializedName("follower")
        public int follower;
        @SerializedName("level")
        public int level;
        @SerializedName("official")
        public OfficialDescInfo official;
        @SerializedName("level_exp")
        public LevelExp level_exp;
        @SerializedName("is_senior_member")
        public int is_senior_member;
    }

    public static class OfficialDescInfo {
        @SerializedName("role")
        public int role;
        @SerializedName("desc")
        public String desc;
    }

    public static class LevelExp {
        @SerializedName("current_exp")
        public long current_exp;
        @SerializedName("next_exp")
        public long next_exp;
    }

    public static class UserVideoData {
        @SerializedName("list")
        public UserVideoList list;
    }

    public static class UserVideoList {
        @SerializedName("vlist")
        public List<VListItem> vlist;
    }

    public static class VListItem {
        @SerializedName("pic")
        public String pic;
        @SerializedName("play")
        public long play;
        @SerializedName("aid")
        public long aid;
        @SerializedName("bvid")
        public String bvid;
        @SerializedName("author")
        public String author;
        @SerializedName("title")
        public String title;
    }

    public static class UserArticleData {
        @SerializedName("articles")
        public List<ArticleItem> articles;
    }

    public static class ArticleItem {
        @SerializedName("id")
        public long id;
        @SerializedName("title")
        public String title;
        @SerializedName("banner_url")
        public String banner_url;
        @SerializedName("stats")
        public ArticleStats stats;
        @SerializedName("author")
        public ArticleAuthor author;
    }

    public static class ArticleStats {
        @SerializedName("view")
        public int view;
    }

    public static class ArticleAuthor {
        @SerializedName("name")
        public String name;
    }

    public static class NoticeData {
        @SerializedName("data")
        public String data;
    }

    public static UserInfo getUserInfo(long mid) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/web-interface/card?mid=" + mid;
        String json = NetWorkUtil.getJson(url).toString();
        ApiResponse<CardData> resp = GsonUtil.fromJson(json,
                new com.google.gson.reflect.TypeToken<ApiResponse<CardData>>(){}.getType());
        if (resp == null || !resp.isSuccess() || resp.data == null || resp.data.card == null) return null;

        CardData data = resp.data;
        CardDetail card = data.card;

        String notice = "";
        try {
            String noticeJson = NetWorkUtil.getJson("https://api.bilibili.com/x/space/notice?mid=" + mid).toString();
            NoticeData noticeData = GsonUtil.fromJson(noticeJson, NoticeData.class);
            if (noticeData != null && noticeData.data != null) notice = noticeData.data;
        } catch (Exception ignored) {
        }

        String name = card.name;
        String avatar = card.face;
        String sign = card.sign;
        int level = card.level_info != null ? card.level_info.current_level : 0;
        int attention = card.attention;
        int official = card.Official != null ? card.Official.role : 0;
        String officialDesc = card.Official != null ? card.Official.title : "";

        String sys_notice = "";
        LiveRoom liveroom = null;
        boolean is_follow_display = false;
        try {
            JSONObject spaceJson = getUserSpaceInfo(mid);
            if (spaceJson != null) {
                SpaceInfoData spaceInfo = GsonUtil.fromJson(spaceJson.toString(), SpaceInfoData.class);
                if (spaceInfo != null) {
                    if (spaceInfo.sys_notice != null && spaceInfo.sys_notice.content != null) {
                        sys_notice = spaceInfo.sys_notice.content.replace("请点此查看纪念账号相关说明", "");
                    }
                    if (spaceInfo.live_room != null && spaceInfo.live_room.roomStatus == 1 && spaceInfo.live_room.liveStatus == 1) {
                        liveroom = new LiveRoom();
                        liveroom.title = "直播中：" + spaceInfo.live_room.title;
                        liveroom.user_cover = spaceInfo.live_room.cover;
                        liveroom.roomid = spaceInfo.live_room.roomid;
                    }
                    if (spaceInfo.contract != null) {
                        is_follow_display = spaceInfo.contract.is_follow_display;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        int is_senior_member = card.is_senior_member;
        if (card.vip != null && card.vip.status == 1) {
            UserInfo result = new UserInfo(mid, name, avatar, sign, data.follower, attention, level, data.following, notice, official, officialDesc, card.vip.role, sys_notice, liveroom, is_senior_member);
            result.vip_nickname_color = card.vip.nickname_color;
            result.is_follow_display = is_follow_display;
            return result;
        } else {
            UserInfo result = new UserInfo(mid, name, avatar, sign, data.follower, attention, level, data.following, notice, official, officialDesc, sys_notice, liveroom, is_senior_member);
            result.is_follow_display = is_follow_display;
            return result;
        }
    }

    public static org.json.JSONObject getUserSpaceInfo(long mid) throws JSONException, IOException {
        String url = "https://api.bilibili.com/x/space/wbi/acc/info?mid=" + mid;
        org.json.JSONObject all = NetWorkUtil.getJson(ConfInfoApi.signWBI(DmImgParamUtil.getDmImgParamsUrl(url)));
        return all.optJSONObject("data");
    }

    public static UserInfo getCurrentUserInfo() throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/space/myinfo";
        String json = NetWorkUtil.getJson(url).toString();
        ApiResponse<MyInfoData> resp = GsonUtil.fromJson(json,
                new com.google.gson.reflect.TypeToken<ApiResponse<MyInfoData>>(){}.getType());
        if (resp == null || !resp.isSuccess() || resp.data == null)
            return new UserInfo(0, "加载失败", "", "", 0, 0, 0, false, "", 0, "", 0);

        MyInfoData d = resp.data;
        int official = d.official != null ? d.official.role : 0;
        String officialDesc = d.official != null ? d.official.desc : "";
        long current_exp = d.level_exp != null ? d.level_exp.current_exp : 0;
        long next_exp = d.level_exp != null ? d.level_exp.next_exp : 0;
        return new UserInfo(d.mid, d.name, d.face, d.sign, d.follower, 0, d.level, false, "", official, officialDesc, current_exp, next_exp, d.is_senior_member);
    }

    public static int getCurrentUserCoin() {
        try {
            String json = NetWorkUtil.getJson("https://account.bilibili.com/site/getCoin").toString();
            ApiResponse<CoinData> resp = GsonUtil.fromJson(json,
                    new com.google.gson.reflect.TypeToken<ApiResponse<CoinData>>(){}.getType());
            return resp != null && resp.data != null ? resp.data.money : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static class CoinData {
        @SerializedName("money")
        public int money;
    }

    public static int getUserVideos(long mid, int page, String searchKeyword, List<VideoCard> videoList) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/space/wbi/arc/search?keyword=" + searchKeyword + "&mid=" + mid + "&order_avoided=true&order=pubdate&pn=" + page + "&ps=40&tid=0&web_location=333.999";
        String json = NetWorkUtil.getJson(ConfInfoApi.signWBI(DmImgParamUtil.getDmImgParamsUrl(url))).toString();
        ApiResponse<UserVideoData> resp = GsonUtil.fromJson(json,
                new com.google.gson.reflect.TypeToken<ApiResponse<UserVideoData>>(){}.getType());
        if (resp == null || resp.data == null || resp.data.list == null || resp.data.list.vlist == null) return -1;
        if (resp.data.list.vlist.isEmpty()) return 1;
        for (VListItem item : resp.data.list.vlist) {
            if (item == null) continue;
            videoList.add(new VideoCard(item.title, item.author, StringUtil.toWan(item.play) + "观看", item.pic, item.aid, item.bvid));
        }
        return 0;
    }

    public static int getUserArticles(long mid, int page, List<ArticleCard> articleList) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/space/wbi/article?mid=" + mid + "&order_avoided=true&order=pubdate&pn=" + page + "&ps=30&tid=0";
        String json = NetWorkUtil.getJson(ConfInfoApi.signWBI(url), NetWorkUtil.webHeaders).toString();
        ApiResponse<UserArticleData> resp = GsonUtil.fromJson(json,
                new com.google.gson.reflect.TypeToken<ApiResponse<UserArticleData>>(){}.getType());
        if (resp == null || resp.data == null || resp.data.articles == null || resp.data.articles.isEmpty()) return 1;
        for (ArticleItem item : resp.data.articles) {
            if (item == null) continue;
            ArticleCard card = new ArticleCard();
            card.id = item.id;
            card.title = item.title;
            card.view = StringUtil.toWan(item.stats != null ? item.stats.view : 0) + "阅读";
            card.cover = item.banner_url;
            card.upName = item.author != null ? item.author.name : "";
            articleList.add(card);
        }
        return 0;
    }

    public static int followUser(long mid, boolean isFollow) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/relation/modify?";
        String arg = "fid=" + mid + "&csrf=" + NetWorkUtil.getInfoFromCookie("bili_jct", SharedPreferencesUtil.getString(SharedPreferencesUtil.cookies, ""));
        arg += isFollow ? "&act=1" : "&act=2";
        return GsonUtil.fromJson(Objects.requireNonNull(NetWorkUtil.post(url, arg, NetWorkUtil.webHeaders).body()).string(), ApiResponse.class).code;
    }

    public static void exitLogin() {
        try { NetWorkUtil.get("https://passport.bilibili.com/login/exit/v2", NetWorkUtil.webHeaders); } catch (Exception ignored) {}
    }

    public static int addContract(long upMid) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/v1/contract/add_contract";
        String csrf = NetWorkUtil.getInfoFromCookie("bili_jct", SharedPreferencesUtil.getString(SharedPreferencesUtil.cookies, ""));
        String arg = "aid=&up_mid=" + upMid + "&source=4&scene=105&platform=web&mobi_app=pc&csrf=" + csrf;
        return GsonUtil.fromJson(Objects.requireNonNull(NetWorkUtil.post(url, arg, NetWorkUtil.webHeaders).body()).string(), ApiResponse.class).code;
    }

    public static org.json.JSONObject getMedalWall(long targetId) throws IOException, JSONException {
        org.json.JSONObject all = NetWorkUtil.getJson("https://api.live.bilibili.com/xlive/web-ucenter/user/MedalWall?target_id=" + targetId, NetWorkUtil.webHeaders);
        return all != null ? all.optJSONObject("data") : null;
    }

    public static org.json.JSONObject updateUserSign(String userSign) throws IOException, JSONException {
        String url = "https://api.bilibili.com/x/member/web/sign/update";
        String csrf = NetWorkUtil.getInfoFromCookie("bili_jct", SharedPreferencesUtil.getString(SharedPreferencesUtil.cookies, ""));
        String arg = "csrf=" + csrf;
        if (userSign != null) arg += "&user_sign=" + java.net.URLEncoder.encode(userSign, "UTF-8");
        return new org.json.JSONObject(Objects.requireNonNull(NetWorkUtil.post(url, arg, NetWorkUtil.webHeaders).body()).string());
    }
}
