package com.feiniu;

import com.feiniu.tms.bean.warehouse.SpeedyDeliveryReq;
import com.feiniu.tms.bean.warehouse.SpeedyDeliveryResp;
import com.feiniu.tms.bean.warehouse.WarehouseDeliveryReq;
import com.feiniu.tms.bean.warehouse.WarehouseDeliveryResp;
import com.feiniu.tms.interfaces.warehouse.ISpeedyDeliveryService;
import com.feiniu.tms.interfaces.warehouse.IWarehouseDeliveryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressGeocodesApplicationTests {

    @Autowired
    private ISpeedyDeliveryService speedyDeliveryService;

    @Autowired
    private IWarehouseDeliveryService warehouseDeliveryService;

    @Test
    public void testGetWarehouseDeliveryService() {
        Map<String, SpeedyDeliveryReq> map = new HashMap<>();
        SpeedyDeliveryReq req = new SpeedyDeliveryReq();
        req.setAreaSeq("321203");
        req.setLongitude("120.043683");
        req.setLatitude("32.393796");

        map.put("1", req);

        Map<String, SpeedyDeliveryResp> speedyDelivery = speedyDeliveryService.getSpeedyDelivery(map);
        System.out.println("batch" + speedyDelivery);

        WarehouseDeliveryReq warehouseDeliveryReq = new WarehouseDeliveryReq();
        warehouseDeliveryReq.setLat("31.185435");
        warehouseDeliveryReq.setLng("121.451803");
        List<WarehouseDeliveryResp> warehouseDeliveryResps = this.warehouseDeliveryService.getWarehouseDeliveryService(warehouseDeliveryReq);
        System.out.println(warehouseDeliveryResps.get(0));
    }

    @Test
    public void contextLoads() {
    }

}
