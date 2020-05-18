package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("接收到导入solr数据请求");
        if(message instanceof TextMessage){
            TextMessage textMessage=(TextMessage) message;

            try {
                String jsonStr = textMessage.getText();
                //转换json串为list集合
                List<TbItem> list = JSON.parseArray(jsonStr, TbItem.class);
                //遍历集合
                for(TbItem item :list){
                    System.out.println("title:"+item.getTitle());
                    //读取sku对应规格，转换成json对象
                    Map<String,Object> specMap=  JSON.parseObject(item.getSpec());
                    Map map=new HashMap();
                    //遍历规格map
                    for(String key :specMap.keySet()){
                        map.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
                    }

                    item.setSpecMap(map);
                }
                //保存sku集合到solr
                itemSearchService.importList(list);
                System.out.println("成功保存sku数据到solr");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
