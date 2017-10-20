package com.feiniu.score.common;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.dao.growth.SearchMemberDao;
import com.feiniu.score.dto.PartnerInfo;
import com.feiniu.score.entity.score.ScoreMember;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.fn.cache.client.RedisCacheClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("cacheUtils")
public class CacheUtils {
    private static final CustomLog log = CustomLog.getLogger(CacheUtils.class);
    @Autowired
    private RedisCacheClient cacheClient;

    @Autowired
    private SearchMemberDao searchMemberDao;

    /**
     * 消费积分
     */
    private static final String CONSUMESCORE = "score_consume_score_";

    /**
     * 获得积分
     */
    private static final String ORDERGETSCORE = "score_get_score_";

    /**
     * 是否是企业用户
     */
    private static final String SCORE_IS_COMPANY_USER = "score_is_company_user_";

    private static final String SCORE_IS_EMPLOYEE = "score_is_employee_user_";

    private static final String IS_COMPANY_USER_OR_PARTNER = "is_company_or_partner_";

    private static final String IS_TRADE_UNIONIST = "_is_trade_unionist";

    private static final String PARTNER_INFO = "_score_partner";

    private static final String MEMGUID_AVALIABLESCORE = "avaliablescore:";

    private static final String MEMGUID_LOCKEDANDEXPIRED = "lockedandexpired:";

    private static final String MEMGUID_USERSCOREDETAILLIST = "userscoredetaillist:";
    /**
     * 10天
     */
    public static final int TEN_DAY = 60 * 60 * 24 * 10;
    /**
     * 30天
     */
    public static final int THIRTY_DAY = 60 * 60 * 24 * 30;
    /**
     * 5天
     */
    public static final int FIVE_DAY = 60 * 60 * 24 * 5;
    /**
     * 1天
     */
    public static final int ONE_DAY = 60 * 60 * 24;
    /**
     * 1小时
     */
    public static final int ONE_HOUR = 60 * 60;

    /**
     * 3分钟
     */
    public static final int FIVE_MINUTES = 60 * 5;

    public String getCacheData(String key) {
        String value = null;
        try {
            value = cacheClient.get(key);
        } catch (Exception e) {
            log.error("从缓存中取数据失败。key=" + key,"getCacheData");
        }
        return value;
    }

    public void putCache(String key, int expire, Object value) {
        try {
            cacheClient.setex(key, expire, String.valueOf(value));
        } catch (Exception e) {
            log.error("把数据放入缓存中失败。key=" + key + "  value=" + value,"putCache");
        }
    }

    public boolean removeCacheData(String key) {
        boolean cacheState = true;
        try {
            cacheClient.remove(key);
        } catch (Exception e) {
            cacheState = false;
            log.error("把数据从缓存中删除失败。key=" + key,"removeCacheData");
        }
        return cacheState;
    }

    public boolean hSet(String key, String field, Object value) {
        boolean cacheState = true;
        try {
            cacheClient.hset(key, field, String.valueOf(value));
        } catch (Exception e) {
            cacheState = false;
            log.error("把数据放入缓存中失败。key=" + key + "field=" + field,"hSet");
        }
        return cacheState;
    }

    public String hGet(String key, String field) {
        String value = null;
        try {
            value = cacheClient.hget(key, field);
        } catch (Exception e) {
            log.error("把数据放入缓存中失败。key=" + key + "field=" + field,"hGet");
        }
        return value;
    }

    public boolean hDel(String key) {
        boolean cacheState = true;
        try {
            cacheClient.del(key);
        } catch (Exception e) {
            cacheState = false;
            log.error("把数据从缓存中删除失败。key=" + key,"hDel");
        }
        return cacheState;
    }

    public boolean isCompanyUser(String memGuid) {
        String key = SCORE_IS_COMPANY_USER + memGuid;
        //查缓存
        String flagStr = getCacheData(key);
        Boolean flag;
        if (StringUtils.isBlank(flagStr)) {
            Map<String, Object> resultMap = searchMemberDao.getMemberInfo(memGuid);
            flag = (Boolean) resultMap.get("isCompany");
            putCache(key, ONE_DAY, flag);
        } else {
            flag = Boolean.parseBoolean(flagStr);
        }
        return flag;
    }

    public boolean isEmployee(String memGuid) {
        String key = SCORE_IS_EMPLOYEE + memGuid;
        //查缓存
        String flagStr = getCacheData(key);
        Boolean flag;
        if (StringUtils.isBlank(flagStr)) {
            Map<String, Object> resultMap = searchMemberDao.getMemberInfo(memGuid);
            flag = (Boolean) resultMap.get("isEmployee");
            putCache(key, ONE_DAY, flag);
        } else {
            flag = Boolean.parseBoolean(flagStr);
        }
        return flag;
    }

    public Boolean isPartner(String memGuid) {
        return getIsPartnerInfo(memGuid).getIsPartner();
    }

