package com.brin.denonremotefree.Interface;

/**
 * Created by Luca on 05.06.2016.
 */
public interface ReceiverConnectionInterface
{
    int BROAD_CON_WAIT = 0, BROAD_CON_SUCC = 1,BROAD_CON_DIS = 2,BROAD_MSG = 3,BROAD_COM_SUCC = 4,BROAD_COM_DIS = 5,BROAD_MULTI_MSG = 6,BROAD_NSE_RES = 7;
    int CONN_EXCP_TIME = 0, CONN_EXCP_REFUSE = 2, CONN_EXCP_UNREACH = 3, CONN_EXCP_CLOSED = 4, CONN_EXCP_UNKNOWN = 5,CONN_EXCP_TEST = 6,CONN_EXCP_PEER = 7;
}
