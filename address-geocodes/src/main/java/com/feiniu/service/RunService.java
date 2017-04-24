package com.feiniu.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.dao.AddressDao;
import com.feiniu.dto.GeoDTO;
import com.feiniu.entity.Address;
import com.feiniu.entity.GeoEntity;
import com.feiniu.entity.Relation;
import com.feiniu.tms.bean.warehouse.WarehouseDeliveryReq;
import com.feiniu.tms.bean.warehouse.WarehouseDeliveryResp;
import com.feiniu.tms.interfaces.warehouse.ISpeedyDeliveryService;
import com.feiniu.tms.interfaces.warehouse.IWarehouseDeliveryService;
import com.feiniu.util.Security3DesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RunService {

    private static final Log log = LogFactory.getLog(RunService.class);

    private static final String url = "http://restapi.amap.com/v3/geocode/geo?";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private IWarehouseDeliveryService service;

    @Value("${key}")
    private String key;

    @Value("${pageSize}")
    private int pageSize;

    /**
     * 每天限量请求地图api多少次
     */
    @Value("${limit}")
    private int limit;

    private int count = 0;

    private volatile boolean canRun = false;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public void start() {
        int start = getStart();
        List<Address> list = addressDao.getList(start, start + pageSize);
        while (list.size() > 0) {
            buildDatas(list);
            limitJudge();
            start += pageSize;
            list = addressDao.getList(start, start + pageSize);
        }
        log.info("数据全部跑完");
        System.exit(0);

    }

    private void buildDatas(List<Address> list) {
        List<GeoDTO> geoDTOS = new ArrayList<>();
        List<GeoEntity> entities = new ArrayList<>();
        for (Address address : list) {
            String longitude = address.getLongitude();
            String latitude = address.getLatitude();
            if (StringUtils.isEmpty(longitude) && StringUtils.isEmpty(latitude)) {
                GeoDTO geo = getGeo(buildParam(address));
                if (geo != null) {
                    geo.setMemGuid(address.getMemGuid());
                    geo.setAreaCode(address.getAreaCode());
                    geoDTOS.add(geo);

                    GeoEntity entity = new GeoEntity();
                    entity.setLongitude(geo.getLongitude());
                    entity.setLatitude(geo.getLatitude());
                    entity.setId(address.getId());
                    entities.add(entity);
                }
            } else {
                GeoDTO geoDTO = new GeoDTO(longitude, latitude);
                geoDTO.setMemGuid(address.getMemGuid());
                geoDTO.setAreaCode(address.getAreaCode());
                geoDTOS.add(geoDTO);
            }
        }
        try {
            saveWarehouseCode(geoDTOS);
            updateGeo(entities);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void limitJudge() {
        if (count >= limit) {
            log.info("今天已经到达请求限制。count:" + count + "   limit:" + limit);
            canRun = false;
            count = 0;
            await();
        }
    }

    private String buildParam(Address address) {
        StringBuilder addStr = new StringBuilder();
        String province = Security3DesUtils.decrypt(address.getProvince());
        addStr.append(province);
        String city = Security3DesUtils.decrypt(address.getCity());
        addStr.append(city);
        String area = Security3DesUtils.decrypt(address.getArea());
        addStr.append(area);
        String townRaw = address.getTown();
        if (StringUtils.isNotEmpty(townRaw)) {
            String town = Security3DesUtils.decrypt(townRaw);
            addStr.append(town);
        }
        addStr.append(Security3DesUtils.decrypt(address.getAddress()));
        return addStr.toString();
    }

    private void updateGeo(List<GeoEntity> entities) {
        if (entities.size() > 0) {
            addressDao.update(entities);
        }
    }

    private void saveWarehouseCode(List<GeoDTO> geoDTOS) {
        List<Relation> relations = new ArrayList<>();
        for (GeoDTO geoDTO : geoDTOS) {
            WarehouseDeliveryReq req = new WarehouseDeliveryReq();
            req.setLng(geoDTO.getLongitude());
            req.setLat(geoDTO.getLatitude());
            List<WarehouseDeliveryResp> list = service.getWarehouseDeliveryService(req);
            log.info("info:" + list);
            if (list.size() > 0) {
                Relation relation = new Relation();
                relation.setMemGuid(geoDTO.getMemGuid());
                relation.setWarehouseCode(list.get(0).getWarehouseCode());
                relation.setInsTime(new Date());
                relation.setUpTime(new Date());

                relations.add(relation);
            }
        }
        if (relations.size() > 0) {
            try {
                addressDao.saveRelation(relations);
            } catch (Exception e) {
                //可能存在重复数据了
                for (Relation relation : relations) {
                    try {
                        addressDao.saveRelation(Collections.singletonList(relation));
                    } catch (Exception ex) {
                        //重复数据进行更新
                        addressDao.updateRelation(relation);
                    }
                }
            }
        }

    }

    private void await() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1);
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        long timeInMillis = instance.getTimeInMillis() + 5000;
        //第二天零晨唤醒继续执行
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                canRun = true;
                synchronized (RunService.class) {
                    RunService.class.notifyAll();
                }
            }
        }, timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        //等待
        synchronized (RunService.class) {
            while (!canRun) {
                try {
                    RunService.class.wait();
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }
    }

    private int getStart() {
        File file = new File("schedule");
        if (file.exists()) {
            try {
                FileInputStream fileStream = new FileInputStream(file);
                InputStreamReader inputStream = new InputStreamReader(fileStream, "UTF-8");
                Scanner scanner = new Scanner(inputStream);
                try {
                    String startLine = scanner.nextLine();
                    return Integer.parseInt(startLine.trim());
                } finally {
                    scanner.close();
                    inputStream.close();
                    fileStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("读取文件错误", e);
            }
        }
        return 0;
    }

    private GeoDTO getGeo(String address) {
        String param = "address=" + address + "&key=" + key + "&output=JSON";
        for (int i = 0; i < 3; i++) {
            try {
                count++;
                String rep = restTemplate.getForObject(url + param, String.class);
                JSONObject json = JSONObject.parseObject(rep);
                String status = json.getString("status");
                if ("1".equals(status)) {
                    JSONArray geocodes = json.getJSONArray("geocodes");
                    JSONObject geo = geocodes.getJSONObject(0);
                    String location = geo.getString("location");
                    String[] split = location.split(",");
                    //经度
                    String longitude = split[0];
                    //纬度
                    String latitude = split[1];

                    return new GeoDTO(longitude, latitude);
                }
            } catch (Exception e) {
                log.error("请求高德地图异常。i=" + i, e);
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e1) {
                    //ignore
                }
            }
        }
        return null;
    }
}
