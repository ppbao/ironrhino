package org.ironrhino.batch.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ironrhino.batch.job.JobInfo;
import org.ironrhino.batch.job.JobInstanceInfo;
import org.ironrhino.batch.job.JobParameterHelper;
import org.ironrhino.batch.job.SimpleJobParameter;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter.ParameterType;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JobAction extends BaseAction {

	private static final long serialVersionUID = 3379095323379034989L;

	private static Logger logger = LoggerFactory.getLogger(JobAction.class);

	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private JobOperator jobOperator;

	private List list;

	private Job job;

	private JobInstance jobInstance;

	private JobExecution jobExecution;

	private String jobParameters;

	private List<SimpleJobParameter> params;

	private EnumSet<ParameterType> paramTypes;

	private ResultPage<JobInstanceInfo> resultPage;

	public List getList() {
		return list;
	}

	public Job getJob() {
		return job;
	}

	public JobInstance getJobInstance() {
		return jobInstance;
	}

	public JobExecution getJobExecution() {
		return jobExecution;
	}

	public String getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(String jobParameters) {
		this.jobParameters = jobParameters;
	}

	public List<SimpleJobParameter> getParams() {
		return params;
	}

	public void setParams(List<SimpleJobParameter> params) {
		this.params = params;
	}

	public EnumSet<ParameterType> getParamTypes() {
		return paramTypes;
	}

	public ResultPage<JobInstanceInfo> getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage<JobInstanceInfo> resultPage) {
		this.resultPage = resultPage;
	}

	public String execute() throws Exception {
		Set<String> names = new TreeSet<>(jobRegistry.getJobNames());
		list = new ArrayList<>();
		for (String name : names) {
			JobInfo info = new JobInfo();
			Job job = jobRegistry.getJob(name);
			info.setName(job.getName());
			String description = getText(info.getName() + ".description");
			if (!description.endsWith(".description"))
				info.setDescription(description);
			info.setIncrementable(job.getJobParametersIncrementer() != null);
			info.setRestartable(job.isRestartable());
			info.setLaunchable(true);
			List<Long> instanceIds = jobOperator.getJobInstances(name, 0, 1);
			if (!instanceIds.isEmpty()) {
				List<Long> executionIds = jobOperator.getExecutions(instanceIds.get(0));
				if (!executionIds.isEmpty()) {
					info.setLastExecution(jobExplorer.getJobExecution(executionIds.get(0)));
				}
			}
			list.add(info);
		}
		Set<String> namesInRepository = new TreeSet<>(jobExplorer.getJobNames());
		for (String name : namesInRepository) {
			if (names.contains(name))
				continue;
			JobInfo info = new JobInfo();
			info.setName(name);
			String description = getText(info.getName() + ".description");
			if (!description.endsWith(".description"))
				info.setDescription(description);
			list.add(info);
		}
		return LIST;
	}

	@InputConfig(methodName = "inputLaunch")
	public String launch() throws Exception {
		String jobName = getUid();
		Job job = jobRegistry.getJob(jobName);
		JobParametersIncrementer jobParametersIncrementer = job.getJobParametersIncrementer();
		if (jobParametersIncrementer == null) {
			JobParametersValidator jobParametersValidator = job.getJobParametersValidator();
			List<SimpleJobParameter> paramList;
			if (params != null && !params.isEmpty())
				paramList = JobParameterHelper.parse(jobParametersValidator, params);
			else
				paramList = JobParameterHelper.parse(jobParameters);
			JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
			for (SimpleJobParameter param : paramList) {
				switch (param.getType()) {
				case LONG:
					jobParametersBuilder.addLong(param.getKey(), Long.valueOf(param.getValue()), param.isRequired());
					break;
				case DOUBLE:
					jobParametersBuilder.addDouble(param.getKey(), Double.valueOf(param.getValue()),
							param.isRequired());
					break;
				case DATE:
					jobParametersBuilder.addDate(param.getKey(), DateUtils.parseDate10(param.getValue()),
							param.isRequired());
					break;
				default:
					jobParametersBuilder.addString(param.getKey(), param.getValue(), param.isRequired());
				}
			}
			JobParameters jobParameters = jobParametersBuilder.toJobParameters();
			String jobParametersString = JobParameterHelper.toString(jobParameters);
			logger.info("Try launch job {} with {}", jobName, jobParametersString);
			try {
				JobExecution je = jobLauncher.run(job, jobParameters);
				String message = getText("launch.job", new String[] { getText(jobName),
						DateUtils.formatDatetime(je.getCreateTime()), jobParametersString });
				logger.info(message);
				addActionMessage(message);
			} catch (JobExecutionException e) {
				addActionError(getText(e.getClass().getName()));
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.info("Try launch job {} with parameters incrementer {}", jobName, jobParametersIncrementer);
			try {
				JobExecution je = jobExplorer.getJobExecution(jobOperator.startNextInstance(jobName));
				String jobParametersString = JobParameterHelper.toString(je.getJobParameters());
				String message = getText("launch.job", new String[] { getText(jobName),
						DateUtils.formatDatetime(je.getCreateTime()), jobParametersString });
				logger.info(message);
				addActionMessage(message);
			} catch (JobExecutionException e) {
				addActionError(getText(e.getClass().getName()));
				logger.error(e.getMessage(), e);
			}
		}
		return SUCCESS;
	}

	public String inputLaunch() throws Exception {
		String jobName = getUid();
		job = jobRegistry.getJob(jobName);
		JobParametersIncrementer jobParametersIncrementer = job.getJobParametersIncrementer();
		if (jobParametersIncrementer == null) {
			JobParametersValidator jobParametersValidator = job.getJobParametersValidator();
			if (jobParametersValidator instanceof DefaultJobParametersValidator)
				paramTypes = EnumSet.allOf(ParameterType.class);
			params = JobParameterHelper.parse(jobParametersValidator, params);
		}
		return "launch";
	}

	public String instances() throws Exception {
		if (resultPage == null)
			resultPage = new ResultPage<>();
		String jobName = getUid();
		int count = jobExplorer.getJobInstanceCount(jobName);
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<JobInstance> instances = jobExplorer.getJobInstances(jobName, resultPage.getStart(),
					resultPage.getPageSize());
			List<JobInstanceInfo> list = new ArrayList<>(instances.size());
			for (JobInstance instance : instances) {
				JobInstanceInfo info = new JobInstanceInfo();
				info.setId(instance.getId());
				info.setJobName(instance.getJobName());
				List<Long> executionIds = jobOperator.getExecutions(instance.getInstanceId());
				info.setExecutionCount(executionIds.size());
				if (!executionIds.isEmpty())
					info.setLastExecution(jobExplorer.getJobExecution(executionIds.get(0)));
				list.add(info);
			}
			resultPage.setResult(list);
		} else {
			resultPage.setResult(Collections.<JobInstanceInfo>emptyList());
		}
		return "instances";
	}

	public String executions() {
		Long instanceId = Long.valueOf(getUid());
		jobInstance = jobExplorer.getJobInstance(instanceId);
		list = jobExplorer.getJobExecutions(jobExplorer.getJobInstance(instanceId));
		return "executions";
	}

	public String steps() {
		Long executionId = Long.valueOf(getUid());
		jobExecution = jobExplorer.getJobExecution(executionId);
		jobInstance = jobExecution.getJobInstance();
		return "steps";
	}

	public String stop() throws Exception {
		Long executionId = Long.valueOf(getUid());
		jobOperator.stop(executionId);
		addActionMessage(getText("operate.success"));
		return SUCCESS;
	}

	public String restart() throws Exception {
		Long executionId = Long.valueOf(getUid());
		jobOperator.restart(executionId);
		return abandon();
	}

	public String abandon() throws Exception {
		Long executionId = Long.valueOf(getUid());
		jobOperator.abandon(executionId);
		addActionMessage(getText("operate.success"));
		return SUCCESS;
	}

}
