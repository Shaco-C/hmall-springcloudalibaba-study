package com.hmall.api.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "支付单业务传输实体")
public class PayOrderDTO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务订单号")
    private Long bizOrderNo;

    @ApiModelProperty("支付单号")
    private String payOrderNo;

    @ApiModelProperty("支付用户Id")
    private Long bizUserId;

    @ApiModelProperty("支付渠道编码")
    private String payChannelCode;

    @ApiModelProperty("支付金额")
    private Integer amount;

    @ApiModelProperty("支付类型")
    private Integer payType;

    @ApiModelProperty("支付状态")
    private Integer payStatus;

    @ApiModelProperty("扩展信息")
    private String expandJson;

    @ApiModelProperty("支付结果码")
    private String resultCode;

    @ApiModelProperty("支付结果信息")
    private String resultMsg;

    @ApiModelProperty("支付成功时间")
    private LocalDateTime paySuccessTime;

    @ApiModelProperty("支付超时时间")
    private LocalDateTime payOverTime;

    @ApiModelProperty("支付二维码地址")
    private String qrCodeUrl;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;


}
