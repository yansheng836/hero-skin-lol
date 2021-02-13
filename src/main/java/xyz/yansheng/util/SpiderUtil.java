package xyz.yansheng.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import xyz.yansheng.bean.Hero;

/**
 * 工具类
 * 
 * @author yansheng
 * @date 2019/09/30
 */
public class SpiderUtil {

    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";

    /**
     * 获取英雄列表
     */
    public static ArrayList<Hero> getHeros(String url, String encoding) {

        Document doc = null;

        // 判断是读取本地HTML还是在线爬取
        if (url.contains("./")) {
            // 本地HTML
            String html = null;
            try {
                html = FileUtils.readFileToString(new File(url), encoding);
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc = Jsoup.parse(html);
        } else {
            try {
                doc = Jsoup.parse(new URL(url).openStream(), encoding, url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // System.out.println(doc);

        // div herolist-content ul.herolist.clearfix li
        Elements liElements = doc.select("ul.herolist.clearfix li");
        // System.out.println(liElements);

        int size = liElements.size();

        ArrayList<Hero> heros = new ArrayList<Hero>(size);
        // 后面出的英雄在最前面，进行逆序统计id
        for (int i = 0; i < size; i++) {

            Hero hero = new Hero();
            hero.setId(size - i);
            Element liElement = liElements.get(i);
            Element aElement = liElement.selectFirst("a");
            if (liElement != null) {
                // 英雄主页网址heroUrl
                String heroUrl = aElement.attr("href");
                hero.setHeroUrl(heroUrl);

                // 英雄ename
                String ename = heroUrl.substring(heroUrl.lastIndexOf('/') + 1);
                ename = ename.substring(0, 3);
                hero.setEname(Integer.parseInt(ename));

                // 英雄cname
                String cname = aElement.text();
                hero.setCname(cname);
            }
            heros.add(hero);
        }
        return heros;
    }

    /**
     * 根据英雄的信息（主要是英雄介绍页网址），获得皮肤名称字符串（逆序）和皮肤列表（有序）。
     * 
     * @param hero
     *            含有HeroUrl的英雄。
     * @return Hero 在原有基础上，添加了skinName和skins字段的英雄。
     */
    public static Hero getHeroSkins(Hero hero) {

        String url = hero.getHeroUrl();

        List<String> skins1 = new ArrayList<String>(5);

        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(url).openStream(), GBK, url);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // 皮肤列表
        // <ul class="pic-pf-list pic-pf-list3" data-imgname="幻纱之灵|归虚梦演">
        // (2019年11月22日)发现有变化了：幻纱之灵&0|归虚梦演&0，需要去除多余的字符串
        Elements liElements = doc.select("ul.pic-pf-list");

        // 英雄skinName,skins
        String skinName = liElements.attr("data-imgname");
        // System.out.println(skinName);
        // 去除皮肤名中多余的字符串
        skinName = skinName.replaceAll("&\\d+", "");
        String[] skinsArray = skinName.split("\\|");
        skins1 = Arrays.asList(skinsArray);

        hero.setSkinName(skinName);
        hero.setSkins(skins1);

        return hero;
    }

    /**
     * 获取LOL英雄列表
     */
    public static ArrayList<Hero> getLolHeros(String url, String encoding) {

        Document doc = null;

        // 判断是读取本地HTML还是在线爬取
        if (url.contains("./")) {
            // 本地HTML
            String html = null;
            try {
                html = FileUtils.readFileToString(new File(url), encoding);
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc = Jsoup.parse(html);
        } else {
            try {
                doc = Jsoup.parse(new URL(url).openStream(), encoding, url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Elements liElements = doc.select("ul.imgtextlist li");
        // System.out.println(liElements);

        int size = liElements.size();

        ArrayList<Hero> heros = new ArrayList<Hero>(size);
        // 后面出的英雄在最前面，进行逆序统计id
        for (int i = 0; i < size; i++) {

            Hero hero = new Hero();
            hero.setId(i + 1);
            Element liElement = liElements.get(i);
            // System.out.println(liElement);

            Element aElement = liElement.selectFirst("a");
            if (liElement != null) {
                // 英雄主页网址heroUrl
                String heroUrl = aElement.attr("href");
                hero.setHeroUrl(heroUrl);

                // 英雄ename
                String ename = heroUrl.substring(heroUrl.lastIndexOf('=') + 1);

                hero.setEname(Integer.parseInt(ename));

                // 英雄cname
                // String cname = aElement.text();
                String cname = aElement.attr("title");
                hero.setCname(cname);
            }
            heros.add(hero);
        }
        return heros;
    }

    /**
     * 根据英雄的信息（主要是英雄介绍页网址），获得皮肤名称字符串（逆序）和皮肤列表（有序）。
     *
     * @param hero
     *            含有HeroUrl的英雄。
     * @return Hero 在原有基础上，添加了skinName和skins字段的英雄。
     */
    public static Hero getLolHeroSkins(Hero hero) {

        String url = hero.getHeroUrl();
//        System.out.println("url:" + url);

        List<String> skins1 = new ArrayList<String>(16);

        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(url).openStream(), GBK, url);
            // System.out.println("doc:"+doc);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // 皮肤列表
        // <ul class="pic-pf-list pic-pf-list3" data-imgname="幻纱之灵|归虚梦演">
        // (2019年11月22日)发现有变化了：幻纱之灵&0|归虚梦演&0，需要去除多余的字符串
        Elements liElements = doc.select("ul#skinNAV li"); // 延迟加载，爬取不了
        System.out.println("liElements:" + liElements);
        // liElements = liElements.select("a");

        int size = liElements.size();
        StringBuffer skinname = new StringBuffer();
        for (int i = 0; i < size; i++) {
            Element liElement = liElements.get(i);
            System.out.println("liElement:" + liElement);
            skinname = skinname.append(liElement.select("a").attr("title"));

        }
        System.out.println("skinname:" + skinname);

        // 英雄skinName,skins
        String skinName = liElements.select("a").attr("title");
        System.out.println("skinName:" + skinName);
        // 去除皮肤名中多余的字符串
        skinName = skinName.replaceAll("&\\d+", "");
        String[] skinsArray = skinName.split("\\|");
        skins1 = Arrays.asList(skinsArray);

        hero.setSkinName(skinName);
        hero.setSkins(skins1);

        return hero;
    }

    public static Hero getLolHeroSkins2(Hero hero) {

        // https://game.gtimg.cn/images/lol/act/img/js/hero/1.js
        String url = "https://game.gtimg.cn/images/lol/act/img/js/hero/" + hero.getEname() + ".js";
//        System.out.println("url:" + url);

        Document doc = null;
        JSONArray jsonArray = null;
        JSONObject obj = null;
        try {
            doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            // System.out.println(doc.text());
            // JSONObject.parseObject 自动将Unicode字符串进行解码
            obj = JSONObject.parseObject(doc.text());
            // System.out.println("obj:" + obj);
            // System.out.println("obj.skins:" + obj.get("skins"));
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        jsonArray = (JSONArray)obj.get("skins");
        String skinName = "";
        String skinId = "";
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject)object;
            // System.out.println("jsonObject:" + jsonObject);
            // 用于判断皮肤是否有效，0有效，1无效
            // System.out.println("jsonObject.get(\"chromas\"):" + jsonObject.get("chromas"));
            if ("0".equals(jsonObject.get("chromas"))) {
                skinName = skinName + jsonObject.get("name") + "|";
                skinId = skinId + jsonObject.get("skinId") + "|";
            }
        }

        String[] nameArray = skinName.split("\\|");
        String[] idArray = skinId.split("\\|");
        List<String> skins;
        List<String> skinIds;
        skins = Arrays.asList(nameArray);
        skinIds = Arrays.asList(idArray);

        // System.out.println("skins.size():" + skins.size() + ",skins:" + skins);
        // System.out.println("skinIds.size():" + skinIds.size() + ",skinIds:" + skinIds);

        hero.setSkinName(skinName);
        hero.setSkins(skins);
        hero.setSkinId(skinIds);

        return hero;
    }
}
