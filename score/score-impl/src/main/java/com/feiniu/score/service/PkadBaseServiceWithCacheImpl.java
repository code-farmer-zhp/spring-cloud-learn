package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.common.ConstantCache;
import com.feiniu.score.dao.mrst.PkadDao;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yue.teng on 2016/7/15.
 */
@Service
public class PkadBaseServiceWithCacheImpl implements PkadBaseServiceWithCache {
    @Autowired
    private PkadDao pkadDao;
    @Autowired
    private CacheUtils cacheUtils;
    @Override
    public int savePkad(String membId, Pkad Pkad) {
        int flag= pkadDao.savePkad(membId,Pkad);
        cacheUtils.hDel(membId + ConstantCache.GET_PKAD);
        return flag;
    }
    @Override
    public List<Pkad> getPkadListBySelective(String membId, Map<String, Object> paramMap, boolean withCache){
        if(!withCache){
            return pkadDao.getPkadListBySelective(membId, paramMap);
        }else {
            //空取出来为[]
            String pkadListCache = cacheUtils.hGet(membId + ConstantCache.GET_PKAD, "list_" + JSONObject.toJSONString(paramMap));
            if (StringUtils.isNotBlank(pkadListCache)) {
                return JSONArray.parseArray(pkadListCache, Pkad.class);
            }else{
                List<Pkad> pkadList= pkadDao.getPkadListBySelective(membId, paramMap);
                cacheUtils.hSet(membId + ConstantCache.GET_PKAD, "list_" + JSONObject.toJSONString(paramMap), JSONArray.toJSONString(pkadList));
                cacheUtils.expire(membId + ConstantCache.GET_PKAD, DateUtil.getSecondsUntilTomorrowZero().intValue());
                return pkadList;
            }
        }
    }

    @Override
    public int getPkadCountBySelective(String membId, Map<String, Object> paramMap, boolean withCache){
        if(!withCache){
            return pkadDao.getPkadCountBySelective(membId, paramMap);
        }else {
            String pkadCountCache = cacheUtils.hGet(membId + ConstantCache.GET_PKAD, "count_" + JSONObject.toJSONString(paramMap));
            if (StringUtils.isNotBlank(pkadCountCache)) {
                return Integer.parseInt(pkadCountCache);
            }else{
                int count=pkadDao.getPkadCountBySelective(membId, paramMap);
                cacheUtils.hSet(membId + ConstantCache.GET_PKAD, "count_" + JSONObject.toJSONString(paramMap),count);
                cacheUtils.expire(membId + ConstantCache.GET_PKAD, DateUtil.getSecondsUntilTomorrowZero().intValue());
                return count;
            }
        }
    }
    @Override
    public int updatePkad(String membId, Pkad Pkad){
        int flag= pkadDao.updatePkad(membId, Pkad);
        cacheUtils.hDel(membId + ConstantCache.GET_PKAD);
        return flag;
    }

    @Override
    public Set<String> getMrstUisBySelective(String membId, Map<String, Object> paramMap, boolean withCache){
        if(!withCache){
            return pkadDao.getMrstUisBySelective(membId, paramMap);
        }else {
            String mrstUiListCache = cacheUtils.hGet(membId + ConstantCache.GET_PKAD, "mrstUi_list_" + JSONObject.toJSONString(paramMap));
            if (StringUtils.isNotBlank(mrstUiListCache)) {
                List<String> mrstUiArr=JSONArray.parseArray(mrstUiListCache, String.class);
                Set<String> mrstUiSet=new HashSet<>();
                for(String mrstUiStr:mrstUiArr){
                    mrstUiSet.add(mrstUiStr);
                }
                return mrstUiSet;
            }else{
                Set<String> mrstUiSet=pkadDao.getMrstUisBySelective(membId, paramMap);
                cacheUtils.hSet(membId + ConstantCache.GET_PKAD, "mrstUi_list_" + JSONObject.toJSONString(paramMap), JSONArray.toJSONString(mrstUiSet));
                cacheUtils.expire(membId + ConstantCache.GET_PKAD, DateUtil.getSecondsUntilTomorrowZero().intValue());
                return mrstUiSet;
            }
        }
    }

    @Override
    public Pkad getPkadByPkadIdAndMembId(String membId, String pkadId, String membGradeF){
        return pkadDao.getPkadByPkadIdAndMembId(membId, pkadId, membGradeF);
    }
    @Override
    public Pkad getPkadByPkadIdAndMembIdForUpdate(String membId, String pkadId, String membGradeF){
        return pkadDao.getPkadByPkadIdAndMembIdForUpdate(membId, pkadId, membGradeF);
    }

    @Override
    public Pkad getPkadByPkadSeqAndMembId(String membId, String pkadSeq) {
        return pkadDao.getPkadByPkadSeqAndMembId(membId, pkadSeq);
    }
    @Override
    public List<Pkad> getPkadsByPkadSeqsAndMembId(String membId, Set pkadSeqs) {
        return pkadDao.getPkadsByPkadSeqsAndMembId(membId, pkadSeqs);
    }

    @Override
    public Pkad getPkadByPkadSeqAndMembIdForUpdate(String membId, String pkadSeq) {
        return  pkadDao.getPkadByPkadSeqAndMembIdForUpdate(membId, pkadSeq);
    }

    @Override
    public List<String> getTakeCardNoByMembId(String membId) {
        return pkadDao.getTakeCardNoByMembId(membId);
    }

    @Override
    public String getLastPkadMrsuUi(String membId, boolean withCache){
        if(!withCache){
            Pkad pkad=pkadDao.getLastPkad(membId);
            if(pkad!=null&&pkad.getMrstUi()!=null&&pkad.getMrstUiName()!=null){
                return pkad.getMrstUi()+"_"+pkad.getMrstUiName();
            }else{
                return ConstantCache.NONE_STRING;
            }
        }else {
            String getLastPkadMrsuUiAndMrstUiName = cacheUtils.hGet(membId + ConstantCache.GET_PKAD, "Last_Libao_MrstUi_MrstUiName_"+DateUtil.getFormatDate(new Date(),"yyyy-MM-dd"));
            if(ConstantCache.NONE_STRING.equals(getLastPkadMrsuUiAndMrstUiName)){
                return null;
            }else if (StringUtils.isNotBlank(getLastPkadMrsuUiAndMrstUiName)) {
                return getLastPkadMrsuUiAndMrstUiName;
            }else{
                Pkad pkad = pkadDao.getLastPkad(membId);
                getLastPkadMrsuUiAndMrstUiName=(pkad!=null&&pkad.getMrstUi()!=null&&pkad.getMrstUiName()!=null)?pkad.getMrstUi()+"_"+pkad.getMrstUiName():ConstantCache.NONE_STRING;
                String cacheValue;
                //为空也记录到缓存
                if(StringUtils.isBlank(getLastPkadMrsuUiAndMrstUiName)){
                    cacheValue=ConstantCache.NONE_STRING;
                }else{
                    cacheValue=getLastPkadMrsuUiAndMrstUiName;
                }
                cacheUtils.hSet(membId + ConstantCache.GET_PKAD, "Last_Libaokey_pkad_"+DateUtil.getFormatDate(new Date(),"yyyy-MM-dd"),cacheValue);
                cacheUtils.expire(membId + ConstantCache.GET_PKAD, DateUtil.getSecondsUntilTomorrowZero().intValue());
                return getLastPkadMrsuUiAndMrstUiName;
            }
        }
    }
}
