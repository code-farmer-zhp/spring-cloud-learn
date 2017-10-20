package com.feiniu.score.service;

import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dao.score.ScoreOrderDetailDao;
import com.feiniu.score.exception.ScoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class ScoreLoadOlServiceImpl implements ScoreLoadOlService {

    private ExecutorService service = Executors.newFixedThreadPool(10);

    @Autowired
    private ScoreOrderDetailDao scoreOrderDetailDao;

    @Override

    public List<Map<String, Object>> loadOlScoreByType(final String memGuid, Map<Integer, List<Map<String, Object>>> typeMap) {
        if (typeMap.size() > 1) {
            List<Future<List<Map<String, Object>>>> futures = new ArrayList<>();
            //异步
            for (Map.Entry<Integer, List<Map<String, Object>>> entry : typeMap.entrySet()) {
                final Integer type = entry.getKey();
                final List<Map<String, Object>> value = entry.getValue();
                Future<List<Map<String, Object>>> future = service.submit(new Callable<List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call() throws Exception {
                        return scoreOrderDetailDao.loadOlScoreByType(memGuid, type, value);
                    }
                });
                futures.add(future);
            }
            List<Map<String, Object>> result = new ArrayList<>();
            for (Future<List<Map<String, Object>>> future : futures) {
                try {
                    result.addAll(future.get());
                } catch (Exception e) {
                    throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询异常", e);
                }
            }
            return result;
        } else {
            //同步
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<Integer, List<Map<String, Object>>> entry : typeMap.entrySet()) {
                final Integer type = entry.getKey();
                final List<Map<String, Object>> value = entry.getValue();
                result.addAll(scoreOrderDetailDao.loadOlScoreByType(memGuid, type, value));
            }
            return result;
        }
    }
}
