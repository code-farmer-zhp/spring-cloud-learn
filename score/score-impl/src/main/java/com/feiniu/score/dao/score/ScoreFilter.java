package com.feiniu.score.dao.score;

import com.feiniu.score.vo.ProductInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 过滤掉不能使用积分，和不能得到积分的商品
 */
@Service
public class ScoreFilter {

    @Autowired
    private ScoreWhetherUsePoint scoreWhetherUsePoint;

    public Map<String, Boolean> getCanUseScoreInfo(List<ProductInfo.OrderScoreInfo> allList) {
        Set<String> parentIds = new HashSet<>();
        Set<String> itNos = new HashSet<>();
        for (ProductInfo.OrderScoreInfo scoreInfo : allList) {
            parentIds.add(scoreInfo.getParentId());
            itNos.add(scoreInfo.getItNo());
        }

        StringBuilder sb = new StringBuilder();
        for (String itNo : itNos) {
            sb.append(itNo).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        //可以使用积分的
        Map<String, Boolean> booleanMap = scoreWhetherUsePoint.getWhetherCanUsePoint(sb.toString());

        Map<String, Boolean> notCanUseScoreMap = new HashMap<>();
        for (String parentId : parentIds) {
            for (ProductInfo.OrderScoreInfo scoreInfo : allList) {
                if (StringUtils.equals(parentId, scoreInfo.getParentId())) {
                    String itNo = scoreInfo.getItNo();
                    //获得不可以使用积分的parentId
                    Boolean canUser = booleanMap.get(itNo);
                    if (canUser != null && !canUser) {
                        notCanUseScoreMap.put(parentId, true);
                        break;
                    }

                }
            }
        }
        return notCanUseScoreMap;
    }
}