    public PartnerInfo getIsPartnerInfo(String memGuid) {
        //缓存未存是否为合伙人
        //或者是合伙人但是缓存未存成为合伙人的时间

        String key = memGuid + PARTNER_INFO;
        PartnerInfo partnerInfo = null;
        try {
            String partnerInfoStr = getCacheData(key);
            if (StringUtils.isNotBlank(partnerInfoStr)) {
                partnerInfo = JSONObject.parseObject(partnerInfoStr, PartnerInfo.class);
            }
        } catch (Exception e) {
            log.error("从缓存中取用户合伙人信息异常","getIsPartnerInfo");
        }

        //是否合伙人为空或者是合伙人但是无成为合伙人的时间
        if (partnerInfo == null || partnerInfo.getIsPartner() == null || (partnerInfo.getIsPartner() && StringUtils.isBlank(partnerInfo.getBecomePartnerTime()))) {
            partnerInfo = searchMemberDao.getIsPartnerInfo(memGuid);
            if (partnerInfo != null) {
                if (partnerInfo.getIsPartner()) {
                    putCache(key, ONE_HOUR, JSONObject.toJSONString(partnerInfo));
                } else {
                    putCache(key, FIVE_MINUTES, JSONObject.toJSONString(partnerInfo));
                }
                return partnerInfo;
            } else {
                throw new ScoreException(ResultCode.GET_IS_PARTNER_ERROR, "查询用户合伙人信息失败");
            }
        } else {
            return partnerInfo;
        }
    }

    public boolean isCompanyOrPartner(String memGuid) {
        String key = IS_COMPANY_USER_OR_PARTNER + memGuid;
        //查缓存
        String flagStr = getCacheData(key);
        Boolean flag;
        if (StringUtils.isBlank(flagStr)) {
            Map<String, Object> resultMap = searchMemberDao.getMemberInfo(memGuid);
            Boolean isCompany = (Boolean) resultMap.get("isCompany");

            PartnerInfo partnerInfo = searchMemberDao.getIsPartnerInfo(memGuid);
            Boolean isPartner = partnerInfo.getIsPartner();

            flag = isPartner || isCompany;
            if (flag) {
                putCache(key, ONE_HOUR, flag);
            } else {
                putCache(key, FIVE_MINUTES, flag);
            }
        } else {
            flag = Boolean.parseBoolean(flagStr);
        }
        return flag;
    }

    public boolean isTradeUnionist(String memGuid) {
        String key = memGuid + IS_TRADE_UNIONIST;
        //查缓存
        String flagStr = getCacheData(key);
        Boolean flag;
        if (StringUtils.isBlank(flagStr)) {
            Map<String, Object> resultMap = searchMemberDao.getMemberInfo(memGuid);
            flag = (Boolean) resultMap.get("isTradeUnionist");
            if (flag) {
                putCache(key, ONE_HOUR, flag);
            } else {
                putCache(key, FIVE_MINUTES, flag);
            }
        } else {
            flag = Boolean.parseBoolean(flagStr);
        }
        return flag;
    }

    public void expire(String key, Integer seconds) {
        try {
            cacheClient.expire(key, seconds);
        } catch (Exception e) {
            log.error("设置缓存超时时间失败。key=" + key + "  seconds=" + seconds,"expire");
        }
    }

    public String getConsumeScoreKey(String key) {
        return CONSUMESCORE + key;
    }

    public String getGetScoreKey(String key) {
        return ORDERGETSCORE + key;
    }

    public void putConsumeScore(String key, Integer consumeScore) {
        putCache(getConsumeScoreKey(key), TEN_DAY, consumeScore);
    }

    public void putGetScore(String key, Integer getScore) {
        putCache(getGetScoreKey(key), TEN_DAY, getScore);
    }

    public void putAvaliableScore(String memGuid, Integer score) {
        putCache(MEMGUID_AVALIABLESCORE + memGuid, DateUtil.getSecondsUntilTomorrowZero().intValue(), score == null ? 0 : score);
    }

    public Integer getAvaliableScore(String memGuid) {
        String cacheData = getCacheData(MEMGUID_AVALIABLESCORE + memGuid);
        if (!StringUtils.isEmpty(cacheData)) {
            try {
                return Integer.parseInt(cacheData);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void removeAvaliableScore(String memGuid) {
        removeCacheData(MEMGUID_AVALIABLESCORE + memGuid);
    }


    public void putLockedAndExpired(String memGuid, ScoreMember scoreMember) {
        if (scoreMember == null) {
            scoreMember = new ScoreMember();
            scoreMember.setLockedScore(0);
            scoreMember.setExpiredScore(0);
        }
        putCache(MEMGUID_LOCKEDANDEXPIRED + memGuid, DateUtil.getSecondsUntilTomorrowZero().intValue(),
                JSONObject.toJSONString(scoreMember));

    }

    public ScoreMember getLockedAndExpired(String memGuid) {
        String cacheData = getCacheData(MEMGUID_LOCKEDANDEXPIRED + memGuid);
        if (!StringUtils.isEmpty(cacheData)) {
            return JSONObject.parseObject(cacheData, ScoreMember.class);
        }
        return null;
    }

    public void removeLockedAndExpired(String memGuid) {
        removeCacheData(MEMGUID_LOCKEDANDEXPIRED + memGuid);
    }


    public void putUserScoreDetailList(String memGuid, Map<String, Object> data) {
        putCache(MEMGUID_USERSCOREDETAILLIST + memGuid, 5 * 60,
                JSONObject.toJSONString(data));
    }

    public Map getUserScoreDetailList(String memGuid) {
        String cacheData = getCacheData(MEMGUID_USERSCOREDETAILLIST + memGuid);
        if (!StringUtils.isEmpty(cacheData)) {
            return JSONObject.parseObject(cacheData, Map.class);
        }
        return null;
    }

    public void removeUserScoreDetailList(String memGuid) {
        removeCacheData(MEMGUID_USERSCOREDETAILLIST + memGuid);
    }
}
