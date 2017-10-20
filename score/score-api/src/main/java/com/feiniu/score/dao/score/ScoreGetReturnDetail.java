package com.feiniu.score.dao.score;

import com.feiniu.score.vo.ReturnJsonVo;

/**
 * 获取退单详细
 */
public interface ScoreGetReturnDetail {
    ReturnJsonVo getReturnDetail(String memGuid,String rgSeq,String rssSeq);
}
