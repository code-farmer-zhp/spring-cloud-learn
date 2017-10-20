package com.feiniu.score.dao.score;

import com.feiniu.score.common.Constant;
import com.feiniu.score.entity.score.ScoreGrant;
import com.feiniu.score.entity.score.ScoreJobUnsuccessed;
import com.feiniu.score.entity.score.ScoreUse;
import com.feiniu.score.mapper.score.ScoreDefalutTableMapper;
import com.feiniu.score.vo.StoreReportInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ScoreDefalutTableDaoImpl implements ScoreDefalutTableDao {

    @Autowired
    private ScoreDefalutTableMapper scoreDefalutTableMapper;

    @Override
    public int saveFailureJobLog(ScoreJobUnsuccessed sju) {
        return scoreDefalutTableMapper.saveFailureJobLog(sju);
    }

    @Override
    public List<Map<String, Object>> loadScoreSum(Map<String, Object> mapParam) {
        return scoreDefalutTableMapper.loadScoreSum(mapParam);
    }

    @Override
    public List<ScoreJobUnsuccessed> getFialureJobLog(String memGuid, String message, Integer type) {
        return scoreDefalutTableMapper.getFialureJobLog(memGuid, message, type);
    }

    @Override
    public void handleFailMessage(String memGuid, String message, Integer type, String errorMsg) {
        //先查看是否已经有失败日志记录
        List<ScoreJobUnsuccessed> scoreJobUnsuccessedList = getFialureJobLog(memGuid, message, type);
        if (scoreJobUnsuccessedList == null || scoreJobUnsuccessedList.size() == 0) {
            //记录失败日志
            ScoreJobUnsuccessed sju = new ScoreJobUnsuccessed();
            sju.setMemGuid(memGuid);
            //未处理
            sju.setIsDeal(Constant.IS_DEAL_NOT);
            sju.setType(type);
            sju.setSrcType(0);
            sju.setMessage(message);
            sju.setErrorMessage(errorMsg);
            saveFailureJobLog(sju);
        } else {
            if (scoreJobUnsuccessedList.size() > 1) {
                //删除重复数据
                for (int i = 1; i < scoreJobUnsuccessedList.size(); i++) {
                    ScoreJobUnsuccessed scoreJobUnsuccessed = scoreJobUnsuccessedList.get(i);
                    scoreDefalutTableMapper.deleteByScuSeq(scoreJobUnsuccessed.getScuSeq());
                }
            }
            ScoreJobUnsuccessed scoreJobUnsuccessed = scoreJobUnsuccessedList.get(0);
            scoreDefalutTableMapper.updateErrorMsg(scoreJobUnsuccessed.getScuSeq(), errorMsg, type);
        }
    }

    @Override
    public List<ScoreJobUnsuccessed> getScoreCalUnsuccessedList(Map<String, Object> mapParam) {
        return scoreDefalutTableMapper.getScoreCalUnsuccessedList(mapParam);
    }

    @Override
    public int updateScoreCalUnsuccessedIsDel(String scuSeq, String isDeal) {
        return scoreDefalutTableMapper.updateScoreCalUnsuccessedIsDel(scuSeq, isDeal);
    }


    @Override
    public List<ScoreGrant> getScoreGrantDetail(Map<String, Object> paramMap) {
        return scoreDefalutTableMapper.getScoreGrantDetail(paramMap);
    }


    @Override
    public List<ScoreUse> getScoreUseDetail(Map<String, Object> paramMap) {
        return scoreDefalutTableMapper.getScoreUseDetail(paramMap);
    }

    @Override
    public Integer getScoreGrantDetailCount(Map<String, Object> paramMap) {
        return scoreDefalutTableMapper.getScoreGrantDetailCount(paramMap);
    }

    @Override
    public Integer getScoreUseDetailCount(Map<String, Object> paramMap) {
        return scoreDefalutTableMapper.getScoreUseDetailCount(paramMap);
    }

    @Override
    public Date getNow() {
        return scoreDefalutTableMapper.getNow();
    }

    @Override
    public List<StoreReportInfoVo> getStoreScoreReportInfo(String edate) {
        return scoreDefalutTableMapper.getStoreScoreReportInfo(edate);
    }

    @Override
    public List<String> getStoreNo(String table) {
        return scoreDefalutTableMapper.getStoreNo(table);
    }

    @Override
    public int delNoStore(String table, String key) {
        return scoreDefalutTableMapper.delNoStore(table, key);
    }
}
