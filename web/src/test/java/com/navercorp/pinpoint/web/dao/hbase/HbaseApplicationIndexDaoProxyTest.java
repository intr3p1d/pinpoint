package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexPerTimeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class HbaseApplicationIndexDaoProxyTest {

    @Mock
    private ApplicationIndexDao applicationIndexDao;

    @Mock
    private ApplicationIndexPerTimeDao applicationIndexPerTimeDao;


    @Test
    public void selectAllApplicationNamesTest() {
        doReturn(Collections.emptyList()).when(applicationIndexDao).selectAllApplicationNames();
        HbaseApplicationIndexDaoProxy hbaseApplicationIndexDaoProxy = new HbaseApplicationIndexDaoProxy(applicationIndexDao, applicationIndexPerTimeDao);

        hbaseApplicationIndexDaoProxy.selectAllApplicationNames();
        verify(applicationIndexDao, times(1)).selectAllApplicationNames();
    }

    @Test
    public void selectApplicationNameTest() {
        doReturn(Collections.emptyList()).when(applicationIndexDao).selectApplicationName("");
        HbaseApplicationIndexDaoProxy hbaseApplicationIndexDaoProxy = new HbaseApplicationIndexDaoProxy(applicationIndexDao, applicationIndexPerTimeDao);

        hbaseApplicationIndexDaoProxy.selectApplicationName("");
        verify(applicationIndexDao, times(1)).selectApplicationName("");
    }

    @Test
    public void selectAgentIdsTest() {
        doReturn(Collections.emptyList()).when(applicationIndexDao).selectAgentIds("");
        doReturn(Collections.emptyList()).when(applicationIndexPerTimeDao).selectAgentIds("", Range.between(0, Long.MAX_VALUE));
        HbaseApplicationIndexDaoProxy hbaseApplicationIndexDaoProxy = new HbaseApplicationIndexDaoProxy(applicationIndexDao, applicationIndexPerTimeDao);

        hbaseApplicationIndexDaoProxy.setUseIndexPerTime(false);
        hbaseApplicationIndexDaoProxy.selectAgentIds("");
        verify(applicationIndexDao, times(1)).selectAgentIds("");

        hbaseApplicationIndexDaoProxy.setUseIndexPerTime(true);
        hbaseApplicationIndexDaoProxy.selectAgentIds("");
        verify(applicationIndexPerTimeDao, times(1)).selectAgentIds("", Range.between(0, Long.MAX_VALUE));
    }
}
