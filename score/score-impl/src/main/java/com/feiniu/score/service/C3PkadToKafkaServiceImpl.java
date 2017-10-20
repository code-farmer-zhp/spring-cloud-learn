package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.ProducerClient;
import com.feiniu.score.dao.mrst.PkadDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.vo.JobResultVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/1/24 11:07 
*/
@Service
public class C3PkadToKafkaServiceImpl extends AbstractScoreJobService implements C3PkadToKafkaService{
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Log log= LogFactory.getLog(C3PkadToKafkaServiceImpl.class);
    @Value("${fn.topic.pkad.taken}")
    private String pkadTakenTopic;
    @Autowired
    private PkadDao pkadDao;
    @Autowired
    @Qualifier("producerClient")
    private ProducerClient<Object, String> producerClient;


    public boolean saveTakePkadTokafkaForCRM(Pkad pkad, JSONArray cardInfo){
        boolean isSendSuccess=false;
        if(pkad!=null){
            String  dateTaken = null  ;//领取日期
            String  timeTaken = null;//领取时间
            String ddTake=pkad.getDdTake();
            try {
                SimpleDateFormat sdfTime=new SimpleDateFormat("HHmmss");
                SimpleDateFormat sdfDate=new SimpleDateFormat("yyyyMMdd");
                timeTaken=sdfTime.format(ddTake);
                dateTaken=sdfDate.format(ddTake);
            } catch (Exception e) {
                log.error("error：日期格式转换失败 takenDate:"+ddTake);
            }
            Map<String, Object> info = new HashMap<>();
            info.put("memb_id", pkad.getMembId());
            info.put("pkad_id", pkad.getPkadId());
            info.put("memb_grade_f", pkad.getMembGradeF());
            info.put("d_taken", dateTaken);
            info.put("t_taken", timeTaken);
            info.put("is_taken", "T");
            if(cardInfo!=null&&!cardInfo.isEmpty()){
                info.put("card_info", cardInfo);
            }
            String message = JSONObject.toJSONString(info);
            try {
                long keys = System.currentTimeMillis();
                producerClient.sendMessage(pkadTakenTopic,String.valueOf(keys)+"_takePkad", message);
                log.info("礼包领取消息发送成功:"+message);
                isSendSuccess=true;
            } catch (Exception e) {
                log.error("礼包领取消息发送失败。message=" + message);
            }
        }else{
            log.error("礼包为空");
        }
    return isSendSuccess;
    }

    private void populateReturnSeqToCardInfo(JSONArray cardInfoArr,String combinedTakenSeq) {
        //将返回的卡号重新包装入cardInfo字符串中，用来发送kafka。
        JSONObject returnSeqJson=null;
        try {
            if(!StringUtils.isEmpty(combinedTakenSeq)){
                returnSeqJson = JSONObject.parseObject(combinedTakenSeq);
            }
        }catch(Exception e){
            throw new RuntimeException(",combinedTakenSeq parse to JSON error!");
        }
        if(returnSeqJson!=null) {
            for(int j=0;j<cardInfoArr.size();j++){
                JSONObject cardInfo=cardInfoArr.getJSONObject(j);
                JSONArray cardList=cardInfo.getJSONArray("card_list");
                for(int k=0;k<cardList.size();k++){
                    JSONObject card=cardList.getJSONObject(k);
                    String cardSequence=card.getString("card_seq");
                    // if cardSequence exists ,populate "" or returnseq to return cardinfo
                    if(!StringUtils.isEmpty(cardSequence)){
                        card.put("card_seq_rtn",StringUtils.isEmpty(returnSeqJson.getString(cardSequence))?"":returnSeqJson.getString(cardSequence));
                    }
                }
            }
//			}
        }

    }

    @Override
    public JobResultVo processOneTable(String dataSourceName, int tableNo) {
        JobResultVo tableResultVo=new JobResultVo();
        log.info("get C3 pkads from table : dataBase-->"+dataSourceName+" , tableNo-->"+tableNo);
        // 设置连接的数据库
        DataSourceUtils.setCurrentKey(dataSourceName);
        List<Pkad> pkads= pkadDao.getTakenC3Between(tableNo,"2016-12-01 10:33:00",sdf.format(new Date()));
        log.info("sending C3 cards info from to kafka for crm");
        for(int j=0;j<pkads.size();j++){
            Pkad pkad=pkads.get(j);
            JSONArray cardInfoArray=JSONArray.parseArray(pkad.getCardInfo());
            populateReturnSeqToCardInfo(cardInfoArray ,pkad.getTakeCardSeq());
            log.info("before sending-->pkad seq:"+pkad.getPkadSeq()+"msg:"+cardInfoArray.toJSONString());
            boolean isSendSuccess=saveTakePkadTokafkaForCRM(pkad,cardInfoArray);
            if(isSendSuccess){
                tableResultVo.addSuccessNum();
            }else{
                tableResultVo.addFailureNum();
            }
        }
        return tableResultVo;
    }

    @Override
    public void executeJob() {
        JobResultVo resultVo=new JobResultVo();
        try {
            log.info("C3Pkad Job start...");
            super.processJob(resultVo,true);
            log.info("C3Pkad Job end .");
            log.info(resultVo.getPrintString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
