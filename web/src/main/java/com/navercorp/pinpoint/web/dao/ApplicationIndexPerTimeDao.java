package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.web.vo.Application;

import java.util.List;
import java.util.Map;

public interface ApplicationIndexPerTimeDao {

    List<String> selectAgentIds(String applicationName, Range range);

}
