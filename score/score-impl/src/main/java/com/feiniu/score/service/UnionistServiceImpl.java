package com.feiniu.score.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.mrst.CardDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by yue.teng on 2016-09-26.
 */
@Service
public class UnionistServiceImpl implements UnionistService {
    @Autowired
    private CardDao cardDao;

    @Override
    public boolean unionBindSendBonus(String message){
        JSONObject jsonObj = JSONObject.parseObject(message);
        String type = jsonObj.getString("type");
        if (StringUtils.equals(type, "10")) {
            JSONObject info = jsonObj.getJSONObject("info");
            String memGuid = info.getString("MEM_GUID");
            String tpLoginType = info.getString("OPEN_TYPE");
            String status = info.getString("STATUS");//0绑定 1解绑
            Date date = info.getDate("MEM_OPERATION_TIME");
            if("0".equals(status)) {
                return cardDao.takeBonusForUnionist(memGuid, tpLoginType,date, message, Constant.UNIONIST_BIND_SEND_BONUS);
            }
        }
        return true;
    }

    @Override
    public boolean unionRegSendBonus(String message){
        JSONObject jsonObj = JSONObject.parseObject(message);
        String type = jsonObj.getString("type");
        if (StringUtils.equals(type, "2")) {
            JSONObject info = jsonObj.getJSONObject("info");
            String memGuid = info.getString("memGuid");
            String tpLoginType = info.getString("tpLoginType");
            Date date = info.getDate("regTime");
            return cardDao.takeBonusForUnionist(memGuid,tpLoginType,date,message, Constant.UNIONIST_REGISTER_SEND_BONUS);
        }
        return true;
    }
}
