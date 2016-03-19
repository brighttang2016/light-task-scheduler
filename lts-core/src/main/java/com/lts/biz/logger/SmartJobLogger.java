package com.lts.biz.logger;

import com.lts.admin.response.PaginationRsp;
import com.lts.biz.logger.domain.JobLogPo;
import com.lts.biz.logger.domain.JobLoggerRequest;
import com.lts.core.AppContext;
import com.lts.core.cluster.Config;
import com.lts.core.constant.Constants;
import com.lts.core.spi.ServiceLoader;

import java.util.List;

/**
 * 内部根据用户参数决定是否采用延迟批量刷盘的策略,来提高吞吐量
 *
 * @author Robert HG (254963746@qq.com) on 10/2/15.
 */
public class SmartJobLogger implements JobLogger {

    private JobLogger delegate;

    public SmartJobLogger(AppContext appContext) {
        Config config = appContext.getConfig();
        JobLoggerFactory jobLoggerFactory = ServiceLoader.load(JobLoggerFactory.class, config);
        JobLogger jobLogger = jobLoggerFactory.getJobLogger(config);
        if (config.getParameter(Constants.LAZY_JOB_LOGGER, false)) {
            this.delegate = new LazyJobLogger(appContext, jobLogger);
        } else {
            this.delegate = jobLogger;
        }
    }

    @Override
    public void log(JobLogPo jobLogPo) {
        this.delegate.log(jobLogPo);
    }

    @Override
    public void log(List<JobLogPo> jobLogPos) {
        this.delegate.log(jobLogPos);
    }

    @Override
    public PaginationRsp<JobLogPo> search(JobLoggerRequest request) {
        return this.delegate.search(request);
    }
}
