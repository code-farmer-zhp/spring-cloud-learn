package com.feiniu.score.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.common.Constant;
import com.feiniu.score.common.ConstantCache;
import com.feiniu.score.dao.growth.GrowthMainDao;
import com.feiniu.score.entity.growth.GrowthMain;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.vo.OrderJsonVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by yue.teng on 2016/7/15.
 */
@Service
public class GrowthBaseServiceWithCacheImpl implements GrowthBaseServiceWithCache {
    public static final CustomLog log = CustomLog.getLogger(GrowthBaseServiceWithCacheImpl.class);
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private GrowthMainDao growthMainDao;
    @Override
    public GrowthMain getGrowthMainByMemGuid(String memGuid, boolean withCache) {
        if(!withCache){
            return growthMainDao.getGrowthMainByMemGuid(memGuid);
        }else{
            String gmCache = cacheUtils.getCacheData(memGuid + ConstantCache.GROWTH_MAIN);
            if (StringUtils.isNotBlank(gmCache)) {
                try {
                    return JSONObject.parseObject(gmCache, GrowthMain.class);
                }catch (Exception e){
                    log.error("从缓存中取growth_main数据转化失败。gmCache=" + gmCache,"getGrowthMainByMemGuid");
                    GrowthMain gm=growthMainDao.getGrowthMainByMemGuid(memGuid);
                    cacheUtils.putCache(memGuid + ConstantCache.GROWTH_MAIN,300,JSONObject.toJSONString(gm));
                    return gm;
                }
            } else {
                GrowthMain gm=growthMainDao.getGrowthMainByMemGuid(memGuid);
                cacheUtils.putCache(memGuid + ConstantCache.GROWTH_MAIN,300,JSONObject.toJSONString(gm));
                return gm;
            }
        }
    }

    @Override
    public int saveGrowthMain(String memGuid, GrowthMain gm, boolean cleanCache) {
        if(!cleanCache){
            return growthMainDao.saveGrowthMain(memGuid, gm);
        }else{
            int returnRow=growthMainDao.saveGrowthMain(memGuid, gm);
            cacheUtils.removeCacheData(memGuid + ConstantCache.GROWTH_MAIN);
            return returnRow;
        }
    }

    @Override
    public int updateGrowthMain(String memGuid, GrowthMain gm, boolean cleanCache) {
        if(!cleanCache){
            return growthMainDao.updateGrowthMain(memGuid, gm);
        }else{
            int returnRow=growthMainDao.updateGrowthMain(memGuid, gm);
            cacheUtils.removeCacheData(memGuid + ConstantCache.GROWTH_MAIN);
            return returnRow;
        }
    }

    @Override
    public int saveGrowthValueWithValueZero(String memGuid, boolean cleanCache) {
        if(!cleanCache){
            return growthMainDao.saveGrowthValueWithValueZero(memGuid);
        }else{
            int returnRow=growthMainDao.saveGrowthValueWithValueZero(memGuid);
            cacheUtils.removeCacheData(memGuid + ConstantCache.GROWTH_MAIN);
            return returnRow;
        }
    }

    @Override
    public int changeGrowthValue(String memGuid, int changedGrowthValue, boolean cleanCache) {
        if(!cleanCache){
            return growthMainDao.changeGrowthValue(memGuid, changedGrowthValue);
        }else{
            int returnRow=growthMainDao.changeGrowthValue(memGuid, changedGrowthValue);
            cacheUtils.removeCacheData(memGuid + ConstantCache.GROWTH_MAIN);
            return returnRow;
        }
    }

    @Override
    public boolean canGetGrowth(OrderJsonVo vo) {
        Integer virtual = vo.getVirtual();
        Integer sourceMode = vo.getSourceMode();
        String siteMode = vo.getSiteMode();
        if (StringUtils.isNotBlank(vo.getAdrId())
                || StringUtils.isNotBlank(vo.getGroupId())
                || (virtual != null && virtual == 1)
                || StringUtils.equals(siteMode, Constant.ELECTRONIC_SCREEN)
                || Objects.equals(sourceMode, Constant.DISTRIBUTION_PLATFORM)) {
            log.info("计算成长值：因为是分销商户或电子屏订单或CPS订单或组团订单或虚拟充值订单，获得成长值为0。memGuid=" + vo.getMemGuid() + ",ogSeq=" + vo.getOgSeq(),"canGetGrowth");
            return false;
        } else {
            return true;
        }
    }
}
