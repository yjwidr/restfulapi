package com.netbrain.xf.flowengine.background;

import com.netbrain.xf.flowengine.queue.ITaskQueueManager;
import com.netbrain.xf.flowengine.queue.TaskRequest;
import com.netbrain.xf.flowengine.utility.DataCenterSwitching;
import com.netbrain.xf.flowengine.utility.HASupport;
import com.netbrain.xf.flowengine.workerservermanagement.UnackedXFTaskInfo;
import com.netbrain.xf.flowengine.daoinmemory.XFAgentInMemoryRepository;
import com.netbrain.xf.flowengine.workerservermanagement.XFAgentMetadata;
import com.netbrain.xf.model.XFAgent;
import com.netbrain.xf.model.XFTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.ArrayList;

@DisallowConcurrentExecution
public class XFTaskMaintenanceQuartzJob extends QuartzJobBean {

    private static Logger logger = LogManager.getLogger(XFTaskMaintenanceQuartzJob.class.getSimpleName());

    @Value("${background.xftaskmaintenance.task_unack_timelimit_in_seconds}")
    private int task_unack_timelimit_in_seconds;

    @Autowired
    private ITaskQueueManager taskQueueManager;

    @Autowired
    XFAgentInMemoryRepository xfAgentInMemoryRepository;

    @Autowired
    private HASupport haSupport;

    @Autowired
    private DataCenterSwitching dcSwitching;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        if(haSupport.isActive() && dcSwitching.isActiveDC()) {
            PerformXFTaskMaintenanceJob();
        }
        else{
            logger.debug("Noop in standby mode or inactive DC.");
        }
    }

    private void PerformXFTaskMaintenanceJob()
    {
        try {
            logger.warn("Flowengine is performaing XFTask maintenance action.");

            Map<String, XFAgentMetadata> xfagentmetadataMap = this.xfAgentInMemoryRepository.getXfagentserverMetadataHashMap();
            // For each XFAGent and for each unacked task if the timesince is long enough, resend the task and remove from the unacked list
            for (Map.Entry<String, XFAgentMetadata> entry : xfagentmetadataMap.entrySet()) {
                String serverName = entry.getKey();
                XFAgentMetadata agentMetadata = entry.getValue();

                ArrayList<String> taskIDsToRemove = new ArrayList<String>(8);
                Map<String, UnackedXFTaskInfo> unackedTasks = agentMetadata.getUnackedXFTaskHashMap();
                for (Map.Entry<String, UnackedXFTaskInfo> taskEntry : unackedTasks.entrySet()) {
                    String taskId = taskEntry.getKey();
                    UnackedXFTaskInfo taskInfoToResend = taskEntry.getValue();
                    Instant taskPublishTime = taskInfoToResend.getTimesttampPublish();
                    Instant instNow = Instant.now();
                    long taskUnackedForThatManySeconds = Duration.between(taskPublishTime, instNow).toSeconds();
                    if (taskUnackedForThatManySeconds > task_unack_timelimit_in_seconds)
                    {
                        XFTask taskToResend = taskInfoToResend.getXfTask();
                        if (taskToResend != null && !taskToResend.checkAndMarkLogged(XFTask.XFTASK_ACTIONLOG_ACK_TIMEOUT)) {
                            logger.info("***** PerformXFTaskMaintenanceJob detected task {} has not been acked by XFAgent for {} seconds, need to requeue it.", taskId, task_unack_timelimit_in_seconds);
                        } else {
                            logger.debug("***** PerformXFTaskMaintenanceJob detected task {} has not been acked by XFAgent for {} seconds, need to requeue it.", taskId, task_unack_timelimit_in_seconds);
                        }

                        TaskRequest taskReq = new TaskRequest(taskToResend, null);
                        taskQueueManager.enqueue(taskReq);
                        taskIDsToRemove.add(taskId);
                        agentMetadata.deleteOneUnackedXFTask(taskId);
                    }
                }

                // we should not delete the unackedTask, we only delete it when we receive something from XFAgent,
                // otherwise we are not able to record the resendtimes correctly.
            }

            Map<String, XFAgent> xfagentserverInMemoryHashMap = this.xfAgentInMemoryRepository.GetXfagentserverHashMap();
            return;
        }
        catch (Exception ex)
        {
            logger.warn("PerformXFTaskMaintenanceJob exception: ", ex);
        }
    }
}
