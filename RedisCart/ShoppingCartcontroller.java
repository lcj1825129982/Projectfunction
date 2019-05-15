package com.qhit.ShoppingCart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qhit.goodsInfo.pojo.GoodsInfo;
import com.qhit.utils.RedisUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by lenovo on 2019/1/16.
 */
@RestController
@RequestMapping("/ShoppingCart")
public class ShoppingCartcontroller {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    Gson gson;

    @RequestMapping("/addCart")
    public Boolean addCart(GoodsInfo goodsInfo, HttpSession session){
        //新建一个购物车Map;
        Map<Integer,GoodsInfo> map=null;
        Integer userid = (Integer) session.getAttribute("baseUserId");//获取用户id
        System.out.println(userid);
        //判断该用户是否拥有购物车
        boolean isHasUserid=  redisUtils.hasKey(userid.toString());
        if (!isHasUserid){
            //用户在redis里没有购物车   则新建一个购物车
            map=new Hashtable<>();
            map.put(goodsInfo.getGid(),goodsInfo);//然后添加商品信息
            String s = gson.toJson(map); //把购物车转成json
            redisUtils.set(userid.toString(),s); //redis添加json key:userid value:json

        }else{
            //用户在redis中有购物车
            Object cartinfojson = redisUtils.get(userid.toString());//从redis中获取信息
            Object cartinfo = gson.fromJson(cartinfojson.toString(), new TypeToken<Map<Integer, GoodsInfo>>() {
            }.getType()); //把redis中的json转成我们需要的map;
            map= (Map<Integer, GoodsInfo>) cartinfo;
            //遍历购物车，处理购物车信息
            for(Map.Entry<Integer,GoodsInfo> entry:map.entrySet()){
                if (entry.getKey().equals(goodsInfo.getGid())){  //判断商品id是否一致
                    entry.getValue().setCount(goodsInfo.getCount());//商品id一样时  更改count值
                }
            }
        }
        return true;

    }
    @RequestMapping("/delCart")
    public Boolean delCart(HttpSession session,Integer shopid){
        //查询userID然后删除shopid的对象删除
        Integer userid = (Integer) session.getAttribute("baseUserId"); //获取用户id
        try {
            Object cartinfojson = redisUtils.get(userid.toString());//通过userid获取购物车信息
            Object cartinfo = gson.fromJson(cartinfojson.toString(), new TypeToken<Map<Integer, GoodsInfo>>() {

            }.getType());  //将购物车的信息转为map cartinfojson可能为空抓取异常
            Map<Integer, GoodsInfo> map = new Hashtable<>(); // new map用当做数据容器
            map = (Map<Integer, GoodsInfo>) cartinfo;
            if (map.get(shopid) != null) { //判断是否有这个商品有就删除
                map.remove(shopid);
                String s = gson.toJson(map); //删除后的购物车重新存放到redis中
                redisUtils.set(userid + "", s);
                return true;

            } else {
                return false; //没有就return false
            }
        }catch (Exception e){
            return false;
        }

    }

    @RequestMapping("/getCart")
    public String getCart(Integer userId){
        String cartinfojson = (String) redisUtils.get(userId + "");
        return cartinfojson;
    }








}
