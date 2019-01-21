package com.xaeport.crossborder.generated;

import com.xaeport.crossborder.configuration.AppConfiguration;
import com.xaeport.crossborder.convert.bondinven.BaseBondInvenXML;
import com.xaeport.crossborder.convert.delivery.BaseDeliveryXML;
import com.xaeport.crossborder.convert.exitbondinvt.EBaseBondInvtXML;
import com.xaeport.crossborder.convert.exitpassport.EBasePassPortXML;
import com.xaeport.crossborder.convert.inventory.BaseDetailDeclareXML;
import com.xaeport.crossborder.convert.logistics.BaseLogisticsXml;
import com.xaeport.crossborder.convert.logstatus.BaseLogisticsStatusXml;
import com.xaeport.crossborder.convert.manifest.BaseManifestXML;
import com.xaeport.crossborder.convert.order.BaseOrderXml;
import com.xaeport.crossborder.convert.payment.BasePaymentXml;
import com.xaeport.crossborder.data.mapper.*;
import com.xaeport.crossborder.generated.thread.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by lzy on 2019-01-04
 * 报文生成启动器
 */
@Component
public class GeneratedAutoLauncher implements ApplicationListener<ApplicationReadyEvent> {

    private Log logger = LogFactory.getLog(this.getClass());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    //订单线程
    private OrderMessageThread orderMessageThread;
    //支付单线程
    private PaymentMessageThread paymentMessageThread;
    //物流运单线程
    private LogisticsMessageThread logisticsMessageThread;
    //物流运单状态线程
    private LogisticsStatusMessageThread logisticsStatusMessageThread;
    //入库明细单数据线程
    private DeliveryDataThread deliveryDataThread;
    //入库明细单报文线程
    private DeliveryMessageThread deliveryMessageThread;
    //跨境清单线程
    private DetailDeclareMessageThread detailDeclareMessageThread;
    //跨境核放单线程
    private ManifestManageThread manifestManageThread;
    //保税清单线程
    private BondInvenMessageThread bondInvenMessageThread;
    //出区核注清单线程
    private EBondInvtThread eBondInvtThread;
    //出区核放单线程
    private EPassPortThread ePassPortThread;

    //订单
    @Autowired
    OrderDeclareMapper orderDeclareMapper;
    //支付单
    @Autowired
    PaymentDeclareMapper paymentDeclareMapper;
    //物流运单
    @Autowired
    WaybillDeclareMapper waybillDeclareMapper;
    //入库明细单
    @Autowired
    DeliveryDeclareMapper deliveryDeclareMapper;
    //跨境清单
    @Autowired
    DetailDeclareMapper detailDeclareMapper;
    //跨境核放单
    @Autowired
    ManifestManageMapper manifestManageMapper;
    //保税清单
    @Autowired
    BondinvenDeclareMapper bondinvenDeclareMapper;
    //出区核注清单
    @Autowired
    ExitInventoryMapper exitInventoryMapper;
    //出区核放单
    @Autowired
    ExitManifestMapper exitManifestMapper;

    //订单报文
    @Autowired
    BaseOrderXml baseOrderXml;
    //支付单报文
    @Autowired
    BasePaymentXml basePaymentXml;
    //物流运单报文
    @Autowired
    BaseLogisticsXml baseLogisticsXml;
    //物流运单状态报文
    @Autowired
    BaseLogisticsStatusXml baseLogisticsStatusXml;
    //入库明细单报文
    @Autowired
    BaseDeliveryXML baseDeliveryXML;
    //跨境清单报文
    @Autowired
    BaseDetailDeclareXML baseDetailDeclareXML;
    //跨境核放单报文
    @Autowired
    BaseManifestXML baseManifestXML;
    //保税清单报文
    @Autowired
    BaseBondInvenXML baseBondInvenXML;
    //出区核注清单报文
    @Autowired
    EBaseBondInvtXML eBaseBondInvtXML;
    //出区核放单报文
    @Autowired
    EBasePassPortXML eBasePassPortXML;

    //系统配置文件参数
    @Autowired
    AppConfiguration appConfiguration;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        this.logger.debug("跨境订单报文CEB311生成启动器初始化开始");
        orderMessageThread = new OrderMessageThread(this.orderDeclareMapper, this.appConfiguration, this.baseOrderXml);
        executorService.execute(orderMessageThread);

        this.logger.debug("跨境支付单报文CEB411生成启动器初始化开始");
        paymentMessageThread = new PaymentMessageThread(this.paymentDeclareMapper, this.appConfiguration, this.basePaymentXml);
        executorService.execute(paymentMessageThread);

        this.logger.debug("跨境运单报文CEB511生成启动器初始化开始");
        logisticsMessageThread = new LogisticsMessageThread(this.waybillDeclareMapper, this.appConfiguration, this.baseLogisticsXml);
        executorService.execute(logisticsMessageThread);

        this.logger.debug("跨境运单状态报文CEB513生成启动器初始化开始");
        logisticsStatusMessageThread = new LogisticsStatusMessageThread(this.waybillDeclareMapper, this.appConfiguration, this.baseLogisticsStatusXml);
        executorService.execute(logisticsStatusMessageThread);

        this.logger.debug("跨境入库明细单数据生成启动器初始化开始");
        deliveryDataThread = new DeliveryDataThread(this.deliveryDeclareMapper);
        executorService.execute(deliveryDataThread);

        this.logger.debug("跨境入库明细单报文CEB711生成启动器初始化开始");
        deliveryMessageThread = new DeliveryMessageThread(this.deliveryDeclareMapper, this.appConfiguration, this.baseDeliveryXML);
        executorService.execute(deliveryMessageThread);

        this.logger.debug("跨境清单报文CEB621生成启动器初始化开始");
        detailDeclareMessageThread = new DetailDeclareMessageThread(this.detailDeclareMapper, this.appConfiguration, this.baseDetailDeclareXML);
        executorService.execute(detailDeclareMessageThread);

        this.logger.debug("跨境核放单报文CebManifest生成启动器初始化开始");
        manifestManageThread = new ManifestManageThread(this.manifestManageMapper, this.appConfiguration, this.baseManifestXML);
        executorService.execute(manifestManageThread);

        this.logger.debug("保税清单报文生成启动器初始化开始");
        bondInvenMessageThread = new BondInvenMessageThread(this.bondinvenDeclareMapper, this.appConfiguration, this.baseBondInvenXML);
        executorService.execute(bondInvenMessageThread);

        this.logger.debug("进口出区核注清单报文生成启动器初始化开始");
        eBondInvtThread = new EBondInvtThread(this.exitInventoryMapper, this.appConfiguration, this.eBaseBondInvtXML);
        executorService.execute(eBondInvtThread);

        this.logger.debug("进口出区核放单报文生成启动器初始化开始");
        ePassPortThread = new EPassPortThread(this.exitManifestMapper, this.appConfiguration, this.eBasePassPortXML);
        executorService.execute(ePassPortThread);

    }
}
