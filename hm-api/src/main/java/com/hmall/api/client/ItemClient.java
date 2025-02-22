package com.hmall.api.client;



import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.api.fallback.ItemClientFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "item-service",fallback = ItemClientFallbackFactory.class)
public interface ItemClient {

    @GetMapping("/item")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);


    @PutMapping("/item/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> items);

}
