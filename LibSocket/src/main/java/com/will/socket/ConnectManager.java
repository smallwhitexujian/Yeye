package com.will.socket;//package com.will.socket;
//
//import com.framework.socket.factory.SocketModuleManager;
//import com.framework.socket.factory.SocketModuleManagerImpl;
//import com.framework.socket.model.TcpSocketConnectorConfig;
//import com.framework.socket.out.Selector;
//import com.framework.socket.out.TcpSocketCallback;
//import com.framework.socket.out.TcpSocketConnectorCallback;
//import com.framework.socket.protocol.Protocol;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 单例
// * fun:socket 连接管理
// *
// *
// *
// */
//public class ConnectManager {
//
//    private final int DELAY = 1000;
//    private final int RETRYTIME = 5;
//    private final int PERIOD = 10000;
//    private Map<String,SocketModuleManager> mMap = new HashMap<>();
//
//
//    private Protocol mProtocol;
//    private Selector mSelector;
//    private TcpSocketConnectorCallback mConnectorCallback;
//    private TcpSocketCallback mSocketCallback;
//    private TcpSocketConnectorConfig mConnectorConfig;
//
//
//    /**
//     *
//     * @param protocol 协议
//     * @param selector socket 信息 选择器
//     * @param connectorHandler 连接器处理
//     * @param businessHandle 业务处理
//     */
//    public ConnectManager(Protocol protocol,Selector selector,TcpSocketConnectorCallback connectorHandler,TcpSocketCallback businessHandle){
//        this.mProtocol = protocol;
//        this.mSelector = selector;
//        this.mConnectorCallback = connectorHandler;
//        this.mSocketCallback = businessHandle;
//
//        mConnectorConfig = new TcpSocketConnectorConfig();
//        mConnectorConfig.setLaucherDelay(DELAY);
//        mConnectorConfig.setPeriod(PERIOD);
//        mConnectorConfig.setMaxRetrayTime(RETRYTIME);
//
//    }
//
//
//    //添加连接
//    public void connect(String key){
//        if(key == null){
//            return;
//        }
//
//        SocketModuleManager value = mMap.get(key);
//        if(value == null){
//            value = new SocketModuleManagerImpl(mConnectorConfig,mProtocol, mSelector);
////            value.startSocket();
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//}
